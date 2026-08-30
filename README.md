# 闲鱼自动回复 · Android 客户端

本项目是 [zhinianboke/xianyu-auto-reply](https://github.com/zhinianboke/xianyu-auto-reply) 的 **Android 原生客户端**。

- 上游项目：https://github.com/zhinianboke/xianyu-auto-reply  
- 本仓库定位：**仅前端（手机端）**，连接你已部署好的后端服务使用  
- 技术：Kotlin + Jetpack Compose（非 WebView 套壳整站）

不需要改后端代码，也不需要再单独搭一套服务。后端按上游项目正常跑起来后，在本 App 里填入服务器地址即可使用。

---

## 与上游的关系

| 项目 | 说明 |
|------|------|
| [xianyu-auto-reply](https://github.com/zhinianboke/xianyu-auto-reply) | 原系统：后端 API、网页管理端、自动回复等完整能力 |
| 本仓库 | 基于上述后端 API 开发的 Android 控制端，方便在手机上查看与操作 |

本客户端通过 HTTP API 与上游后端通信（登录 Token、控制台、聊天、商品、卡券、订单、风控等接口）。  
网页后台能用的同一套后端地址，一般也可直接用于本 App。

---

## 功能概览

| 模块 | 说明 |
|------|------|
| 登录 | 配置服务器地址、账号密码；支持 WebView 套一层极验滑动验证 |
| 控制台 | 资金/订单统计（数字滚动动画）、服务状态、一键重启、账号概览、订单补评价 |
| 在线聊天 | 账号连接/断开、会话列表、收发文本消息（最新消息在底部） |
| 商品管理 | 分页列表、搜索 |
| 卡券管理 | 分页列表、搜索 |
| 订单管理 | 分页列表、搜索、状态中文显示、标签不换行错位 |
| 风控日志 | 分页查看，类型与状态尽量中文显示 |

---

## 使用方法

1. 按上游仓库文档部署并启动 **xianyu-auto-reply** 后端（例如 `http://IP:8089`）
2. 安装本 App
3. 打开后输入**服务器地址**（与网页管理后台使用的后端地址一致）
4. 使用后台账号密码登录  
   - 若开启了登录滑动验证：先完成滑块再登录  
   - 若未开启：可直接登录
5. 通过底部导航使用各功能

---

## 用 GitHub Actions 构建 APK

1. 将本仓库推送到 GitHub  
2. 打开 **Actions** → **Build APK** → **Run workflow**  
3. 构建完成后在 **Artifacts** 下载 APK 安装  

也可用 Android Studio 打开本项目本地编译运行。

---

## 项目结构（简要）

```
├── app/src/main/java/com/xianyu/client/
│   ├── MainActivity.kt              # 入口与登录流程
│   ├── data/api/ApiService.kt       # 对接上游后端 API
│   ├── data/model/Models.kt
│   ├── network/RetrofitClient.kt
│   ├── ui/geetest/GeetestWebView.kt # 滑动验证
│   ├── ui/screens/Screens.kt        # 业务界面
│   └── ui/theme/Theme.kt
├── .github/workflows/               # CI 构建 APK
└── README.md
```

---

## 注意事项

1. 手机需能访问你的后端地址（局域网或公网）。  
2. 使用 `http` 时请保证网络可达；工程已允许 cleartext。  
3. 滑动验证依赖后端极验接口及公网脚本 `static.geetest.com`。  
4. 部分复杂管理（卡券新建、商品绑卡等）仍以**网页后台**为主，本客户端侧重常用查看与操作。  
5. 后端接口若与上游版本不一致，个别列表可能为空，需对照上游 API 调整。

---

## 致谢与许可

- 原项目由 [zhinianboke/xianyu-auto-reply](https://github.com/zhinianboke/xianyu-auto-reply) 开发与维护，本客户端基于其公开 API 与业务能力实现手机端访问。  
- 使用本客户端前，请遵守上游仓库的许可证与使用规范。  
- 本仓库仅提供 Android 客户端实现，不包含上游后端源码。
