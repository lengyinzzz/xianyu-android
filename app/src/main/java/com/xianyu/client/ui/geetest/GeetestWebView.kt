package com.xianyu.client.ui.geetest

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.xianyu.client.data.model.GeetestResult
import com.xianyu.client.network.RetrofitClient
import org.json.JSONObject

/**
 * 用 WebView 套一层极验滑动验证。
 * 流程：加载 HTML → 调后端 /api/v1/geetest/register → initGeetest →
 * 用户滑动 → 调 /validate → 通过 JS 回调把结果交给原生。
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GeetestWebView(
    onSuccess: (GeetestResult) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseUrl = remember { RetrofitClient.getBaseUrl().trimEnd('/') }

    AndroidView(
        modifier = modifier.fillMaxWidth().height(220.dp),
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // 允许 http 内网后端
                settings.blockNetworkImage = false

                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onGeetestSuccess(json: String) {
                        try {
                            val obj = JSONObject(json)
                            val result = GeetestResult(
                                challenge = obj.optString("challenge"),
                                validate = obj.optString("validate"),
                                seccode = obj.optString("seccode")
                            )
                            post { onSuccess(result) }
                        } catch (e: Exception) {
                            post { onError(e.message ?: "解析验证结果失败") }
                        }
                    }

                    @JavascriptInterface
                    fun onGeetestError(msg: String) {
                        post { onError(msg.ifBlank { "验证失败" }) }
                    }
                }, "AndroidBridge")

                webViewClient = WebViewClient()
                loadDataWithBaseURL(
                    baseUrl,
                    buildHtml(baseUrl),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { /* 不重复加载，避免刷新掉验证状态 */ }
    )
}

private fun buildHtml(apiBase: String): String {
    // apiBase 已不含末尾 /
    return """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"/>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "PingFang SC", "Microsoft YaHei", sans-serif;
      background: transparent;
      padding: 8px;
      color: #333;
    }
    #box {
      border: 1px solid #e5e7eb;
      border-radius: 12px;
      padding: 16px;
      background: #fff;
      text-align: center;
    }
    #status { font-size: 14px; color: #666; margin-bottom: 12px; min-height: 20px; }
    #captcha { min-height: 44px; }
    .err { color: #ff4d4f; }
    .ok { color: #52c41a; }
    button.retry {
      margin-top: 8px;
      padding: 8px 16px;
      border: none;
      border-radius: 8px;
      background: #1677ff;
      color: #fff;
      font-size: 14px;
    }
  </style>
</head>
<body>
  <div id="box">
    <div id="status">正在加载滑动验证...</div>
    <div id="captcha"></div>
  </div>
  <script src="https://static.geetest.com/static/tools/gt.js"></script>
  <script>
    var API_BASE = ${JSONObject.quote(apiBase)};

    function setStatus(text, cls) {
      var el = document.getElementById('status');
      el.className = cls || '';
      el.innerText = text;
    }

    function notifyError(msg) {
      setStatus(msg, 'err');
      try { AndroidBridge.onGeetestError(msg); } catch(e) {}
    }

    function notifySuccess(challenge, validate, seccode) {
      setStatus('验证成功', 'ok');
      var payload = JSON.stringify({
        challenge: challenge,
        validate: validate,
        seccode: seccode
      });
      try { AndroidBridge.onGeetestSuccess(payload); } catch(e) {}
    }

    function start() {
      setStatus('正在获取验证码...');
      document.getElementById('captcha').innerHTML = '';

      fetch(API_BASE + '/api/v1/geetest/register', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      }).then(function(r) { return r.json(); }).then(function(res) {
        if (!res || !res.data) {
          notifyError((res && res.message) || '获取验证码失败');
          return;
        }
        var data = res.data;
        var gt = data.gt;
        var challenge = data.challenge;
        var offline = (data.success === 0 || data.success === false);

        if (!window.initGeetest) {
          notifyError('极验脚本未加载，请检查网络');
          return;
        }

        setStatus('请完成滑动验证');
        window.initGeetest({
          gt: gt,
          challenge: challenge,
          offline: offline,
          new_captcha: true,
          product: 'float',
          width: '100%'
        }, function(captchaObj) {
          captchaObj.appendTo('#captcha');
          captchaObj.onSuccess(function() {
            var result = captchaObj.getValidate();
            if (!result) {
              notifyError('未获取到验证结果');
              return;
            }
            setStatus('正在校验...');
            // 二次验证
            fetch(API_BASE + '/api/v1/geetest/validate', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
              },
              body: JSON.stringify({
                challenge: result.geetest_challenge,
                validate: result.geetest_validate,
                seccode: result.geetest_seccode
              })
            }).then(function(r) { return r.json(); }).then(function(v) {
              if (v && v.success) {
                notifySuccess(
                  result.geetest_challenge,
                  result.geetest_validate,
                  result.geetest_seccode
                );
              } else {
                notifyError((v && v.message) || '二次验证失败，请重试');
                captchaObj.reset();
              }
            }).catch(function(e) {
              notifyError('校验请求失败: ' + e);
            });
          });
          captchaObj.onError(function() {
            notifyError('验证码加载失败');
          });
          captchaObj.onClose(function() {
            setStatus('请完成滑动验证');
          });
        });
      }).catch(function(e) {
        notifyError('网络错误: ' + e);
      });
    }

    // 脚本可能异步，轮询等待 initGeetest
    var tries = 0;
    function waitAndStart() {
      if (window.initGeetest) {
        start();
      } else if (tries++ < 50) {
        setTimeout(waitAndStart, 100);
      } else {
        notifyError('极验脚本加载超时');
      }
    }
    waitAndStart();
  </script>
</body>
</html>
    """.trimIndent()
}
