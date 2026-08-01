package com.tcpg007014.tcpgyt.engine

import android.content.Context
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * 基于 youtubedl-android(yt-dlp)的 [DownloadEngine] 适配实现。
 *
 * 把底层库完全封装在本类内部;上层只认 [DownloadEngine] 与领域模型(ADR §3/§8)。
 * 所有阻塞调用都置于 [Dispatchers.IO];[download] 支持结构化取消 ——
 * 协程被取消时销毁对应 yt-dlp 进程。**不含** ffmpeg 后处理(D-002)、UI 接入。
 */
class YoutubeDlEngine(private val appContext: Context) : DownloadEngine {

    override suspend fun init() = withContext(Dispatchers.IO) {
        try {
            YoutubeDL.init(appContext)
            // ffmpeg 与 yt-dlp 同属底层引擎的初始化(解压 libffmpeg.zip.so),一并封装在此。
            FFmpeg.init(appContext)
        } catch (e: YoutubeDLException) {
            throw EngineInitException("下载引擎初始化失败", e)
        }
    }

    override suspend fun probe(url: String): MediaProbe = withContext(Dispatchers.IO) {
        val info = YoutubeDL.getInfo(url)
        MediaProbe(
            url = url,
            title = info.title,
            durationSeconds = info.duration,
        )
    }

    override suspend fun update(): EngineUpdate = withContext(Dispatchers.IO) {
        // 仅用户主动触发;从 yt-dlp 官方 Release(STABLE 频道)更新内置内核。
        val status = YoutubeDL.updateYoutubeDL(appContext, YoutubeDL.UpdateChannel.STABLE)
        EngineUpdate(
            status = if (status == YoutubeDL.UpdateStatus.DONE) {
                EngineUpdateStatus.UPDATED
            } else {
                EngineUpdateStatus.ALREADY_LATEST
            },
            version = YoutubeDL.version(appContext),
        )
    }

    override suspend fun download(
        request: DownloadRequest,
        onProgress: (percent: Float, etaSeconds: Long, line: String) -> Unit,
    ): DownloadResult = withContext(Dispatchers.IO) {
        val ytRequest = YoutubeDLRequest(request.url).apply {
            // 输出模板:标题.扩展名,写入指定目录
            addOption("-o", "${request.outputDir}/%(title)s.%(ext)s")
        }
        // 结构化取消:协程被取消时销毁底层进程(execute 为阻塞调用,自身不感知协程取消)。
        val cancelHandle = coroutineContext[Job]?.invokeOnCompletion { cause ->
            if (cause is CancellationException) YoutubeDL.destroyProcessById(request.taskId)
        }
        try {
            val response = YoutubeDL.execute(ytRequest, request.taskId) { percent, eta, line ->
                onProgress(percent, eta, line)
            }
            DownloadResult.Success(response.elapsedTime)
        } catch (e: YoutubeDL.CanceledException) {
            DownloadResult.Canceled
        } catch (e: YoutubeDLException) {
            DownloadResult.Failure(e.message ?: "下载失败")
        } finally {
            cancelHandle?.dispose()
        }
    }

    override fun cancel(taskId: String): Boolean = YoutubeDL.destroyProcessById(taskId)
}
