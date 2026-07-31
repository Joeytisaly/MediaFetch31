package com.tcpg007014.tcpgyt.ui.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcpg007014.tcpgyt.ui.components.TcpgytBottomSheet
import com.tcpg007014.tcpgyt.ui.components.TcpgytSegmented
import com.tcpg007014.tcpgyt.ui.theme.AppTheme

private enum class SettingPage {
    None, DownloadPrefs, SaveLocation, Cookie, LocalData, Appearance, About
}

private data class SettingItem(
    val label: String,
    val detail: String,
    val icon: ImageVector,
    val iconBg: Color,
    val onClick: () -> Unit
)

private data class ThemeOption(
    val theme: AppTheme,
    val title: String,
    val detail: String,
    val swatches: List<Color>
)

private val themeOptions = listOf(
    ThemeOption(AppTheme.Blush, "淡粉玻璃", "温柔奶油底 · 玫红强调", listOf(Color(0xFFF6B8C8), Color(0xFFFFF9F8), Color(0xFFED1D55))),
    ThemeOption(AppTheme.Blue, "淡蓝玻璃", "雾蓝光晕 · 清透蓝强调", listOf(Color(0xFFB9D9EE), Color(0xFFF7FBFF), Color(0xFF287FBD))),
    ThemeOption(AppTheme.Mint, "薄荷玻璃", "雾绿光晕 · 青绿强调", listOf(Color(0xFFBFE5D5), Color(0xFFF7FFFB), Color(0xFF218C72))),
    ThemeOption(AppTheme.Lavender, "淡紫玻璃", "熏衣草光晕 · 紫罗兰强调", listOf(Color(0xFFD8C8EE), Color(0xFFFBF9FF), Color(0xFF7659AD))),
    ThemeOption(AppTheme.Night, "深海玻璃", "墓蓝夜色 · 浅蓝高亮", listOf(Color(0xFF10243A), Color(0xFF193551), Color(0xFF76C2ED)))
)

@Composable
fun MoreScreen(
    padding: PaddingValues,
    current: AppTheme,
    onSnack: (String) -> Unit = {},
    onTheme: (AppTheme) -> Unit
) {
    var page by remember { mutableStateOf(SettingPage.None) }
    var defaultType by remember { mutableStateOf("视频") }
    var videoQuality by remember { mutableStateOf("推荐") }
    var audioFormat by remember { mutableStateOf("原始音频") }
    var networkPref by remember { mutableStateOf("仅 Wi-Fi") }
    var autoStart by remember { mutableStateOf(true) }
    var savePath by remember { mutableStateOf("Download / TCPGYT") }
    var cookieEnabled by remember { mutableStateOf(false) }
    var cookieItems by remember { mutableStateOf(listOf<String>()) }
    var temporaryFilesPresent by remember { mutableStateOf(true) }
    var historyCleared by remember { mutableStateOf(false) }
    var cookieConfirm by remember { mutableStateOf<String?>(null) }
    var dataConfirm by remember { mutableStateOf<String?>(null) }
    var restorePathConfirm by remember { mutableStateOf(false) }

    BackHandler(enabled = page != SettingPage.None) { page = SettingPage.None }

    val primaryWash = MaterialTheme.colorScheme.primaryContainer

    if (page == SettingPage.None) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            Text("更多", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(24.dp))

            SettingsGroup(
                title = "下载",
                items = listOf(
                    SettingItem("下载偏好", "默认格式、画质与网络", Icons.Outlined.Download, primaryWash) { page = SettingPage.DownloadPrefs },
                    SettingItem("保存位置", savePath, Icons.Outlined.FolderOpen, Color(0xFFFFE4CC)) { page = SettingPage.SaveLocation }
                )
            )
            Spacer(Modifier.height(16.dp))
            SettingsGroup(
                title = "隐私",
                items = listOf(
                    SettingItem("Cookie 管理", if (cookieEnabled) "已启用 · ${cookieItems.size} 个占位" else "未启用 · 本地导入", Icons.Outlined.Cookie, Color(0xFFD4EEFF)) { page = SettingPage.Cookie },
                    SettingItem("本地数据", "清理任务记录与缓存", Icons.Outlined.Storage, Color(0xFFEBE4FF)) { page = SettingPage.LocalData }
                )
            )
            Spacer(Modifier.height(16.dp))
            SettingsGroup(
                title = "应用",
                items = listOf(
                    SettingItem("外观", current.label, Icons.Outlined.AutoAwesome, primaryWash) { page = SettingPage.Appearance },
                    SettingItem("关于与支持", "TCPGYT · 原型版", Icons.Outlined.Info, primaryWash) { page = SettingPage.About }
                )
            )
            Spacer(Modifier.height(24.dp))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = { page = SettingPage.None },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("‹ 返回", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = when (page) {
                    SettingPage.DownloadPrefs -> "下载偏好"
                    SettingPage.SaveLocation  -> "保存位置"
                    SettingPage.Cookie        -> "Cookie 管理"
                    SettingPage.LocalData     -> "本地数据"
                    SettingPage.Appearance    -> "外观"
                    SettingPage.About         -> "关于与支持"
                    else -> ""
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            when (page) {
                SettingPage.DownloadPrefs -> DownloadPrefsPage(
                    defaultType, videoQuality, audioFormat, networkPref, autoStart,
                    onDefaultType = { defaultType = it; onSnack("已更新默认下载类型") },
                    onVideoQuality = { videoQuality = it; onSnack("已更新默认视频质量") },
                    onAudioFormat = { audioFormat = it; onSnack("已更新默认音频格式") },
                    onNetworkPref = { networkPref = it; onSnack("已更新网络偏好") },
                    onAutoStart = { autoStart = it; onSnack("已更新自动开始设置") }
                )
                SettingPage.SaveLocation -> SaveLocationPage(
                    savePath = savePath,
                    onSelectRequest = { onSnack("系统文件夹选择器尚未接入") },
                    onRestoreRequest = { restorePathConfirm = true }
                )
                SettingPage.Cookie -> CookiePage(
                    enabled = cookieEnabled,
                    items = cookieItems,
                    onToggle = { cookieEnabled = it; onSnack(if (it) "已启用本地 Cookie 状态占位" else "已关闭 Cookie 状态占位") },
                    onAdd = { cookieItems = cookieItems + "本地状态 ${cookieItems.size + 1}"; onSnack("已添加本地状态占位") },
                    onDelete = { cookieConfirm = it },
                    onClearAll = { cookieConfirm = "全部" }
                )
                SettingPage.LocalData -> LocalDataPage(
                    temporaryFilesPresent = temporaryFilesPresent,
                    historyCleared = historyCleared,
                    onAction = { dataConfirm = it }
                )
                SettingPage.Appearance -> AppearancePage(
                    current = current,
                    onPick = { theme ->
                        onTheme(theme)
                        onSnack("已切换为${themeOptions.first { it.theme == theme }.title}主题")
                    }
                )
                SettingPage.About -> AboutPage()
                else -> Unit
            }
        }
    }

    if (restorePathConfirm) {
        AlertDialog(
            onDismissRequest = { restorePathConfirm = false },
            title = { Text("恢复默认位置？") },
            text = { Text("后续下载将保存到 Download / TCPGYT。") },
            confirmButton = {
                TextButton(onClick = { savePath = "Download / TCPGYT"; restorePathConfirm = false; onSnack("已恢复默认保存位置") }) {
                    Text("确认", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = { TextButton(onClick = { restorePathConfirm = false }) { Text("取消") } }
        )
    }

    if (cookieConfirm != null) {
        AlertDialog(
            onDismissRequest = { cookieConfirm = null },
            title = { Text(if (cookieConfirm == "全部") "清空全部本地 Cookie？" else "删除此本地状态？") },
            confirmButton = {
                TextButton(onClick = {
                    if (cookieConfirm == "全部") cookieItems = emptyList()
                    else cookieItems = cookieItems.filter { it != cookieConfirm }
                    cookieConfirm = null; onSnack("已清空本地 Cookie")
                }) { Text("确认", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { cookieConfirm = null }) { Text("取消") } }
        )
    }

    if (dataConfirm != null) {
        AlertDialog(
            onDismissRequest = { dataConfirm = null },
            title = { Text("${dataConfirm}？") },
            text = {
                Text(
                    when (dataConfirm) {
                        "清理临时文件" -> "仅清除原型中的临时文件标记，不会触碰设备文件。"
                        "清空下载历史" -> "仅隐藏已完成、失败或取消的任务记录；不会删除媒体文件。"
                        else -> "仅恢复原型中的下载偏好；不会影响文件或设备设置。"
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    when (dataConfirm) {
                        "清理临时文件" -> temporaryFilesPresent = false
                        "清空下载历史" -> historyCleared = true
                        "重置应用设置" -> { defaultType = "视频"; videoQuality = "推荐"; audioFormat = "原始音频"; networkPref = "仅 Wi-Fi"; autoStart = true }
                    }
                    onSnack(
                        when (dataConfirm) {
                            "清理临时文件" -> "已清理临时文件标记"
                            "清空下载历史" -> "已清空任务记录"
                            else -> "已重置原型偏好"
                        }
                    )
                    dataConfirm = null
                }) { Text("确认", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { dataConfirm = null }) { Text("取消") } }
        )
    }
}

@Composable
private fun SettingsGroup(title: String, items: List<SettingItem>) {
    Text(
        title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
    items.forEach { item ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = item.onClick)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(item.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun SectionCard(title: String?, content: @Composable ColumnScope.() -> Unit) {
    if (title != null) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DownloadPrefsPage(
    defaultType: String, videoQuality: String, audioFormat: String,
    networkPref: String, autoStart: Boolean,
    onDefaultType: (String) -> Unit, onVideoQuality: (String) -> Unit,
    onAudioFormat: (String) -> Unit, onNetworkPref: (String) -> Unit,
    onAutoStart: (Boolean) -> Unit
) {
    var localPrefPicker by remember { mutableStateOf<String?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "这些偏好仅影响后续创建的原型任务，不会修改设备网络或真实下载设置。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SectionCard("默认类型") {
            // 画布 line173：py-2.5 + text-sm → 14sp / lineHeight 20sp / 纵向 10dp（比文件筛选略大）
            TcpgytSegmented(
                options = listOf("视频", "音频"),
                selected = defaultType,
                onSelect = onDefaultType,
                modifier = Modifier.padding(12.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                verticalPadding = 10.dp
            )
        }
        Spacer(Modifier.height(12.dp))
        SectionCard("格式与网络") {
            listOf("默认视频质量" to videoQuality, "默认音频格式" to audioFormat, "网络偏好" to networkPref)
                .forEachIndexed { i, (label, value) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { localPrefPicker = label }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    if (i < 2) HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                }
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("自动开始下载", style = MaterialTheme.typography.titleMedium)
                    Text("创建任务后自动进入下载中", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = autoStart, onCheckedChange = onAutoStart)
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    // 偏好选择弹窗 —— 统一走 TcpgytBottomSheet：完全展开 + 可滚动
    if (localPrefPicker != null) {
        TcpgytBottomSheet(onDismiss = { localPrefPicker = null }) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
                Text(localPrefPicker!!, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(16.dp))
                val options = when (localPrefPicker) {
                    "默认视频质量" -> listOf("推荐", "高清", "省空间")
                    "默认音频格式" -> listOf("原始音频", "MP3", "M4A")
                    else -> listOf("仅 Wi-Fi", "任意网络")
                }
                options.forEach { opt ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable {
                            when (localPrefPicker) {
                                "默认视频质量" -> onVideoQuality(opt)
                                "默认音频格式" -> onAudioFormat(opt)
                                else -> onNetworkPref(opt)
                            }
                            localPrefPicker = null
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Text(opt, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveLocationPage(savePath: String, onSelectRequest: () -> Unit, onRestoreRequest: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "保存位置仅用于当前原型展示，不会读取、创建或写入设备文件夹。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SectionCard("当前原型位置") {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) { Text("📁", style = MaterialTheme.typography.titleLarge) }
                Column {
                    Text(savePath, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("视频和音频的原型保存位置", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        SectionCard(null) {
            Column {
                Row(
                    Modifier.fillMaxWidth().clickable(onClick = onSelectRequest).padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("选择文件夹", style = MaterialTheme.typography.bodyLarge)
                        Text("系统文件夹选择器尚未接入", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                TextButton(onClick = onRestoreRequest, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text("恢复默认位置")
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CookiePage(
    enabled: Boolean, items: List<String>,
    onToggle: (Boolean) -> Unit, onAdd: () -> Unit,
    onDelete: (String) -> Unit, onClearAll: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🔒", style = MaterialTheme.typography.titleLarge)
                Column {
                    Text("本地 Cookie 状态占位", style = MaterialTheme.typography.titleSmall)
                    Text("不会显示、保存、导入或上传任何 Cookie 内容。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        SectionCard("本地状态") {
            Column {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("本地状态", style = MaterialTheme.typography.titleMedium)
                        Text(if (enabled) "已启用 · ${items.size} 个状态占位" else "未启用", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            if (enabled) "已启用" else "未启用",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                if (enabled && items.isNotEmpty()) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                    items.forEach { item ->
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(item, style = MaterialTheme.typography.bodyMedium)
                                Text("不含任何 Cookie 内容", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { onDelete(item) }) { Text("删除", color = MaterialTheme.colorScheme.error) }
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (enabled) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onAdd, modifier = Modifier.weight(1f)) { Text("添加状态占位") }
                OutlinedButton(onClick = onClearAll, modifier = Modifier.weight(1f)) { Text("清空全部") }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onToggle(false) }, modifier = Modifier.fillMaxWidth()) { Text("关闭 Cookie") }
        } else {
            Button(onClick = { onToggle(true) }, modifier = Modifier.fillMaxWidth()) { Text("启用本地状态占位") }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LocalDataPage(temporaryFilesPresent: Boolean, historyCleared: Boolean, onAction: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ℹ️", style = MaterialTheme.typography.titleLarge)
                Column {
                    Text("原型数据管理", style = MaterialTheme.typography.titleSmall)
                    Text("操作只更新当前原型状态，不删除设备媒体文件、浏览器数据或 Cookie 内容。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        SectionCard("清理操作") {
            val actions = listOf(
                Triple("临时文件", if (temporaryFilesPresent) "待清理" else "已清理", "清理临时文件"),
                Triple("下载历史", if (historyCleared) "已清空" else "任务记录", "清空下载历史"),
                Triple("应用设置", "原型偏好", "重置应用设置")
            )
            actions.forEachIndexed { i, (label, status, action) ->
                Row(
                    Modifier.fillMaxWidth().clickable { onAction(action) }.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(status, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                    Text(action, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                if (i < actions.size - 1) HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AppearancePage(current: AppTheme, onPick: (AppTheme) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "主题偏好仅保存在本地；不上传、不与账号同步。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        themeOptions.forEach { option ->
            val selected = current == option.theme
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { onPick(option.theme) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else Color.White.copy(alpha = 0.65f)
                ),
                border = if (selected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                    ) {
                        option.swatches.forEach { color ->
                            Box(Modifier.weight(1f).fillMaxHeight().background(color))
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        Text(option.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(option.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .border(1.5.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) Text("✓", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AboutPage() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.size(72.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) { Text("↓", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary) }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("TCPGYT", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                Text("原型版", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
        Text("本地优先的下载管理界面原型", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        SectionCard("应用信息") {
            listOf(
                "开发者" to "TCPG007014 (YaR)",
                "联系邮箱" to "ChengYuan.tcpg@gnail.com",
                "包名" to "com.tcpg007014.tcpgyt",
                "隐私" to "数据仅保存在本机"
            ).forEachIndexed { i, (label, value) ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, style = MaterialTheme.typography.bodyMedium)
                }
                if (i < 3) HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ℹ️", style = MaterialTheme.typography.titleLarge)
                Column {
                    Text("支持开发暂未开放", style = MaterialTheme.typography.titleSmall)
                    Text("尚未配置捐赠信息，因此不会显示链接或跳转入口。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
