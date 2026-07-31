package com.tcpg007014.tcpgyt.engine

import android.content.Context
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest

/**
 * 基于 youtubedl-android(yt-dlp)的 [DownloadEngine] 适配实现。
 *
 * 骨架阶段目标:打通 init / probe / download / cancel 的调用链与类型边界,
 * 把底层库完全封装在本类内部。**不含** ffmpeg 后处理(D-002)、协程调度、UI 接入。
 *
 * 注意:方法为阻塞式,须由调用方置于后台线程(见 [DownloadEngine] 线程约定)。
 */
class YoutubeDlEngine(private val appContext: Context) : DownloadEngine {

    override fun init() {
        try {
            YoutubeDL.init(appContext)
        } catch (e: YoutubeDLException) {
            throw EngineInitException("下载引擎初始化失败", e)
        }
    }

    override fun probe(url: String): MediaProbe {
        val info = YoutubeDL.getInfo(url)
        return MediaProbe(
            url = url,
            title = info.title,
            durationSeconds = info.duration,
        )
    }

    override fun download(
        request: DownloadRequest,
        onProgress: (percent: Float, etaSeconds: Long, line: String) -> Unit,
    ): DownloadResult {
        val ytRequest = YoutubeDLRequest(request.url).apply {
            // 输出模板:标题.扩展名,写入指定目录
            addOption("-o", "${request.outputDir}/%(title)s.%(ext)s")
        }
        return try {
            val response = YoutubeDL.execute(ytRequest, request.taskId) { percent, eta, line ->
                onProgress(percent, eta, line)
            }
            DownloadResult.Success(response.elapsedTime)
        } catch (e: YoutubeDL.CanceledException) {
            DownloadResult.Canceled
        } catch (e: YoutubeDLException) {
            DownloadResult.Failure(e.message ?: "下载失败")
        }
    }

    override fun cancel(taskId: String): Boolean = YoutubeDL.destroyProcessById(taskId)
}
