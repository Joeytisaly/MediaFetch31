package com.tcpg007014.tcpgyt.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.theme.AppTheme

private enum class Sheet { None, Theme, DownloadPrefs, SaveLocation, Cookie, LocalData, About }

@Composable
fun MoreScreen(padding: PaddingValues, current: AppTheme, onTheme: (AppTheme) -> Unit) {
    var sheet by remember { mutableStateOf(Sheet.None) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("更多", style = MaterialTheme.typography.headlineMedium)
        Text("设置均在本地存储，不上传任何数据", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))

        SectionLabel("下载")
        Setting("下载偏好", "默认格式、画质与网络") { sheet = Sheet.DownloadPrefs }
        Setting("保存位置", "Downloads/TCPGYT") { sheet = Sheet.SaveLocation }

        SectionLabel("隐私")
        Setting("Cookie 管理", "未启用 · 本地导入") { sheet = Sheet.Cookie }
        Setting("本地数据", "清理任务记录与缓存") { sheet = Sheet.LocalData }

        SectionLabel("应用")
        Setting("外观", current.label) { sheet = Sheet.Theme }
        Setting("关于与支持", "TCPGYT · v1.0") { sheet = Sheet.About }
    }

    when (sheet) {
        Sheet.Theme -> ThemeSheet(current, { onTheme(it); sheet = Sheet.None }, { sheet = Sheet.None })
        Sheet.DownloadPrefs -> DownloadPrefsSheet { sheet = Sheet.None }
        Sheet.SaveLocation -> SaveLocationSheet { sheet = Sheet.None }
        Sheet.Cookie -> CookieSheet { sheet = Sheet.None }
        Sheet.LocalData -> LocalDataSheet { sheet = Sheet.None }
        Sheet.About -> AboutSheet { sheet = Sheet.None }
        Sheet.None -> Unit
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
    )
}

@Composable
private fun Setting(title: String, detail: String, click: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable(onClick = click)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSheet(current: AppTheme, pick: (AppTheme) -> Unit, dismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = dismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            Text("选择外观", style = MaterialTheme.typography.titleLarge)
            Text("外观选择已自动保存", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            AppTheme.entries.forEach { theme ->
                ListItem(
                    headlineContent = { Text(theme.label) },
                    supportingContent = { Text(if (theme == AppTheme.Blush) "默认主题" else "预览主题") },
                    trailingContent = { if (theme == current) Text("已启用", color = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.clickable { pick(theme) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DownloadPrefsSheet(dismiss: () -> Unit) {
    var defaultType by remember { mutableStateOf("音频") }
    var videoQuality by remember { mutableStateOf("720P") }
    var audioFormat by remember { mutableStateOf("MP3") }
    var wifiOnly by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = dismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("下载偏好", style = MaterialTheme.typography.titleLarge)
            Text("仅在本地保存，不上传", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            Text("默认类型", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("音频", "视频").forEach { type ->
                    FilterChip(selected = defaultType == type, onClick = { defaultType = type }, label = { Text(type) })
                }
            }

            Spacer(Modifier.height(14.dp))
            Text("视频画质", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1080P", "720P", "480P").forEach { q ->
                    FilterChip(selected = videoQuality == q, onClick = { videoQuality = q }, label = { Text(q) })
                }
            }

            Spacer(Modifier.height(14.dp))
            Text("音频格式", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("MP3", "M4A").forEach { fmt ->
                    FilterChip(selected = audioFormat == fmt, onClick = { audioFormat = fmt }, label = { Text(fmt) })
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("仅 Wi-Fi 下载", style = MaterialTheme.typography.titleMedium)
                    Text("移动网络时不自动开始任务", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = wifiOnly, onCheckedChange = { wifiOnly = it })
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = dismiss, modifier = Modifier.fillMaxWidth()) { Text("保存") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaveLocationSheet(dismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = dismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("保存位置", style = MaterialTheme.typography.titleLarge)
            Text("下载文件将存储在此目录", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("当前路径", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Text("Downloads/TCPGYT", style = MaterialTheme.typography.bodyLarge)
                    Text("文件权限尚未申请，路径仅供预览", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("选择其他目录（下载引擎接入后可用）") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = dismiss, modifier = Modifier.fillMaxWidth()) { Text("关闭") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CookieSheet(dismiss: () -> Unit) {
    var enabled by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = dismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Cookie 管理", style = MaterialTheme.typography.titleLarge)
            Text("Cookie 仅本地使用，不上传、不共享、不记录明文", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("启用 Cookie", style = MaterialTheme.typography.titleMedium)
                    Text("用于需要登录才能下载的内容", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = enabled, onCheckedChange = { enabled = it })
            }

            if (enabled) {
                Spacer(Modifier.height(14.dp))
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text("导入方式", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(6.dp))
                        Text("通过浏览器扩展导出 cookies.txt 文件后，在此导入", style = MaterialTheme.typography.bodySmall)
                        Text("Cookie 明文不会进入日志、通知或网络请求", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("导入 cookies.txt（下载引擎接入后可用）") }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = dismiss, modifier = Modifier.fillMaxWidth()) { Text("确认") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalDataSheet(dismiss: () -> Unit) {
    var confirmClear by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = dismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("本地数据", style = MaterialTheme.typography.titleLarge)
            Text("仅清理应用内部数据，不影响已下载的媒体文件", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            listOf(
                "任务记录" to "0 条（数据库尚未建立）",
                "下载缓存" to "0 B",
                "Cookie 数据" to "未启用"
            ).forEach { (label, value) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, style = MaterialTheme.typography.bodyMedium)
                    Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDivider()
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = { confirmClear = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("清除所有本地数据") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = dismiss, modifier = Modifier.fillMaxWidth()) { Text("关闭") }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("确认清除") },
            text = { Text("将清除所有任务记录和缓存数据。已下载的媒体文件不受影响。") },
            confirmButton = {
                TextButton(onClick = { confirmClear = false; dismiss() }) {
                    Text("确认清除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("取消") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutSheet(dismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = dismiss) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text("TCPGYT", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text("本地优先 · 音视频下载", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("v1.0 · 原型阶段", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(20.dp))

            listOf(
                "开发者" to "TCPG007014 (YaR)",
                "包名" to "com.tcpg007014.tcpgyt",
                "本地优先" to "不收集数据，不需要账号",
                "捐赠" to "暂未开放"
            ).forEach { (label, value) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider()
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = dismiss, modifier = Modifier.fillMaxWidth()) { Text("关闭") }
        }
    }
}
