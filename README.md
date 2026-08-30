# 闲鱼自动回复 · Android 原生控制端

基于 Kotlin + Jetpack Compose 的原生 Android 客户端，连接闲鱼自动回复系统后端（FastAPI），**不是 WebView 套壳**。

输入服务器地址并登录后，可在手机上查看与控制主要业务。

---

## 功能概览

| 模块 | 功能 |
|------|------|
| **登录** | 配置后端地址、账号密码登录；支持 WebView 套一层极验滑动验证 |
| **控制台** | 资金/订单统计、服务在线状态、一键重启、账号概览、订单补评价 |
| **在线聊天** | 账号连接/断开、会话列表、收发文本消息（最新消息在底部） |
| **商品管理** | 分页列表、搜索商品 ID/标题 |
| **卡券管理** | 分页列表、搜索卡券 |
| **订单管理** | 分页列表、搜索、状态/发货方式中文显示 |
| **风控日志** | 分页查看，类型与状态尽量中文显示 |

底部导航在各模块间切换。

---

## 环境要求

- 后端已部署并可访问（默认端口 `8089`）
- 手机与后端网络互通（同一局域网或公网）
- 若使用滑动验证，手机需能访问 `static.geetest.com`

---

## 使用方法

1. 安装 APK 并打开
2. 输入后端地址，例如：`http://192.168.1.100:8089`（不要末尾多余路径）
3. 输入后台用户名、密码登录  
   - 若后台开启了登录滑动验证：先在登录页完成滑块再登录  
   - 若未开启：可直接登录
4. 使用底部导航进入各功能

---

## 用 GitHub Actions 构建 APK（推荐）

1. 将本仓库推送到 GitHub
2. 打开仓库 **Actions** → 选择 **Build APK** → **Run workflow**
3. 等待构建成功
4. 在该次运行页面底部 **Artifacts** 下载：
   - `xianyu-client-debug` — 调试版（可直接安装）
   - `xianyu-client-release` — 正式版（未签名）

首次安装需在手机上允许「安装未知应用」。

---

## 本地用 Android Studio 构建

1. 用 Android Studio 打开**最外层**项目目录（内含 `settings.gradle.kts`、`app/`）
2. 等待 Gradle Sync 完成
3. 连接手机或模拟器，点击 Run  
   或菜单：**Build → Build Bundle(s) / APK(s) → Build APK(s)**

---

## 项目结构

```
xianyu-android-client/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/xianyu/client/
│       │   ├── MainActivity.kt          # 入口与登录流程
│       │   ├── XianyuApp.kt
│       │   ├── data/
│       │   │   ├── api/ApiService.kt    # 后端接口
│       │   │   └── model/Models.kt      # 数据模型
│       │   ├── network/RetrofitClient.kt
│       │   ├── ui/
│       │   │   ├── geetest/GeetestWebView.kt  # 滑动验证 WebView
│       │   │   ├── screens/Screens.kt         # 全部业务界面
│       │   │   └── theme/Theme.kt
│       │   └── util/Prefs.kt            # 本地存储（地址、Token）
│       └── res/
├── .github/workflows/build-apk.yml      # CI 自动构建
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 技术栈

- Kotlin
- Jetpack Compose + Material3
- Retrofit2 + OkHttp + Gson
- DataStore Preferences
- WebView（仅用于极验滑动验证）
- GitHub Actions 构建 APK

---

## 注意事项

1. 后端 CORS / 鉴权需允许手机端访问；Token 以 `Authorization: Bearer` 携带。
2. 使用 `http` 内网地址时，工程已开启 cleartext 流量。
3. 滑动验证依赖后端 `/api/v1/geetest/register` 与 `/validate`，以及极验公网脚本。
4. 卡券新增/编辑、商品绑定卡券等复杂表单仍以网页后台为主，本客户端以查看与常用操作为主。
5. 若接口字段与后端版本不一致，对应列表可能为空，需对照后端 API 调整模型。

---

## 许可证

请遵循上游闲鱼自动回复系统仓库的许可协议。
