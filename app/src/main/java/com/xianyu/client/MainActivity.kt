package com.xianyu.client

import android.os.Bundle
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.xianyu.client.network.RetrofitClient
import com.xianyu.client.ui.screens.*
import com.xianyu.client.ui.theme.XianyuTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var prefs: com.xianyu.client.util.Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 全屏内容区域，状态栏仅系统图标，应用内容不顶到状态栏里
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        prefs = (application as XianyuApp).prefs

        setContent {
            XianyuTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNav()
                }
            }
        }
    }

    @Composable
    private fun AppNav() {
        var stage by remember { mutableStateOf("loading") } // loading / server / login / main
        var savedUrl by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            val url = prefs.getBaseUrl()
            val token = prefs.getToken()
            savedUrl = url
            if (url.isBlank()) {
                stage = "server"
            } else {
                RetrofitClient.setBaseUrl(url)
                if (!token.isNullOrBlank()) {
                    RetrofitClient.setToken(token)
                    try {
                        val v = RetrofitClient.api().verifyToken()
                        stage = if (v.authenticated) "main" else "login"
                    } catch (_: Exception) {
                        stage = "login"
                    }
                } else {
                    stage = "login"
                }
            }
        }

        when (stage) {
            "loading" -> { /* wait */ }
            "server" -> {
                ServerConfigScreen(
                    initialUrl = savedUrl,
                    onSave = { url ->
                        lifecycleScope.launch {
                            prefs.saveBaseUrl(url)
                            RetrofitClient.setBaseUrl(url)
                            savedUrl = url
                            stage = "login"
                        }
                    }
                )
            }
            "login" -> {
                LoginScreen(
                    onLoginSuccess = {
                        lifecycleScope.launch {
                            val token = RetrofitClient.getToken()
                            if (token != null) {
                                prefs.saveAuth(token, null, null)
                            }
                            stage = "main"
                        }
                    },
                    onChangeServer = { stage = "server" }
                )
            }
            "main" -> {
                MainShell(
                    onLogout = {
                        lifecycleScope.launch {
                            try { RetrofitClient.api().logout() } catch (_: Exception) {}
                            RetrofitClient.setToken(null)
                            prefs.clearAuth()
                            stage = "login"
                        }
                    }
                )
            }
        }
    }
}
