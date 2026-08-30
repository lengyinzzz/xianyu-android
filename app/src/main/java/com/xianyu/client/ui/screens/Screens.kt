package com.xianyu.client.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xianyu.client.data.model.*
import com.xianyu.client.network.RetrofitClient
import kotlinx.coroutines.launch


private val gson = Gson()

/** 订单状态中文 */
private fun orderStatusCn(s: String?): String = when (s?.lowercase()) {
    "shipped", "delivered", "completed", "success" -> "已发货"
    "pending_payment", "unpaid", "wait_pay" -> "待付款"
    "pending_ship", "paid", "wait_ship", "to_ship" -> "待发货"
    "cancelled", "canceled", "closed" -> "已取消"
    "refunding" -> "退款中"
    "refunded" -> "已退款"
    "processing" -> "处理中"
    null, "" -> "未知"
    else -> s
}

/** 发货方式中文 */
private fun deliveryMethodCn(s: String?): String = when (s?.lowercase()) {
    "auto" -> "自动发货"
    "manual" -> "手动发货"
    "scheduled" -> "定时发货"
    "none", "" -> "未发货"
    null -> "-"
    else -> s
}


// ========== 服务器配置 ==========
@Composable
fun ServerConfigScreen(initialUrl: String, onSave: (String) -> Unit) {
    var url by remember { mutableStateOf(initialUrl.ifBlank { "http://" }) }
    var error by remember { mutableStateOf<String?>(null) }
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Dns, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("闲鱼控制端", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("请输入后端服务器地址", color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = url, onValueChange = { url = it; error = null },
            label = { Text("服务器地址") },
            placeholder = { Text("http://192.168.1.100:8089") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                val t = url.trim().removeSuffix("/")
                if (!t.startsWith("http://") && !t.startsWith("https://")) {
                    error = "请以 http:// 或 https:// 开头"; return@Button
                }
                onSave(t)
            },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text("保存并继续") }
    }
}

// ========== 登录 ==========
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onChangeServer: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("登录", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(RetrofitClient.getBaseUrl().removeSuffix("/"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(username, { username = it; error = null }, label = { Text("用户名") }, singleLine = true, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Person, null) })
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(password, { password = it; error = null }, label = { Text("密码") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Lock, null) })
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) { error = "请输入用户名和密码"; return@Button }
                loading = true; error = null
                scope.launch {
                    try {
                        val resp = RetrofitClient.api().login(LoginRequest(username = username.trim(), password = password))
                        if (resp.success && !resp.token.isNullOrBlank()) {
                            RetrofitClient.setToken(resp.token)
                            onLoginSuccess()
                        } else error = resp.message ?: "登录失败"
                    } catch (e: Exception) { error = e.message ?: "网络错误" }
                    finally { loading = false }
                }
            },
            enabled = !loading, modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
            else Text("登录")
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onChangeServer) { Text("更换服务器地址") }
        Text("若开启滑动验证码请先在后台关闭", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.45f))
    }
}

// ========== 主界面带底部导航 ==========
enum class MainTab(val label: String, val icon: ImageVector) {
    Dashboard("控制台", Icons.Default.Home),
    Chat("聊天", Icons.Default.Chat),
    Products("商品", Icons.Default.ShoppingCart),
    Cards("卡券", Icons.Default.Star),
    Orders("订单", Icons.Default.List),
    Risk("风控", Icons.Default.Security)
}

@Composable
fun MainShell(onLogout: () -> Unit) {
    var tab by remember { mutableStateOf(MainTab.Dashboard) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, t.label) },
                        label = { Text(t.label, fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                MainTab.Dashboard -> DashboardContent(onLogout)
                MainTab.Chat -> ChatScreen()
                MainTab.Products -> ProductsScreen()
                MainTab.Cards -> CardsScreen()
                MainTab.Orders -> OrdersScreen()
                MainTab.Risk -> RiskLogsScreen()
            }
        }
    }
}

// ========== 控制台 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(onLogout: () -> Unit) {
    var services by remember { mutableStateOf<List<ServiceStatusItem>>(emptyList()) }
    var runtime by remember { mutableStateOf<String?>(null) }
    var accounts by remember { mutableStateOf<List<AccountOption>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun refresh() {
        scope.launch {
            loading = true; message = null
            try {
                val resp = RetrofitClient.api().getServicesStatus()
                if (resp.success && resp.data != null) {
                    services = resp.data.services
                    runtime = resp.data.runtime
                }
                accounts = try { RetrofitClient.api().getAccountOptions() } catch (_: Exception) { emptyList() }
            } catch (e: Exception) { message = e.message }
            finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("控制台") },
                actions = {
                    IconButton(onClick = { refresh() }) { Icon(Icons.Default.Refresh, "刷新") }
                    IconButton(onClick = onLogout) { Icon(Icons.Default.ExitToApp, "退出") }
                }
            )
        }
    ) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Text("服务状态", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (runtime != null) Text("运行环境: $runtime", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            }
            if (loading && services.isEmpty()) {
                item { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            }
            items(services) { svc ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(10.dp).background(if (svc.online) Color(0xFF52C41A) else Color(0xFFFF4D4F), CircleShape))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(svc.label, fontWeight = FontWeight.Medium)
                            Text("${svc.key} · ${svc.port}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
                        }
                        Text(if (svc.online) "在线" else "离线", color = if (svc.online) Color(0xFF52C41A) else Color(0xFFFF4D4F), fontSize = 13.sp)
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = {
                            scope.launch {
                                try {
                                    val r = RetrofitClient.api().restartService(svc.key)
                                    message = if (r.success) "已重启 ${svc.label}" else (r.message ?: "失败")
                                    refresh()
                                } catch (e: Exception) { message = e.message }
                            }
                        }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)) { Text("重启", fontSize = 12.sp) }
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text("账号概览 (${accounts.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(accounts.take(30)) { acc ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(acc.id, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (!acc.remark.isNullOrBlank()) Text(acc.remark, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
                        }
                        AssistChip(onClick = {}, label = { Text(if (acc.enabled) "启用" else "禁用") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (acc.enabled) Color(0xFFE6F7FF) else Color(0xFFFFF1F0),
                                labelColor = if (acc.enabled) Color(0xFF1677FF) else Color(0xFFFF4D4F)
                            ))
                    }
                }
            }
            if (message != null) item { Text(message!!, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

// ========== 在线聊天 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var accounts by remember { mutableStateOf<List<ChatAccount>>(emptyList()) }
    var selectedAccount by remember { mutableStateOf<ChatAccount?>(null) }
    var conversations by remember { mutableStateOf<List<Conversation>>(emptyList()) }
    var selectedConv by remember { mutableStateOf<Conversation?>(null) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadAccounts() {
        scope.launch {
            loading = true; error = null
            try {
                val body = RetrofitClient.api().getChatAccountsRaw(1, 50).string()
                val root = gson.fromJson(body, com.google.gson.JsonObject::class.java)
                val arr = when {
                    root.get("data")?.isJsonArray == true -> root.getAsJsonArray("data")
                    root.get("accounts")?.isJsonArray == true -> root.getAsJsonArray("accounts")
                    else -> null
                }
                accounts = if (arr != null) gson.fromJson(arr, object : TypeToken<List<ChatAccount>>() {}.type) else emptyList()
            } catch (e: Exception) { error = e.message }
            finally { loading = false }
        }
    }

    fun loadConversations(acc: ChatAccount) {
        scope.launch {
            loading = true; error = null
            try {
                val body = RetrofitClient.api().getConversationsRaw(acc.accountId).string()
                val root = gson.fromJson(body, com.google.gson.JsonObject::class.java)
                // 后端结构: { success, data: { conversations: [...], hasMore, nextCursor } }
                val arr = when {
                    root.get("data")?.isJsonObject == true -> {
                        val data = root.getAsJsonObject("data")
                        when {
                            data.get("conversations")?.isJsonArray == true -> data.getAsJsonArray("conversations")
                            else -> null
                        }
                    }
                    root.get("conversations")?.isJsonArray == true -> root.getAsJsonArray("conversations")
                    root.get("data")?.isJsonArray == true -> root.getAsJsonArray("data")
                    else -> null
                }
                conversations = if (arr != null) gson.fromJson(arr, object : TypeToken<List<Conversation>>() {}.type) else emptyList()
            } catch (e: Exception) { error = e.message }
            finally { loading = false }
        }
    }

    fun loadMessages(acc: ChatAccount, conv: Conversation) {
        scope.launch {
            loading = true; error = null
            try {
                val body = RetrofitClient.api().getMessagesRaw(acc.accountId, conv.cid).string()
                val root = gson.fromJson(body, com.google.gson.JsonObject::class.java)
                // 后端结构: { success, data: { messages: [...], hasMore, nextCursor } }
                val arr = when {
                    root.get("data")?.isJsonObject == true -> {
                        val data = root.getAsJsonObject("data")
                        when {
                            data.get("messages")?.isJsonArray == true -> data.getAsJsonArray("messages")
                            else -> null
                        }
                    }
                    root.get("messages")?.isJsonArray == true -> root.getAsJsonArray("messages")
                    root.get("data")?.isJsonArray == true -> root.getAsJsonArray("data")
                    else -> null
                }
                messages = if (arr != null) gson.fromJson(arr, object : TypeToken<List<ChatMessage>>() {}.type) else emptyList()
            } catch (e: Exception) { error = e.message }
            finally { loading = false }
        }
    }

    LaunchedEffect(Unit) { loadAccounts() }

    when {
        selectedConv != null && selectedAccount != null -> {
            // 消息界面
            val acc = selectedAccount!!
            val conv = selectedConv!!
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Column {
                            Text(conv.otherUserName ?: conv.cid, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (!conv.itemTitle.isNullOrBlank()) Text(conv.itemTitle!!, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }},
                        navigationIcon = {
                            IconButton(onClick = { selectedConv = null; messages = emptyList() }) {
                                Icon(Icons.Default.ArrowBack, "返回")
                            }
                        },
                        actions = {
                            IconButton(onClick = { loadMessages(acc, conv) }) { Icon(Icons.Default.Refresh, null) }
                        }
                    )
                },
                bottomBar = {
                    Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(input, { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("输入消息") }, singleLine = true)
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (input.isBlank()) return@IconButton
                            val text = input.trim()
                            scope.launch {
                                try {
                                    RetrofitClient.api().sendTextMessage(acc.accountId, mapOf(
                                        "cid" to conv.cid,
                                        "toUserId" to (conv.otherUserId ?: ""),
                                        "text" to text
                                    ))
                                    input = ""
                                    loadMessages(acc, conv)
                                } catch (e: Exception) { error = e.message }
                            }
                        }) { Icon(Icons.Default.Send, "发送", tint = MaterialTheme.colorScheme.primary) }
                    }
                }
            ) { p ->
                LazyColumn(Modifier.fillMaxSize().padding(p).padding(horizontal = 12.dp), reverseLayout = false) {
                    items(messages) { msg ->
                        val isSelf = msg.isSelf
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start) {
                            Surface(
                                color = if (isSelf) Color(0xFF1677FF) else Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    msg.text ?: (if (msg.type == "image") "[图片]" else msg.type ?: ""),
                                    modifier = Modifier.padding(10.dp),
                                    color = if (isSelf) Color.White else Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    if (error != null) item { Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        selectedAccount != null -> {
            // 会话列表
            val acc = selectedAccount!!
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(acc.displayName ?: acc.accountId) },
                        navigationIcon = {
                            IconButton(onClick = { selectedAccount = null; conversations = emptyList() }) {
                                Icon(Icons.Default.ArrowBack, null)
                            }
                        },
                        actions = {
                            IconButton(onClick = { loadConversations(acc) }) { Icon(Icons.Default.Refresh, null) }
                            IconButton(onClick = {
                                scope.launch {
                                    try {
                                        if (acc.connected) RetrofitClient.api().disconnectChatAccount(acc.accountId)
                                        else RetrofitClient.api().connectChatAccount(acc.accountId)
                                        loadAccounts()
                                    } catch (e: Exception) { error = e.message }
                                }
                            }) { Icon(if (acc.connected) Icons.Default.Link else Icons.Default.Link, null) }
                        }
                    )
                }
            ) { p ->
                if (loading && conversations.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(p).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (error != null) item { Text(error!!, color = MaterialTheme.colorScheme.error) }
                        items(conversations) { c ->
                            Card(Modifier.fillMaxWidth().clickable {
                                selectedConv = c
                                loadMessages(acc, c)
                            }, shape = RoundedCornerShape(10.dp)) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(c.otherUserName ?: c.otherUserId ?: c.cid, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        if (c.unreadCount > 0) {
                                            Badge { Text("${c.unreadCount}") }
                                        }
                                    }
                                    if (!c.itemTitle.isNullOrBlank()) Text(c.itemTitle!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(c.lastMessageSummary ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        if (conversations.isEmpty() && !loading) item { Text("暂无会话，请先连接账号", color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                    }
                }
            }
        }
        else -> {
            // 账号列表
            Scaffold(topBar = { TopAppBar(title = { Text("在线聊天") }, actions = { IconButton(onClick = { loadAccounts() }) { Icon(Icons.Default.Refresh, null) } }) }) { p ->
                if (loading && accounts.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(p).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (error != null) item { Text(error!!, color = MaterialTheme.colorScheme.error) }
                        items(accounts) { a ->
                            Card(Modifier.fillMaxWidth().clickable {
                                selectedAccount = a
                                loadConversations(a)
                            }, shape = RoundedCornerShape(10.dp)) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(a.displayName ?: a.accountId, fontWeight = FontWeight.Medium)
                                        if (!a.remark.isNullOrBlank()) Text(a.remark!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
                                    }
                                    AssistChip(onClick = {}, label = { Text(if (a.connected) "已连接" else "未连接") },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = if (a.connected) Color(0xFFE6F7FF) else Color(0xFFFFF7E6),
                                            labelColor = if (a.connected) Color(0xFF1677FF) else Color(0xFFFA8C16)
                                        ))
                                }
                            }
                        }
                        if (accounts.isEmpty() && !loading) item { Text("暂无聊天账号", color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                    }
                }
            }
        }
    }
}

// ========== 商品管理 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen() {
    var items by remember { mutableStateOf<List<ItemData>>(emptyList()) }
    var total by remember { mutableStateOf(0) }
    var page by remember { mutableStateOf(1) }
    var keyword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load(p: Int = 1) {
        scope.launch {
            loading = true; error = null
            try {
                val resp = RetrofitClient.api().getItemsPaginated(p, 20, keyword = keyword.ifBlank { null })
                items = resp.data
                total = resp.total
                page = resp.page
            } catch (e: Exception) { error = e.message }
            finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("商品管理 ($total)") }, actions = {
                IconButton(onClick = { load(page) }) { Icon(Icons.Default.Refresh, null) }
            })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                keyword, { keyword = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = { Text("搜索商品ID/标题") },
                singleLine = true,
                trailingIcon = { IconButton(onClick = { load(1) }) { Icon(Icons.Default.Search, null) } }
            )
            if (loading && items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    if (error != null) item { Text(error!!, color = MaterialTheme.colorScheme.error) }
                    items(items) { it ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(it.itemTitle ?: it.title ?: it.itemId ?: "-", fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(4.dp))
                                Row {
                                    Text("ID: ${it.itemId ?: "-"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f), modifier = Modifier.weight(1f))
                                    Text("¥${it.itemPrice ?: it.price ?: "-"}", color = Color(0xFFFF4D4F), fontWeight = FontWeight.Medium)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (it.isPolished == true) AssistChip(onClick = {}, label = { Text("已擦亮") })
                                    if (it.hasCard == true) AssistChip(onClick = {}, label = { Text("有卡券") })
                                }
                            }
                        }
                    }
                    if (items.isNotEmpty()) {
                        item {
                            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                TextButton(onClick = { if (page > 1) load(page - 1) }, enabled = page > 1) { Text("上一页") }
                                Text("第 $page 页", modifier = Modifier.align(Alignment.CenterVertically))
                                TextButton(onClick = { load(page + 1) }) { Text("下一页") }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========== 卡券管理 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen() {
    var cards by remember { mutableStateOf<List<CardData>>(emptyList()) }
    var total by remember { mutableStateOf(0) }
    var page by remember { mutableStateOf(1) }
    var search by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load(p: Int = 1) {
        scope.launch {
            loading = true; error = null
            try {
                val resp = RetrofitClient.api().getCards(p, 20, search.ifBlank { null })
                cards = resp.list
                total = resp.total
                page = resp.page
            } catch (e: Exception) { error = e.message }
            finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { load() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("卡券管理 ($total)") }, actions = {
            IconButton(onClick = { load(page) }) { Icon(Icons.Default.Refresh, null) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                search, { search = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = { Text("搜索卡券名称") }, singleLine = true,
                trailingIcon = { IconButton(onClick = { load(1) }) { Icon(Icons.Default.Search, null) } }
            )
            if (loading && cards.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    if (error != null) item { Text(error!!, color = MaterialTheme.colorScheme.error) }
                    items(cards) { c ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(c.name, fontWeight = FontWeight.Medium)
                                    Text("类型: ${c.type ?: "-"} · ID: ${c.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
                                    if (!c.description.isNullOrBlank()) Text(c.description!!, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                AssistChip(onClick = {}, label = { Text(if (c.enabled != false) "启用" else "禁用") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (c.enabled != false) Color(0xFFE6F7FF) else Color(0xFFFFF1F0),
                                        labelColor = if (c.enabled != false) Color(0xFF1677FF) else Color(0xFFFF4D4F)
                                    ))
                            }
                        }
                    }
                    if (cards.isNotEmpty()) {
                        item {
                            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                TextButton(onClick = { if (page > 1) load(page - 1) }, enabled = page > 1) { Text("上一页") }
                                Text("第 $page 页")
                                TextButton(onClick = { load(page + 1) }) { Text("下一页") }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========== 订单管理 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen() {
    var orders by remember { mutableStateOf<List<OrderData>>(emptyList()) }
    var total by remember { mutableStateOf(0) }
    var page by remember { mutableStateOf(1) }
    var search by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load(p: Int = 1) {
        scope.launch {
            loading = true; error = null
            try {
                val resp = RetrofitClient.api().getOrders(p, 20, search = search.ifBlank { null })
                orders = resp.data
                total = resp.total
                page = resp.page
            } catch (e: Exception) { error = e.message }
            finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { load() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("订单管理 ($total)") }, actions = {
            IconButton(onClick = { load(page) }) { Icon(Icons.Default.Refresh, null) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                search, { search = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = { Text("订单号/商品ID/买家") }, singleLine = true,
                trailingIcon = { IconButton(onClick = { load(1) }) { Icon(Icons.Default.Search, null) } }
            )
            if (loading && orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    if (error != null) item { Text(error!!, color = MaterialTheme.colorScheme.error) }
                    items(orders) { o ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(o.orderNo ?: o.orderId ?: o.id ?: "-", fontWeight = FontWeight.Medium)
                                Text(o.itemTitle ?: o.itemId ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Row {
                                    Text("买家: ${o.buyerFishNick ?: o.buyerId ?: "-"}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                    Text("¥${o.amount ?: "-"}", color = Color(0xFFFF4D4F), fontWeight = FontWeight.Medium)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    AssistChip(onClick = {}, label = { Text(orderStatusCn(o.status)) })
                                    if (!o.deliveryMethod.isNullOrBlank()) AssistChip(onClick = {}, label = { Text(deliveryMethodCn(o.deliveryMethod)) })
                                    if (o.isBargain == true) AssistChip(onClick = {}, label = { Text("小刀") })
                                    if (o.isRated == true) AssistChip(onClick = {}, label = { Text("已评价") })
                                }
                            }
                        }
                    }
                    if (orders.isNotEmpty()) {
                        item {
                            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                TextButton(onClick = { if (page > 1) load(page - 1) }, enabled = page > 1) { Text("上一页") }
                                Text("第 $page 页")
                                TextButton(onClick = { load(page + 1) }) { Text("下一页") }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========== 风控日志 ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskLogsScreen() {
    var logs by remember { mutableStateOf<List<RiskLogItem>>(emptyList()) }
    var total by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(0) }
    val limit = 20
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load(off: Int = 0) {
        scope.launch {
            loading = true; error = null
            try {
                val resp = RetrofitClient.api().getRiskLogs(limit, off)
                logs = resp.data
                total = resp.total
                offset = off
                if (!resp.success && resp.message != null) error = resp.message
            } catch (e: Exception) { error = e.message }
            finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { load() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("风控日志 ($total)") }, actions = {
            IconButton(onClick = { load(offset) }) { Icon(Icons.Default.Refresh, null) }
        })
    }) { padding ->
        if (loading && logs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                if (error != null) item { Text(error!!, color = MaterialTheme.colorScheme.error) }
                items(logs) { log ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Row {
                                Text(log.callType ?: "风控事件", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                val st = log.processingStatus ?: ""
                                val color = when {
                                    st.contains("success", true) || log.success == true -> Color(0xFF52C41A)
                                    st.contains("fail", true) || log.success == false -> Color(0xFFFF4D4F)
                                    else -> Color(0xFFFA8C16)
                                }
                                Text(st.ifBlank { if (log.success == true) "成功" else if (log.success == false) "失败" else "-" }, color = color, fontSize = 13.sp)
                            }
                            Text("账号: ${log.cookieId ?: log.accountId ?: "-"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
                            if (!log.message.isNullOrBlank()) Text(log.message!!, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            if (!log.createdAt.isNullOrBlank()) Text(log.createdAt!!, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
                        }
                    }
                }
                if (logs.isNotEmpty()) {
                    item {
                        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { if (offset > 0) load((offset - limit).coerceAtLeast(0)) }, enabled = offset > 0) { Text("上一页") }
                            Text("${offset + 1}-${offset + logs.size} / $total")
                            TextButton(onClick = { if (offset + limit < total) load(offset + limit) }, enabled = offset + limit < total) { Text("下一页") }
                        }
                    }
                }
                if (logs.isEmpty() && !loading) item { Text("暂无风控日志", color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
            }
        }
    }
}
