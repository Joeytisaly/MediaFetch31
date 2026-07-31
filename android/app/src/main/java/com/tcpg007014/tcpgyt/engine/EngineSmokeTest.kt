package com.tcpg007014.tcpgyt.engine

/**
 * 引擎冒烟自检(开发用,纯逻辑,不含 UI)。
 *
 * 目的:在真机上验证 [DownloadEngine.init] 能解压 python / yt-dlp,
 * 且 [DownloadEngine.probe] 能真正解析出媒体元信息 —— 而不只是"能编译"。
 * 不触发实际下载落盘。
 */
object EngineSmokeTest {

    /** 依次 init + probe,返回可读结果字符串;任何异常都转成可读文本,不抛出。 */
    suspend fun run(engine: DownloadEngine, url: String): String {
        return try {
            engine.init()
            val probe = engine.probe(url)
            buildString {
                appendLine("✓ 初始化成功(yt-dlp + ffmpeg)")
                appendLine("✓ 解析成功")
                appendLine("标题:${probe.title ?: "(无)"}")
                append("时长:${probe.durationSeconds} 秒")
            }
        } catch (e: EngineInitException) {
            "✗ 初始化失败:${e.message}"
        } catch (e: Exception) {
            "✗ 解析失败:${e.message ?: e.javaClass.simpleName}"
        }
    }
}
