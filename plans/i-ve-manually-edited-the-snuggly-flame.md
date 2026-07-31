# Phase E 切片 E-002:引入 kotlinx-coroutines + 引擎改 suspend + 最小冒烟入口

## Context(为什么做)

E-001 已让引擎骨架(`DownloadEngine` 接口 + `YoutubeDlEngine` 实现)入 build 并构建成功,但方法当前是**阻塞式**,注释里明确写着"结构化并发将随经批准的 kotlinx-coroutines 依赖后续引入"。本切片兑现这一步:

1. 让引擎方法可安全地在后台执行并支持**结构化取消**(取消协程 → 销毁底层 yt-dlp 进程),这是后续接 UI/ViewModel/前台服务的前提。
2. 提供一个**最小可运行冒烟入口**,让用户能在真机上验证 `init` + `probe` 真的跑通(底层解压 python/yt-dlp、能解析出标题/时长),而不是只"能编译"。

用户已批准该方案("同意你的方案")。本文件即 先审后写 变更方案,待用户确认精确版本后再落笔。

## 依赖核实(官方来源,已完成)

- 组件:`org.jetbrains.kotlinx:kotlinx-coroutines-android`
- 精确版本:**`1.11.0`**(最新稳定版)
- 来源:Maven Central `repo1.maven.org/.../kotlinx-coroutines-android/1.11.0/` + GitHub Release `Kotlin/kotlinx.coroutines` tag `1.11.0`
- 许可:**Apache-2.0**(POM `<licenses>` 核实)
- 兼容依据:1.11.0 由 **Kotlin 2.2.20** 编译;项目 Kotlin 2.4.10 更新,Kotlin 元数据向前兼容(新编译器可消费旧元数据),安全。仅引入 `-android` 构件,它传递依赖 `-core`,无需单列。
- 保守替代:`1.10.2`(2025-04 稳定版)可作回退,若用户偏好更成熟版本。

## 变更清单

### 1. `android/gradle/libs.versions.toml`
- `[versions]` 增:`coroutines = "1.11.0"`
- `[libraries]` 增:`kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }`

### 2. `android/app/build.gradle.kts`
- `dependencies { }` 增:`implementation(libs.kotlinx.coroutines.android)`

### 3. `.../engine/DownloadEngine.kt`(接口)
- `init()` → `suspend fun init()`;`probe(url)` → `suspend`;`download(...)` → `suspend`
- `cancel(taskId): Boolean` 保持非 suspend(即发即忘,供只持有 taskId 时使用)
- 更新 KDoc:线程约定改为"挂起函数,内部切 `Dispatchers.IO`;取消随协程结构化传播"。

### 4. `.../engine/YoutubeDlEngine.kt`(实现)
- `init` / `probe`:`withContext(Dispatchers.IO) { ... }` 包裹现有逻辑,行为不变。
- `download`:`withContext(Dispatchers.IO)` 包裹;用 `coroutineContext[Job]?.invokeOnCompletion { if (it is CancellationException) YoutubeDL.destroyProcessById(taskId) }` 实现结构化取消(协程被取消 → 销毁 yt-dlp 进程),`finally` 中 `dispose()` handle;保留 `YoutubeDL.CanceledException`/`YoutubeDLException` → `Canceled`/`Failure` 映射。
- 边界不变:底层库仍完全封闭在本类内(ADR §3/§8)。

### 5.(新)`.../engine/EngineSmokeTest.kt`
- `object EngineSmokeTest { suspend fun run(engine: DownloadEngine, url: String): String }`
- `engine.init()` → `engine.probe(url)`,拼标题/时长为可读字符串;捕获 `EngineInitException` 及其它异常 → 可读错误文本。纯逻辑,无 UI。

### 6.(新)`.../ui/dev/EngineSmokeScreen.kt`
- 最小 Compose 屏:URL 输入框(默认一个公开测试 URL)、"运行自检"按钮、结果 `Text`、返回按钮。
- `rememberCoroutineScope().launch { result = EngineSmokeTest.run(YoutubeDlEngine(context.applicationContext), url) }`;运行时禁用按钮显示"运行中…"。复用 `TcpgytTheme`,保持精简,不引新依赖。

### 7. `.../ui/more/MoreScreen.kt`
- `SettingPage` 枚举增 `EngineSmoke`;设置列表增一行"引擎自检(开发)";`when` 增一分支渲染 `EngineSmokeScreen`,复用现有 `BackHandler`/返回模式。局部改动,不重排现有页面。

## 关联影响

- UI:仅 MoreScreen 新增一个开发入口 + 一个新子屏;其它页面不变。
- 状态机/存储/网络:无新增持久化、无服务端、无账号、无远程日志;probe 仅发起 yt-dlp 元信息解析请求(读取用户提供的公开 URL,符合本地优先边界)。
- Cookie:本切片不涉及;probe 默认不带 Cookie。
- 依赖:仅 +1 运行时依赖(Apache-2.0)。

## 回滚

- 撤销 4 处编辑 + 删除 2 个新文件即回到 E-001;依赖行移除后无残留副作用。

## 验收(用户真机)

1. 工作区改完 → 用户在 Make 手动推送 → `git fetch origin` / `git reset --hard origin/main`。
2. `cd android` → `.\gradlew.bat :app:assembleDebug` 构建成功(验证依赖 + suspend 改造编译通过)。
3. 装机 → 「更多」→「引擎自检(开发)」→ 公开 URL 点「运行自检」:首次 init 解压后 probe 返回标题+时长(证明 yt-dlp 真跑通);失败显示可读错误,不崩溃。
4.(可选)运行中按返回可取消,进程被销毁(结构化取消)。

## 不在本切片范围(后续另审)

- D-002 ffmpeg 精确 Maven 版本 + FFmpeg 二进制许可与归属。
- 真正下载落盘(存储权限/MediaStore/前台服务)、ViewModel、Room、DataStore 扩展、security-crypto。
- 冒烟入口收敛到 debug-only variant(上线阶段处理)。
- docs/03 标注 D-001「已入 build」、PROGRESS.md 同步(与本切片一并推送时补)。
