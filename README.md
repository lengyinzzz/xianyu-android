# 闲鱼自动回复 · Android 原生控制端

原生 Kotlin + Jetpack Compose 客户端（非 WebView）。

## 功能# 闲鱼自动回复 · Android 原生控制端

原生 Kotlin + Jetpack Compose（非 WebView）。

## 功能（本次更新）

| 模块 | 说明 |
|------|------|
| 控制台 | 服务状态查看 / 一键重启 / 账号概览 |
| 在线聊天 | 账号连接、会话列表、收发文本消息 |
| 商品管理 | 分页列表、搜索商品 |
| 卡券管理 | 分页列表、搜索卡券 |
| 订单管理 | 分页列表、搜索订单 |
| 风控日志 | 分页查看风控处理记录 |

底部导航切换各模块。

## 用 GitHub Actions 构建 APK

1. 新建 GitHub 空仓库
2. 上传本项目全部文件
3. Actions → **Build APK** → **Run workflow**
4. 完成后在 Artifacts 下载 `xianyu-client-debug`

## 本地构建

用 Android Studio 打开最外层 `xianyu-android-client` 文件夹，Sync 后 Run。

## 使用

1. 输入后端地址，如 `http://IP:8089`
2. 登录（若开启滑动验证码请先在后台关闭）
3. 底部切换：控制台 / 聊天 / 商品 / 卡券 / 订单 / 风控

## 本次相对上一版新增/修改的文件

- `app/.../data/model/Models.kt` — 增加聊天/商品/卡券/订单/风控模型
- `app/.../data/api/ApiService.kt` — 增加对应 API
- `app/.../ui/screens/Screens.kt` — 底部导航 + 全部业务页面
- `app/.../MainActivity.kt` — 接入新主界面
- `app/build.gradle.kts` — 补充 gson 依赖
- `.github/workflows/build-apk.yml` — CI 构建（已有）

- 输入服务器地址后使用
- 登录
- 查看并重启服务（backend-web / websocket / scheduler）
- 查看账号列表

---

## 方法一：用 GitHub Actions 自动构建 APK（推荐）

1. 注册/登录 [GitHub](https://github.com)
2. 新建一个 **空仓库**（不要勾选 README）
3. 把本项目所有文件上传到仓库（可以用网页上传，或用 Git）
4. 打开仓库页面 → 点击 **Actions** 标签
5. 选择左侧 **Build APK** → 点击 **Run workflow**
6. 等待几分钟构建完成
7. 构建成功后，在该次运行页面底部 **Artifacts** 处下载：
   - `xianyu-client-debug` → 调试版 APK（推荐直接安装）
   - `xianyu-client-release` → 正式版（未签名）

下载后解压即可得到 `.apk` 文件，传到手机安装。

> 注意：首次安装需要在手机上允许「未知来源」应用。

---

## 方法二：本地 Android Studio 构建

1. 用 Android Studio 打开**最外层** `xianyu-android-client` 文件夹
2. 等待 Gradle 同步完成
3. 点击 Run 运行，或使用 **Build → Build Bundle(s) / APK(s) → Build APK(s)**

---

## 使用说明
1. 打开 App，输入后端地址，例如：`http://你的IP:8089`
2. 使用后台账号密码登录
3. 即可查看服务状态、重启服务、查看账号

如果后端开启了登录滑动验证码，请先在后台关闭，否则无法登录。
