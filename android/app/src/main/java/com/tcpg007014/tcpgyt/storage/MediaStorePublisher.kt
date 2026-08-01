package com.tcpg007014.tcpgyt.storage

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 把下载完成的媒体从应用专属目录发布到设备公共媒体库,使系统「相册/文件」可见(A-3)。
 *
 * 视频→Movies、音频→Music、其它→Downloads。
 * - API 29+:走 MediaStore(`RELATIVE_PATH`+`IS_PENDING`),**免存储权限**。
 * - API 24–28:复制到公共目录 + `MediaScanner` 建索引,**需 `WRITE_EXTERNAL_STORAGE`**。
 *
 * 仅写入用户自己设备的公共媒体目录,无任何上报。
 */
object MediaStorePublisher {

    /** 发布并返回可读落地位置;失败抛异常(由调用方兜底)。 */
    suspend fun publish(context: Context, tempFile: File): String = withContext(Dispatchers.IO) {
        val kind = kindOf(tempFile.extension)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            publishViaMediaStore(context, tempFile, kind)
        } else {
            publishLegacy(context, tempFile, kind)
        }
    }

    private enum class Kind(val dir: String) {
        VIDEO(Environment.DIRECTORY_MOVIES),
        AUDIO(Environment.DIRECTORY_MUSIC),
        OTHER(Environment.DIRECTORY_DOWNLOADS),
    }

    private fun kindOf(ext: String): Kind = when (ext.lowercase()) {
        "mp4", "mkv", "webm", "mov", "avi", "flv", "m4v", "3gp" -> Kind.VIDEO
        "mp3", "m4a", "aac", "opus", "ogg", "flac", "wav" -> Kind.AUDIO
        else -> Kind.OTHER
    }

    private fun mimeOf(ext: String): String = when (ext.lowercase()) {
        "mp4", "m4v" -> "video/mp4"
        "mkv" -> "video/x-matroska"
        "webm" -> "video/webm"
        "mov" -> "video/quicktime"
        "mp3" -> "audio/mpeg"
        "m4a", "aac" -> "audio/mp4"
        "opus", "ogg" -> "audio/ogg"
        "flac" -> "audio/flac"
        "wav" -> "audio/x-wav"
        else -> "application/octet-stream"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun publishViaMediaStore(context: Context, tempFile: File, kind: Kind): String {
        val resolver = context.contentResolver
        val collection = when (kind) {
            Kind.VIDEO -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            Kind.AUDIO -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            Kind.OTHER -> MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, tempFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeOf(tempFile.extension))
            put(MediaStore.MediaColumns.RELATIVE_PATH, kind.dir)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val uri: Uri = resolver.insert(collection, values)
            ?: throw IllegalStateException("无法创建媒体库条目")
        resolver.openOutputStream(uri).use { out ->
            requireNotNull(out) { "无法打开媒体库输出流" }
            tempFile.inputStream().use { it.copyTo(out) }
        }
        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return "${kind.dir}/${tempFile.name}"
    }

    @Suppress("DEPRECATION")
    private fun publishLegacy(context: Context, tempFile: File, kind: Kind): String {
        val publicDir = Environment.getExternalStoragePublicDirectory(kind.dir)
        if (!publicDir.exists()) publicDir.mkdirs()
        val dest = File(publicDir, tempFile.name)
        tempFile.copyTo(dest, overwrite = true)
        // 建索引让相册/文件应用可见(异步,发后即忘)。
        MediaScannerConnection.scanFile(context, arrayOf(dest.absolutePath), null, null)
        return dest.absolutePath
    }
}
