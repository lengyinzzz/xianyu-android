package com.xianyu.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xianyu.client.data.model.AccountOption
import com.xianyu.client.data.model.ServiceStatusItem
import com.xianyu.client.network.RetrofitClient
import kotlinx.coroutines.launch

// ========== 服务器配置页 ==========
@Composable
fun ServerConfigScreen(
    initialUrl: String,
    onSave: (String) -> Unit
) {
    var url by remember { mutableStateOf(initialUrl.ifBlank { "http://" }) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("闲鱼自动回复 · 控制端", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("请输入后端服务器地址", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it; error = null },
            label = { Text("服务器地址") },
            placeholder = { Text("http://192.168.1.100:8089") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                val trimmed = url.trim().removeSuffix("/")
                if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                    error = "请以 http:// 或 https:// 开头"
                    return@Button
                }
                onSave(trimmed)
            },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("保存并继续")
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "示例：http://你的IP:8089\n（对应 backend-web 服务端口）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// ========== 登录页 ==========
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onChangeServer: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("登录", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            RetrofitClient.getBaseUrl().removeSuffix("/"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; error = null },
            label = { Text("用户名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, null) }
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text("密码") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Password, null) }
        )
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    error = "请输入用户名和密码"
                    return@Button
                }
                loading = true
                error = null
                scope.launch {
                    try {
                        val resp = RetrofitClient.api().login(
                            com.xianyu.client.data.model.LoginRequest(
                                username = username.trim(),
                                password = password
                            )
                        )
                        if (resp.success && !resp.token.isNullOrBlank()) {
                            RetrofitClient.setToken(resp.token)
                            onLoginSuccess()
                        } else {
                            error = resp.message ?: "登录失败"
                        }
                    } catch (e: Exception) {
                        error = e.message ?: "网络错误，请检查服务器地址与网络"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
            } else {
                Text("登录")
            }
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onChangeServer) {
            Text("更换服务器地址")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "若后端开启了滑动验证码，当前版本暂不支持，请先在后台关闭登录验证码。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
        )
    }
}

// ========== 主控制台 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onOpenAccounts: () -> Unit
) {
    var services by remember { mutableStateOf<List<ServiceStatusItem>>(emptyList()) }
    var runtime by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun refresh() {
        scope.launch {
            loading = true
            message = null
            try {
                val resp = RetrofitClient.api().getServicesStatus()
                if (resp.success && resp.data != null) {
                    services = resp.data.services
                    runtime = resp.data.runtime
                } else {
                    message = resp.message ?: "获取状态失败"
                }
            } catch (e: Exception) {
                message = e.message ?: "网络错误"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("控制台") },
                actions = {
                    IconButton(onClick = { refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "退出")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("服务状态", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (runtime != null) {
                    Text("运行环境: $runtime", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            if (loading && services.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(services) { svc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    if (svc.online) Color(0xFF52C41A) else Color(0xFFFF4D4F),
                                    shape = RoundedCornerShape(50)
                                )
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(svc.label, fontWeight = FontWeight.Medium)
                            Text("${svc.key} · 端口 ${svc.port}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Text(
                            if (svc.online) "在线" else "离线",
                            color = if (svc.online) Color(0xFF52C41A) else Color(0xFFFF4D4F),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    try {
                                        val r = RetrofitClient.api().restartService(svc.key)
                                        message = if (r.success) "已发送重启指令: ${svc.label}" else (r.message ?: "重启失败")
                                        refresh()
                                    } catch (e: Exception) {
                                        message = e.message
                                    }
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("重启", fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onOpenAccounts,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.People, null)
                    Spacer(Modifier.width(8.dp))
                    Text("账号管理")
                }
            }

            if (message != null) {
                item {
                    Text(message!!, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// ========== 账号列表 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onBack: () -> Unit
) {
    var accounts by remember { mutableStateOf<List<AccountOption>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            loading = true
            error = null
            try {
                accounts = RetrofitClient.api().getAccountOptions()
            } catch (e: Exception) {
                error = e.message ?: "加载失败"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("账号列表") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { load() }) {
                        Icon(Icons.Default.Refresh, null)
                    }
                }
            )
        }
    ) { padding ->
        when {
            loading && accounts.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { load() }) { Text("重试") }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    item {
                        Text("共 ${accounts.size} 个账号", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    items(accounts) { acc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        acc.id,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!acc.remark.isNullOrBlank()) {
                                        Text(acc.remark, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    }
                                    Text("pk=${acc.pk}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(if (acc.enabled) "已启用" else "已禁用") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (acc.enabled) Color(0xFFE6F7FF) else Color(0xFFFFF1F0),
                                        labelColor = if (acc.enabled) Color(0xFF1677FF) else Color(0xFFFF4D4F)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
