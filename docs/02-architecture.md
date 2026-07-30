# TCPGYT 架构决策记录（ADR-001）

**状态：已批准**  
**标题：** 选择本地 `youtubedl-android` 作为首版下载引擎集成路线  
**关联：** `docs/00-project-charter.md`、`docs/01-prd.md`

## 1. 背景与约束

TCPGYT 是 Android 本地优先的音视频下载与下载管理产品。它不建设收集注册、账号、用户资料、下载历史或行为数据的远程服务器；不要求用户注册、登录或云同步。

下载解析、任务执行、文件保存、历史和设置默认在用户设备本地完成。用户对合法可访问资源确有兼容需要时，可主动启用并本地导入 Cookie。Cookie 不上传、不共享、不用于追踪。

项目为个人自用、不上架、不对外分发，并接受 GPL-3.0 兼容路线。项目不实现 DRM、付费墙、验证码、访问控制或其他平台安全机制的绕过。

## 2. 决策

首版下载引擎采用本地 Android 包装库：

```text
Kotlin Android App
  └─ TCPGYT 下载引擎适配层
       └─ io.github.junkfood02.youtubedl-android
            ├─ library
            └─ ffmpeg
```

提议在 Android 工程创建阶段使用精确固定版本：

- `io.github.junkfood02.youtubedl-android:library:0.18.1`
- `io.github.junkfood02.youtubedl-android:ffmpeg:0.18.1`

`aria2c` 不纳入首版。

Maven Central 的公开元数据列出 `0.18.1` 为该 `library` 构件的当前发布版本。实际引入前，仍须单独审核 Android 工具链、构件校验、传递依赖和归属文件。

Android Gradle Plugin、Kotlin、Compose、Room、JDK、compileSdk、targetSdk 与 minSdk 不在本 ADR 中确定；必须在 Android 工具链安装并核验后，依据官方兼容矩阵另行审批。

## 3. 模块边界

```text
┌─────────────────────────────────────┐
│ 表现层：Compose 页面                 │
│ 首页 / 解析 / 队列 / 文件库 / 设置   │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│ 应用层：Use Case + ViewModel         │
│ 解析、创建任务、暂停、取消、重试     │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│ 任务层：DownloadCoordinator          │
│ 状态机、并发策略、持久化协调         │
└───────┬──────────────────┬──────────┘
        │                  │
┌───────▼─────────┐ ┌──────▼──────────┐
│ 引擎适配层       │ │ 本地数据层       │
│ DownloaderEngine │ │ Task Repository  │
└───────┬─────────┘ └──────┬──────────┘
        │                  │
┌───────▼─────────┐ ┌──────▼──────────┐
│ youtubedl-android│ │ 安全存储         │
│ yt-dlp + FFmpeg  │ │ Cookie Vault     │
└─────────────────┘ └─────────────────┘
```

| 模块 | 必须负责 | 禁止事项 |
|---|---|---|
| Compose UI | 收集输入、展示状态、触发用户操作。 | 拼接 yt-dlp 参数、保存 Cookie、直接控制后台进程。 |
| ViewModel / Use Case | 校验用户意图、处理 UI 状态、调用业务用例。 | 保存 Cookie 明文、包含平台特定下载命令。 |
| `DownloadCoordinator` | 状态机、并发限制、取消与重试协调。 | 绕过安全策略或自行保存媒体文件。 |
| 引擎适配层 | 将批准格式转为受限请求，归一化进度和错误。 | 提供自由文本命令执行入口。 |
| Cookie Vault | 导入、加密、读取、删除和清空 Cookie。 | 让 Cookie 出现在日志、普通数据库、通知或详情页。 |
| Task Repository | 保存非敏感任务、格式摘要、文件定位和失败摘要。 | 保存完整 Cookie、授权头或敏感 URL 参数。 |
| Android 前台服务 | 用户可见的持续下载、通知与生命周期。 | 创建不可追踪下载。 |
| MediaStore / 文件层 | 保存、定位和删除用户文件。 | 上传文件到远端服务。 |

## 4. 下载生命周期

```text
DRAFT
  → INSPECTING
  → READY
  → QUEUED
  → DOWNLOADING
  → POST_PROCESSING
  → COMPLETED

QUEUED / DOWNLOADING
  → PAUSED
  → CANCELED
  → FAILED

PAUSED
  → QUEUED
  → CANCELED

FAILED
  → INSPECTING（重新解析）
  → QUEUED（格式仍有效时）
  → CANCELED
```

- 写入下载引擎前，任务必须已持久化为 `QUEUED`。
- 每次状态转换必须保存时间戳与非敏感原因码。
- 取消任务不得显示为完成。
- 后处理失败必须保留源下载结果与失败原因，不得静默删除文件。
- 断点续传取决于源站与引擎能力；无法恢复时必须明确提示重新下载。
- 引擎、网络、空间、格式、权限与 Cookie 异常必须映射为可理解的错误类别。

## 5. Cookie 本地安全设计

1. 用户在设置页主动启用 Cookie 功能。
2. 应用展示用途、风险、本地保存、无上传和删除方式。
3. 用户通过系统文件选择器选择 Cookie 文件。
4. 应用仅在内存中校验和提取必要字段。
5. Cookie 数据经加密后写入应用私有目录。
6. 仅在用户主动请求资源并选择关联凭据时由适配层临时读取。
7. 请求结束后清理短期内存引用；用户可删除长期密文。

禁止浏览器自动提取、WebView 静默读取、上传或同步 Cookie。Cookie 明文不得写入 Room、普通 SharedPreferences、日志、通知、分享内容或异常堆栈。

Android Keystore 只管理密钥材料；Cookie 密文与来源标签、导入日期、最后使用时间分离保存。若无法安全初始化密钥存储，Cookie 功能必须禁用，不能回退为明文。

## 6. FFmpeg 与许可

音视频分离流的合并、音频提取或后处理依赖 FFmpeg。`ffmpeg` 构件是首版下载质量与兼容性的必要候选。

`youtubedl-android` 为 GPL-3.0 项目。FFmpeg 的许可证取决于构建选项；FFmpeg 官方说明基础代码为 LGPL 2.1+，启用 GPL 组件时会适用 GPL。TCPGYT 已接受 GPL-3.0 兼容路线，但未来 Android 工程仍必须保留：

- GPL-3.0 文本；
- yt-dlp、youtubedl-android、FFmpeg 的归属说明；
- 所用版本和来源链接；
- 额外二进制的 ABI、版本和校验记录。

## 7. 首版排除项

- `aria2c`；
- 自定义 yt-dlp 命令模板；
- 用户自由输入命令行参数；
- 账号体系、下载服务器、远程代理；
- 上传型崩溃收集、分析 SDK、广告 SDK；
- 自动提取浏览器或其他 App Cookie；
- 批量播放列表、字幕嵌入和高级转码。

## 8. 架构验收标准

1. UI 层不能直接执行下载命令。
2. Cookie 明文不进入普通数据库、日志或通知。
3. 没有注册、登录、用户数据上传或云同步接口。
4. 下载任务具备完整、可恢复的状态机。
5. 引擎、持久化、文件保存和 UI 具有可替换边界。
6. 无 Cookie 的公开资源下载流程完整可用。
7. 许可证与第三方声明是首版工程的一部分。
8. 未单独批准前不得引入 aria2c、命令模板或其他二进制组件。
