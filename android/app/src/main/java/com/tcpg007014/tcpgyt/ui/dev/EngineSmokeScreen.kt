package com.tcpg007014.tcpgyt.ui.dev

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcpg007014.tcpgyt.engine.DownloadRequest
import com.tcpg007014.tcpgyt.engine.DownloadResult
import com.tcpg007014.tcpgyt.engine.EngineSmokeTest
import com.tcpg007014.tcpgyt.engine.EngineUpdateStatus
import com.tcpg007014.tcpgyt.engine.YoutubeDlEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 引擎自检屏(开发用,最小实现)。
 *
 * 输入一个公开 URL,点「运行自检」触发 [EngineSmokeTest.run](init + probe),
 * 结果以文本呈现。用于真机验证底层 yt-dlp 是否真正跑通。
 */
@Composable
fun EngineSmokeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // 单一引擎实例,便于取消按钮直接调用 engine.cancel(taskId)。
    val engine = remember { YoutubeDlEngine(context.applicationContext) }
    var url by remember { mutableStateOf("https://www.youtube.com/watch?v=aqz-KE-bpKQ") }
    var running by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }

    // A-1b:yt-dlp 内核更新(仅用户主动触发)。
    var updating by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf("") }

    // A-1:真实下载到应用专属目录(零权限),验证 download + 进度 + 取消链路。
    var downloading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("") }
    var downloadResult by remember { mutableStateOf("") }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var currentTaskId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "对指定公开 URL 执行 init + probe,验证底层引擎能否解压并解析元信息。不会下载媒体。",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("媒体 URL") },
            singleLine = true,
            enabled = !running,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                running = true
                result = ""
                scope.launch {
                    result = EngineSmokeTest.run(YoutubeDlEngine(context.applicationContext), url.trim())
                    running = false
                }
            },
            enabled = !running && url.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (running) "运行中…" else "运行自检")
        }
        if (result.isNotEmpty()) {
            Text(result, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        // ——— A-1b:更新 yt-dlp 内核(解 YouTube 403) ———
        Text(
            "从 yt-dlp 官方更新内置内核(仅本次点击触发)。内置版本过期时 YouTube 会返回 403,更新后可解。",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = {
                updating = true
                updateResult = ""
                scope.launch {
                    updateResult = try {
                        engine.init()
                        val update = engine.update()
                        when (update.status) {
                            EngineUpdateStatus.UPDATED -> "✓ 已更新到 ${update.version ?: "最新版"}"
                            EngineUpdateStatus.ALREADY_LATEST -> "已是最新(${update.version ?: "未知版本"})"
                        }
                    } catch (e: Exception) {
                        "✗ 更新失败:${e.message ?: e.javaClass.simpleName}"
                    } finally {
                        updating = false
                    }
                }
            },
            enabled = !updating,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (updating) "更新中…" else "更新 yt-dlp 内核")
        }
        if (updateResult.isNotEmpty()) {
            Text(updateResult, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        // ——— A-1:下载到应用专属目录 ———
        Text(
            "下载到应用专属目录(无需存储权限)。用于验证真实 download 链路与进度、取消。",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = {
                downloading = true
                progress = ""
                downloadResult = ""
                val outputDir = context.getExternalFilesDir(null)!!.absolutePath
                val taskId = UUID.randomUUID().toString()
                currentTaskId = taskId
                downloadJob = scope.launch {
                    try {
                        engine.init()
                        val request = DownloadRequest(
                            taskId = taskId,
                            url = url.trim(),
                            outputDir = outputDir,
                        )
                        val outcome = engine.download(request) { percent, eta, _ ->
                            // yt-dlp 在解析/预处理阶段回调 -1(未知),此时显示「准备中…」。
                            progress = if (percent < 0f) {
                                "准备中…"
                            } else {
                                "进度:%.1f%%  剩余约 %d 秒".format(percent, eta.coerceAtLeast(0))
                            }
                        }
                        downloadResult = when (outcome) {
                            is DownloadResult.Success ->
                                "✓ 下载完成(用时 ${outcome.elapsedMillis} ms)\n落地目录:$outputDir"
                            DownloadResult.Canceled -> "已取消"
                            is DownloadResult.Failure -> "✗ 下载失败:${outcome.message}"
                        }
                    } catch (e: Exception) {
                        downloadResult = "✗ 下载出错:${e.message ?: e.javaClass.simpleName}"
                    } finally {
                        downloading = false
                    }
                }
            },
            enabled = !downloading && url.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (downloading) "下载中…" else "下载到应用目录")
        }
        if (downloading) {
            Button(
                onClick = {
                    // 直接销毁底层进程(execute 阻塞、不感知协程取消),再取消协程收尾。
                    currentTaskId?.let { engine.cancel(it) }
                    downloadJob?.cancel()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("取消下载")
            }
        }
        if (progress.isNotEmpty()) {
            Text(progress, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        if (downloadResult.isNotEmpty()) {
            Text(downloadResult, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
