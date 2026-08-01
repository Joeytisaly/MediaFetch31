package com.tcpg007014.tcpgyt.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tcpg007014.tcpgyt.engine.DownloadRequest
import com.tcpg007014.tcpgyt.engine.DownloadResult
import com.tcpg007014.tcpgyt.engine.YoutubeDlEngine
import com.tcpg007014.tcpgyt.storage.MediaStorePublisher
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 下载前台服务(A-2)。
 *
 * 把下载迁入前台服务,使切后台/息屏不中断;进度经通知栏呈现,通知上「取消」→
 * 销毁底层进程并结束服务。下载仍经 [YoutubeDlEngine] 适配层,不拼 yt-dlp 命令。
 * 仅本地下载与本地通知,无任何上报。
 */
class DownloadService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val engine by lazy { YoutubeDlEngine(applicationContext) }
    private var taskId: String? = null
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CANCEL -> {
                // 只销毁底层进程;download 会捕获 CanceledException 返回 Canceled,
                // 由下载协程收尾停服。不额外 job.cancel()(否则会被当成「下载出错」)。
                taskId?.let { engine.cancel(it) }
            }
            else -> {
                val url = intent?.getStringExtra(EXTRA_URL)?.trim().orEmpty()
                if (url.isEmpty()) {
                    stopForegroundAndSelf()
                } else {
                    start(url)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun start(url: String) {
        createChannel()
        startForegroundCompat(buildProgress("准备中…", indeterminate = true, percent = 0f))
        // 禁止并发:已有下载在进行时,重复启动只刷新前台通知,不再新起协程,
        // 避免旧下载进程失去 taskId 引用而无法取消(A-2 缺陷修正)。
        if (job?.isActive == true) return
        val id = UUID.randomUUID().toString()
        taskId = id
        job = scope.launch {
            val done: String = try {
                engine.init()
                val outputDir = getExternalFilesDir(null)!!.absolutePath
                val outcome = engine.download(
                    DownloadRequest(taskId = id, url = url, outputDir = outputDir),
                ) { percent, eta, _ ->
                    val text = if (percent < 0f) {
                        "准备中…"
                    } else {
                        "%.1f%%  剩余约 %d 秒".format(percent, eta.coerceAtLeast(0))
                    }
                    updateProgress(buildProgress(text, indeterminate = percent < 0f, percent = percent))
                }
                when (outcome) {
                    is DownloadResult.Success -> publishProduced(outputDir)
                    DownloadResult.Canceled -> "已取消"
                    is DownloadResult.Failure -> "下载失败:${outcome.message}"
                }
            } catch (e: Exception) {
                "下载出错:${e.message ?: e.javaClass.simpleName}"
            }
            notifyDone(done)
            stopForegroundAndSelf()
        }
    }

    /**
     * 下载成功后:定位产出文件 → 发布到公共媒体库 → **删临时**(删应用目录副本)。
     *
     * 删除语义分离(§5):此处只做「删临时」;「删记录 / 删媒体」依赖任务表(Room)与
     * 文件管理 UI,属后续切片,不在本片混入。
     */
    private suspend fun publishProduced(outputDir: String): String {
        val produced = findProducedFile(File(outputDir))
            ?: return "下载完成(未找到产出文件)"
        return try {
            val where = MediaStorePublisher.publish(applicationContext, produced)
            produced.delete() // 删临时:应用目录副本
            "已保存到:$where"
        } catch (e: Exception) {
            "下载完成,但保存到公共目录失败:${e.message ?: e.javaClass.simpleName}(文件仍在应用目录)"
        }
    }

    /** 取应用目录中最新的非中间文件(排除 .part / .ytdl 临时片段)。 */
    private fun findProducedFile(dir: File): File? =
        dir.listFiles { f -> f.isFile && !f.name.endsWith(".part") && !f.name.endsWith(".ytdl") }
            ?.maxByOrNull { it.lastModified() }

    // ——— 通知 ———

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "下载",
                NotificationManager.IMPORTANCE_LOW,
            )
            notificationManager().createNotificationChannel(channel)
        }
    }

    private fun buildProgress(text: String, indeterminate: Boolean, percent: Float): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("正在下载")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(0, "取消", cancelIntent())
        if (indeterminate) {
            builder.setProgress(0, 0, true)
        } else {
            builder.setProgress(100, percent.toInt().coerceIn(0, 100), false)
        }
        return builder.build()
    }

    private fun notifyDone(text: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("下载")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()
        notificationManager().notify(COMPLETE_ID, notification)
    }

    private fun updateProgress(notification: Notification) {
        notificationManager().notify(ONGOING_ID, notification)
    }

    private fun cancelIntent(): PendingIntent {
        val intent = Intent(this, DownloadService::class.java).apply { action = ACTION_CANCEL }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getService(this, 0, intent, flags)
    }

    private fun notificationManager(): NotificationManager =
        getSystemService(NotificationManager::class.java)

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(ONGOING_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(ONGOING_ID, notification)
        }
    }

    private fun stopForegroundAndSelf() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.tcpg007014.tcpgyt.action.START"
        const val ACTION_CANCEL = "com.tcpg007014.tcpgyt.action.CANCEL"
        const val EXTRA_URL = "url"
        private const val CHANNEL_ID = "download"
        private const val ONGOING_ID = 1001
        private const val COMPLETE_ID = 1002
    }
}
