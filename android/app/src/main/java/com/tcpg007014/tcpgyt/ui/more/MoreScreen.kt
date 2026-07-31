package com.tcpg007014.tcpgyt.ui.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcpg007014.tcpgyt.ui.components.TcpgytBottomSheet
import com.tcpg007014.tcpgyt.ui.components.TcpgytIcons
import com.tcpg007014.tcpgyt.ui.components.TcpgytSegmented
import com.tcpg007014.tcpgyt.ui.theme.AppTheme
import com.tcpg007014.tcpgyt.ui.theme.LocalAppTheme
import com.tcpg007014.tcpgyt.ui.theme.themeCardSurface
import com.tcpg007014.tcpgyt.ui.theme.themeGlass
import com.tcpg007014.tcpgyt.ui.theme.themeHairline
import com.tcpg007014.tcpgyt.ui.theme.themeMuted
import com.tcpg007014.tcpgyt.ui.theme.themeNeutralAction
import com.tcpg007014.tcpgyt.ui.theme.themePrimaryPale
import com.tcpg007014.tcpgyt.ui.theme.themePrimarySoft
import com.tcpg007014.tcpgyt.ui.theme.themePrimaryWash
import com.tcpg007014.tcpgyt.ui.theme.themeSectionLabel

private enum class SettingPage {
    None, DownloadPrefs, SaveLocation, Cookie, LocalData, Appearance, About
}

private data class SettingItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

private data class CleanAction(
    val label: String,
    val action: String,
    val status: String,
    val detail: String
)

private data class ThemeOption(
    val theme: AppTheme,
    val title: String,
    val detail: String,
    val swatches: List<Color>
)

// 强调色对齐画布 App.tsx themeOptions 精确值（blue #3A8DCC / lavender #7B61B4 / deep #83C8F2）。
private val themeOptions = listOf(
    ThemeOption(AppTheme.Blush, "淡粉玻璃", "温柔奶油底 · 玫红强调", listOf(Color(0xFFF6B8C8), Color(0xFFFFF9F8), Color(0xFFED1D55))),
    ThemeOption(AppTheme.Blue, "淡蓝玻璃", "雾蓝光晕 · 清透蓝强调", listOf(Color(0xFFB9D9EE), Color(0xFFF7FBFF), Color(0xFF3A8DCC))),
    ThemeOption(AppTheme.Mint, "薄荷玻璃", "雾绿光晕 · 青绿强调", listOf(Color(0xFFBFE5D5), Color(0xFFF7FFFB), Color(0xFF218C72))),
    ThemeOption(AppTheme.Lavender, "淡紫玻璃", "熏衣草光晕 · 紫罗兰强调", listOf(Color(0xFFD8C8EE), Color(0xFFFBF9FF), Color(0xFF7B61B4))),
    ThemeOption(AppTheme.Night, "深海玻璃", "墓蓝夜色 · 浅蓝高亮", listOf(Color(0xFF10243A), Color(0xFF193551), Color(0xFF83C8F2)))
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
                    SettingItem("下载偏好", TcpgytIcons.Settings) { page = SettingPage.DownloadPrefs },
                    SettingItem("保存位置", TcpgytIcons.Folder) { page = SettingPage.SaveLocation }
                )
            )
            Spacer(Modifier.height(24.dp))
            SettingsGroup(
                title = "隐私",
                items = listOf(
                    SettingItem("Cookie 管理", TcpgytIcons.Shield) { page = SettingPage.Cookie },
                    SettingItem("本地数据", TcpgytIcons.Sliders) { page = SettingPage.LocalData }
                )
            )
            Spacer(Modifier.height(24.dp))
            SettingsGroup(
                title = "应用",
                items = listOf(
                    SettingItem("外观", TcpgytIcons.Spark) { page = SettingPage.Appearance },
                    SettingItem("关于与支持", TcpgytIcons.Info) { page = SettingPage.About }
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
                Text("‹ 返回", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
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
                    onAutoStart = { autoStart = it; onSnack(if (it) "已开启自动开始下载" else "已关闭自动开始下载") }
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
    val theme = LocalAppTheme.current
    Text(
        title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Black,
        color = themeSectionLabel(theme),
        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
    )
    items.forEach { item ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.72f)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = item.onClick)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(themePrimarySoft(theme)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    item.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = TcpgytIcons.Chevron,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

// ── 子页共用积木（对照画布 tcp-glass 外层卡 + bg-white/70 内层分区 + primary-pale 信息横幅）──

/** 外层玻璃卡 = tcp-glass rounded-[26px] p-5。 */
@Composable
private fun GlassCard(spacing: Dp = 16.dp, content: @Composable ColumnScope.() -> Unit) {
    val theme = LocalAppTheme.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = themeGlass(theme)),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, themeHairline(theme)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(spacing),
            content = content
        )
    }
}

/** 内层分区卡 = bg-white/70 rounded-[22px]，可选带下边框的 section-label 头。 */
@Composable
private fun WhiteSection(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    val theme = LocalAppTheme.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(themeCardSurface(theme))
    ) {
        if (title != null) {
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = themeSectionLabel(theme),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            )
            HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
        }
        content()
    }
}

/** 信息横幅 = primary-pale 底 + primary-soft 圆形图标底 + 线条图标。 */
@Composable
private fun InfoBanner(icon: ImageVector, title: String, body: String) {
    val theme = LocalAppTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(themePrimaryPale(theme))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(themePrimarySoft(theme)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                body,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = themeMuted(theme),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/** 填充按钮（primary-wash / neutral-action / primary-soft 底）。 */
@Composable
private fun FillButton(text: String, bg: Color, fg: Color, corner: Dp, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Black, color = fg)
    }
}

@Composable
private fun DownloadPrefsPage(
    defaultType: String, videoQuality: String, audioFormat: String,
    networkPref: String, autoStart: Boolean,
    onDefaultType: (String) -> Unit, onVideoQuality: (String) -> Unit,
    onAudioFormat: (String) -> Unit, onNetworkPref: (String) -> Unit,
    onAutoStart: (Boolean) -> Unit
) {
    val theme = LocalAppTheme.current
    var localPrefPicker by remember { mutableStateOf<String?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GlassCard {
            Text(
                "这些偏好仅影响后续创建的原型任务，不会修改设备网络或真实下载设置。",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = themeMuted(theme),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            WhiteSection("默认类型") {
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
            WhiteSection("格式与网络") {
                val rows = listOf("默认视频质量" to videoQuality, "默认音频格式" to audioFormat, "网络偏好" to networkPref)
                rows.forEachIndexed { i, (label, value) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { localPrefPicker = label }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Icon(TcpgytIcons.Chevron, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                        }
                    }
                    if (i < rows.size - 1) HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
                }
            }
            // 自动开始下载 —— 画布药丸（非 Material Switch）
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(themeCardSurface(theme))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("自动开始下载", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text("创建原型任务后自动进入下载中", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                }
                val pillBg = if (autoStart) themePrimaryWash(theme) else Color(0xFFEDF0F2)
                val pillFg = if (autoStart) MaterialTheme.colorScheme.primary else themeMuted(theme)
                Text(
                    if (autoStart) "已开启" else "已关闭",
                    modifier = Modifier.clip(CircleShape).background(pillBg).clickable { onAutoStart(!autoStart) }.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = pillFg
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    // 偏好选择弹窗 —— 统一走 TcpgytBottomSheet
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
    val theme = LocalAppTheme.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GlassCard {
            Text(
                "保存位置仅用于当前原型展示，不会读取、创建或写入设备文件夹。",
                fontSize = 12.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            WhiteSection("当前原型位置") {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(themePrimarySoft(theme)),
                        contentAlignment = Alignment.Center
                    ) { Icon(TcpgytIcons.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                    Column(Modifier.weight(1f)) {
                        Text(savePath, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("视频和音频的原型保存位置", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
            WhiteSection {
                Row(
                    Modifier.fillMaxWidth().clickable(onClick = onSelectRequest).padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("选择文件夹", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text("系统文件夹选择器尚未接入", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                    }
                    Icon(TcpgytIcons.Chevron, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                }
                HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
                Box(Modifier.padding(12.dp)) {
                    FillButton("恢复默认位置", themePrimarySoft(theme), MaterialTheme.colorScheme.primary, 16.dp, Modifier.fillMaxWidth(), onRestoreRequest)
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
    val theme = LocalAppTheme.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GlassCard {
            InfoBanner(TcpgytIcons.Shield, "本地 Cookie 状态占位", "不会显示、保存、导入或上传任何 Cookie 内容。")
            WhiteSection {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("本地状态", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text(if (enabled) "已启用 · ${items.size} 个状态占位" else "未启用", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                    }
                    val bg = if (enabled) themePrimaryWash(theme) else Color(0xFFEDF0F2)
                    val fg = if (enabled) MaterialTheme.colorScheme.primary else themeMuted(theme)
                    Text(
                        if (enabled) "已启用" else "未启用",
                        modifier = Modifier.clip(CircleShape).background(bg).padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp, fontWeight = FontWeight.Black, color = fg
                    )
                }
                if (enabled) {
                    HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
                    if (items.isEmpty()) {
                        Text("尚无本地状态占位。", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
                    } else {
                        items.forEachIndexed { i, item ->
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("本地状态占位 ${i + 1}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("不含任何 Cookie 内容", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                                }
                                Text("删除", modifier = Modifier.clickable { onDelete(item) }, fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                            if (i < items.size - 1) HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
                        }
                    }
                }
            }
            if (enabled) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FillButton("添加状态占位", themePrimaryWash(theme), MaterialTheme.colorScheme.primary, 18.dp, Modifier.weight(1f), onAdd)
                    FillButton("清空全部", themeNeutralAction(theme), MaterialTheme.colorScheme.onSurface, 18.dp, Modifier.weight(1f), onClearAll)
                }
            } else {
                FillButton("启用本地状态占位", themePrimaryWash(theme), MaterialTheme.colorScheme.primary, 18.dp, Modifier.fillMaxWidth()) { onToggle(true) }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LocalDataPage(temporaryFilesPresent: Boolean, historyCleared: Boolean, onAction: (String) -> Unit) {
    val theme = LocalAppTheme.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GlassCard {
            InfoBanner(TcpgytIcons.Info, "原型数据管理", "操作只更新当前原型状态，不删除设备媒体文件、浏览器数据或 Cookie 内容。")
            WhiteSection("清理操作") {
                val actions = listOf(
                    CleanAction("临时文件", "清理临时文件", if (temporaryFilesPresent) "待清理" else "已清理", "低风险 · 仅清除原型临时标记"),
                    CleanAction("下载历史", "清空下载历史", if (historyCleared) "已清空" else "任务记录", "中风险 · 隐藏已结束的原型任务"),
                    CleanAction("应用设置", "重置应用设置", "原型偏好", "低风险 · 恢复下载偏好默认值")
                )
                actions.forEachIndexed { i, a ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onAction(a.action) }.padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(a.label, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            Text(a.detail, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                            Text(
                                a.status,
                                modifier = Modifier.padding(top = 6.dp).clip(CircleShape).background(themePrimarySoft(theme)).padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text("${a.action} 〉", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp))
                    }
                    if (i < actions.size - 1) HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AppearancePage(current: AppTheme, onPick: (AppTheme) -> Unit) {
    val theme = LocalAppTheme.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GlassCard(spacing = 12.dp) {
            Text(
                "主题偏好仅保存在此浏览器的本地原型数据中；不上传、不与账号同步。",
                fontSize = 13.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme)
            )
            themeOptions.forEach { option ->
                val selected = current == option.theme
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (selected) themePrimaryPale(theme) else themeCardSurface(theme))
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else themeHairline(theme), RoundedCornerShape(22.dp))
                        .clickable { onPick(option.theme) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    ) {
                        option.swatches.forEach { color -> Box(Modifier.weight(1f).fillMaxHeight().background(color)) }
                    }
                    Column(Modifier.weight(1f)) {
                        Text(option.title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text(option.detail, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                    }
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .border(1.5.dp, if (selected) MaterialTheme.colorScheme.primary else Color(0xFFD9D5DA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) Icon(TcpgytIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AboutPage() {
    val theme = LocalAppTheme.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GlassCard {
            Row(
                Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(20.dp)).background(themePrimarySoft(theme)),
                    contentAlignment = Alignment.Center
                ) { Icon(TcpgytIcons.Download, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(25.dp)) }
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("TCPGYT", fontSize = 21.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.8).sp)
                        Text(
                            "原型版",
                            modifier = Modifier.clip(CircleShape).background(themePrimaryWash(theme)).padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text("本地优先的下载管理界面原型", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeMuted(theme), modifier = Modifier.padding(top = 4.dp))
                }
            }
            WhiteSection("应用信息") {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text("开发者", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeMuted(theme))
                    Text("TCPG007014 (YaR)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
                }
                HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
                Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text("联系邮箱", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeMuted(theme))
                    Text("ChengYuan.tcpg@gnail.com", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                }
                HorizontalDivider(color = themeHairline(theme), thickness = 1.dp)
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Column(Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Text("隐私", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeMuted(theme))
                        Text("数据仅保存在本机", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
                    }
                    VerticalDivider(color = themeHairline(theme), thickness = 1.dp)
                    Column(Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Text("组件", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeMuted(theme))
                        Text("后续显示第三方声明", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
            InfoBanner(TcpgytIcons.Info, "支持开发暂未开放", "尚未配置捐赠信息，因此不会显示链接或跳转入口。")
        }
        Spacer(Modifier.height(24.dp))
    }
}
