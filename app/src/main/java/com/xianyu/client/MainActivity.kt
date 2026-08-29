package com.xianyu.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
        enableEdgeToEdge()
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
        var stage by remember { mutableStateOf("loading") }
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
            "loading" -> { }
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
