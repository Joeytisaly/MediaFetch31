package com.tcpg007014.tcpgyt.engine

/**
 * 领域模型:引擎适配层对外暴露的类型。
 *
 * 刻意与 youtubedl-android 的 `VideoInfo` 等类型解耦 —— 上层只认这些领域类型,
 * 底层库若替换或升级,不会波及 UI / ViewModel(ADR §8)。
 */

/** 媒体探测结果(probe 的产物)。 */
data class MediaProbe(
    val url: String,
    val title: String?,
    val durationSeconds: Int,
)

/** 一次下载请求。 */
data class DownloadRequest(
    /** 任务标识,用于进度关联与取消。 */
    val taskId: String,
    val url: String,
    /** 输出目录(应用有权写入的路径)。 */
    val outputDir: String,
)

/** 下载结果。 */
sealed interface DownloadResult {
    data class Success(val elapsedMillis: Long) : DownloadResult
    data object Canceled : DownloadResult
    data class Failure(val message: String) : DownloadResult
}

/** yt-dlp 内核更新结果状态。 */
enum class EngineUpdateStatus { UPDATED, ALREADY_LATEST }

/** yt-dlp 内核更新结果(状态 + 更新后版本)。 */
data class EngineUpdate(
    val status: EngineUpdateStatus,
    val version: String?,
)

/** 引擎初始化失败。 */
class EngineInitException(message: String, cause: Throwable? = null) : Exception(message, cause)
