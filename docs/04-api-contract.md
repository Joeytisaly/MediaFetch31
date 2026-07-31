# TCPGYT 本地模块接口契约（API Contract v0.1）

**状态：草案（待审批）**
**关联：** `docs/00-project-charter.md`、`docs/01-prd.md`、`docs/02-architecture.md`、`docs/05-data-model.md`

> 本文件定义 TCPGYT 各层之间的**契约级**接口（签名 + 职责 + 禁止项），不含实现。
> 作为 Phase E（Android）编码蓝本。引擎核心为 yt-dlp（经 ADR-001 批准的封装 `youtubedl-android`）。
> 实体类型引用 `docs/05-data-model.md`。凡未拍板处标 **[待审批]**。

## 0. 契约原则

- 层间**单向依赖**：UI → 应用层 → 任务层 →（引擎适配层 / 数据层）。UI 不得跨层直达引擎（ADR §3、§8-1）。
- **无自由命令执行**：任何层都不得暴露自由文本 / yt-dlp 原始参数入口（ADR §3、§8-8）。
- **无远端**：不存在注册/登录/上传/云同步接口（章程 §3、ADR §8-3）。
- 所有挂起操作以协程 `suspend` 或 `Flow` 表达；错误经统一 `FailureCode` 归一化。

## 1. DownloaderEngine（引擎适配层）

将"已批准的格式选择"转为受限请求，归一化进度与错误；隔离 `youtubedl-android` / yt-dlp / FFmpeg。

```kotlin
interface DownloaderEngine {
    // 解析资源，返回资源信息 + 可用格式；credentialRef 为可选 Cookie 引用
    suspend fun inspect(sourceRef: String, credentialRef: String?): InspectResult

    // 依据已持久化的 QUEUED 任务发起下载；返回进度事件流
    fun start(request: EngineDownloadRequest): Flow<EngineProgress>

    suspend fun pause(taskId: String)
    suspend fun resume(taskId: String)
    suspend fun cancel(taskId: String)
}

data class InspectResult(
    val title: String,
    val durationSec: Int?,
    val thumbnailRef: String?,
    val sourceLabel: String,
    val formats: List<MediaFormat>,   // 见 05 §2.4
)

data class EngineDownloadRequest(
    val taskId: String,
    val sourceRef: String,
    val formatId: String,             // 来自受限的格式列表，非自由参数
    val output: FileLocator,          // 见 05 §2.6
    val credentialRef: String?,
)

sealed interface EngineProgress {
    data class Running(val percent: Float, val speedBps: Long?, val downloaded: Long, val total: Long?) : EngineProgress
    data object PostProcessing : EngineProgress
    data class Completed(val output: FileLocator) : EngineProgress
    data class Failed(val code: FailureCode, val note: String?) : EngineProgress   // note 已脱敏
}
```

**必须**：把批准格式转受限请求、归一化进度/错误、封装 FFmpeg 后处理。
**禁止**：暴露自由文本命令；输出/持久化 Cookie 明文；自行加入 aria2c / 命令模板 / 二进制下载更新逻辑（ADR §7、§8-8）。

## 2. DownloadCoordinator（任务层）

驱动状态机、并发与持久化协调。

```kotlin
interface DownloadCoordinator {
    suspend fun enqueue(taskId: String)          // 前提：任务已持久化为 QUEUED
    suspend fun pause(taskId: String)
    suspend fun resume(taskId: String)
    suspend fun cancel(taskId: String)
    suspend fun retry(taskId: String)            // FAILED → INSPECTING / QUEUED
    fun observe(taskId: String): Flow<DownloadTask>
}
```

**必须**：状态机合法转换（05 §2.2）、并发上限 [待审批]、每次转换写 `StatusTransition`、取消不显示为完成、后处理失败保留文件。
**禁止**：绕过安全策略；自行保存媒体文件（交文件层）。

## 3. TaskRepository（本地数据层）

```kotlin
interface TaskRepository {
    suspend fun create(task: DownloadTask): String
    suspend fun update(task: DownloadTask)
    suspend fun updateStatus(taskId: String, to: TaskStatus, reasonCode: String)
    suspend fun get(taskId: String): DownloadTask?
    fun observeByStatus(vararg status: TaskStatus): Flow<List<DownloadTask>>   // 文件库/历史筛选
    suspend fun deleteRecord(taskId: String)     // 只删记录
}
```

**禁止存**：完整 Cookie、授权头、敏感 URL 参数（05 §2.1、§0）。

## 4. CookieVault（Cookie 本地安全）

```kotlin
interface CookieVault {
    suspend fun isEnabled(): Boolean
    suspend fun setEnabled(enabled: Boolean)
    suspend fun import(fileUri: String): CookieEntry     // 仅内存校验+加密落盘
    suspend fun list(): List<CookieEntry>                // 仅元数据（05 §2.7）
    suspend fun deleteOne(ref: String)
    suspend fun clearAll()
    // 仅供引擎适配层在用户主动请求时临时取用；用后清内存
    suspend fun readForRequest(ref: String): SecureCookieHandle
}
```

**必须**：设置页主动启用；导入前展示用途/风险/本地保存/删除方式；密文经 Keystore 加密，与元数据分离；删除后引擎不可再读（PRD 验收 9、ADR §5）。
**禁止**：浏览器/WebView 自动提取；上传/同步；明文进日志/通知/普通库/异常堆栈。若 Keystore 无法安全初始化 → 禁用 Cookie，不回退明文。

## 5. 应用层用例（ViewModel 调用）

对齐 PRD §4.2 主流程，供三大 Compose 页面调用：

```kotlin
interface ParseUseCase   { suspend fun parse(rawUrl: String, credentialRef: String?): InspectResult }  // 含基础 URL 校验
interface CreateTaskUseCase { suspend fun create(inspect: InspectResult, formatId: String): String }   // 落 QUEUED 后 enqueue
interface FileLibraryUseCase { fun observe(filter: LibraryFilter): Flow<List<DownloadTask>>; suspend fun openFile(taskId: String); suspend fun locateFile(taskId: String); suspend fun deleteRecord(taskId: String); suspend fun deleteFile(taskId: String) /* 二次确认 */ }
interface PreferencesUseCase { fun observe(): Flow<AppPreferences>; suspend fun update(prefs: AppPreferences) }
```

**必须**：校验用户意图、处理 UI 状态。
**禁止**：拼接 yt-dlp 参数；保存 Cookie 明文；包含平台特定下载命令（ADR §3）。

## 6. 前台服务与文件层（契约边界）

- **前台下载服务**：用户可见的持续下载、通知、生命周期；后台/锁屏/切换不丢状态；系统杀死后恢复策略 [待审批]（PRD §5.3）。
- **MediaStore / 文件层**：`save / locate / delete` 用户文件；禁止上传远端（ADR §3）。

## 7. 契约验收（对应 ADR §8 / PRD §7）

1. UI 层无法直接执行下载命令 → §0、§5 禁止项（ADR §8-1）。
2. Cookie 明文不进普通库/日志/通知 → §3、§4（ADR §8-2、PRD 验收 9）。
3. 无注册/登录/上传/云同步接口 → 全文无此类签名（ADR §8-3）。
4. 任务状态机完整可恢复 → §2（ADR §8-4、PRD 验收 3）。
5. 引擎/持久化/文件/UI 可替换边界 → §1–§6 接口隔离（ADR §8-5）。
6. 无 Cookie 的公开资源流程完整 → `credentialRef` 全程可空（ADR §8-6、PRD 验收 7）。
7. 未批准前不得引入 aria2c/命令模板/其他二进制 → §1 禁止项（ADR §8-8）。

## 8. 本文件未决项（[待审批]）

1. 并发上限、断点续传接口字段（并发/恢复策略定案后，PRD §8-4）。
2. 系统杀死后的任务恢复契约。
3. Cookie 文件支持格式与 `import` 校验细节（详见后续 `docs/06`）。
4. 协程调度器 / 线程模型（工具链审批阶段）。
