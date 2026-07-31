package com.tcpg007014.tcpgyt.engine

/**
 * 下载引擎适配层抽象。
 *
 * 架构边界(ADR §3 / §8):UI 与 ViewModel 只能依赖本接口,
 * **不得**直接引用 youtubedl-android、也不得自行拼接 yt-dlp 命令。
 * 所有对底层引擎的调用都必须经由该接口的实现。
 *
 * 线程约定:本接口的方法为**挂起函数**,实现内部切到 [kotlinx.coroutines.Dispatchers.IO];
 * 取消随协程结构化传播(取消协程即取消底层 yt-dlp 进程),调用方无需自建线程。
 */
interface DownloadEngine {

    /** 初始化底层引擎(解压 python / yt-dlp 到应用私有目录)。应在应用启动后调用一次。 */
    suspend fun init()

    /** 解析媒体信息(标题 / 时长等),不下载媒体本身。 */
    suspend fun probe(url: String): MediaProbe

    /**
     * 启动一次下载。协程被取消时,底层进程会被结构化销毁。
     *
     * @param request 下载请求(含任务标识、URL、输出目录)。
     * @param onProgress 进度回调:百分比(0-100)、预计剩余秒数、原始日志行。
     * @return 下载结果(成功 / 失败,含可读原因)。
     */
    suspend fun download(
        request: DownloadRequest,
        onProgress: (percent: Float, etaSeconds: Long, line: String) -> Unit,
    ): DownloadResult

    /**
     * 按任务标识取消正在进行的下载。
     * @return 是否成功发出取消(任务不存在或已结束时返回 false)。
     */
    fun cancel(taskId: String): Boolean
}
