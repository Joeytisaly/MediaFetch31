package com.tcpg007014.tcpgyt.ui.files

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.components.SheetSection
import com.tcpg007014.tcpgyt.ui.components.TcpgytBottomSheet
import com.tcpg007014.tcpgyt.ui.components.TcpgytIcons
import com.tcpg007014.tcpgyt.ui.components.TcpgytSegmented
import com.tcpg007014.tcpgyt.ui.theme.LocalAppTheme
import com.tcpg007014.tcpgyt.ui.theme.themePrimarySoft

private data class DemoFile(
    val id: Int,
    val name: String,
    val type: String,
    val meta: String,
    val format: String,
    val platform: String = "示例媒体平台",
    val url: String = "https://example.com/media/example",
    val completedAt: String = "2026-07-30 14:20"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(padding: PaddingValues, onSnack: (String) -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("全部") }
    var sortBy by remember { mutableStateOf("最近添加") }
    var deleteTarget by remember { mutableStateOf<DemoFile?>(null) }
    var menuTarget by remember { mutableStateOf<DemoFile?>(null) }
    var detailTarget by remember { mutableStateOf<DemoFile?>(null) }
    val clipboard = LocalClipboardManager.current

    var files by remember {
        mutableStateOf(
            listOf(
                DemoFile(
                    id = 1,
                    name = "城市电台片段.mp3",
                    type = "音频",
                    meta = "12.4 MB · 今天",
                    format = "MP3 · 320 kbps",
                    url = "https://example.com/media/city-radio",
                    completedAt = "2026-07-31 09:14"
                ),
                DemoFile(
                    id = 2,
                    name = "旅行影像精选.mp4",
                    type = "视频",
                    meta = "84.6 MB · 昨天",
                    format = "视频 · 1080P",
                    url = "https://example.com/media/travel-clips",
                    completedAt = "2026-07-30 14:20"
                )
            )
        )
    }

    val shown = files
        .filter { (filter == "全部" || it.type == filter) && it.name.contains(query, true) }
        .let { list -> if (sortBy == "名称") list.sortedBy { it.name } else list }

    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("文件", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
        Text(
            if (files.isEmpty()) "还没有完成的原型文件" else "已完成 ${files.size} 个原型文件",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("全部文件", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("仅显示当前原型文件库中的项目", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // 排序 = 切换按钮（对照画布：点按在 最近添加↔名称 之间切换，不弹菜单）
            Surface(
                onClick = { sortBy = if (sortBy == "最近添加") "名称" else "最近添加" },
                shape = RoundedCornerShape(50),
                color = themePrimarySoft(LocalAppTheme.current)
            ) {
                Text(
                    "排序 · $sortBy",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) { Icon(TcpgytIcons.FolderSolid, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Unspecified) }
                Spacer(Modifier.width(8.dp))
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("搜索原型文件名") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (query.isNotEmpty()) {
                    TextButton(onClick = { query = "" }) {
                        Text("清除", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // 分段筛选 —— 统一走 TcpgytSegmented（画布 --tcp-primary-muted 底槽）
        TcpgytSegmented(
            options = listOf("全部", "音频", "视频"),
            selected = filter,
            onSelect = { filter = it }
        )

        Spacer(Modifier.height(6.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("显示文件", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${shown.size} 项", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(64.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) { Icon(TcpgytIcons.Folder, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (files.isEmpty()) "还没有完成文件" else "没有匹配的原型文件",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (files.isEmpty()) "完成原型任务后，它会出现在这里。" else "尝试调整关键词或文件类型。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (query.isNotEmpty() || filter != "全部") {
                        TextButton(onClick = { query = ""; filter = "全部" }) { Text("清除筛选") }
                    }
                }
            }
        } else {
            shown.forEach { file ->
                FileCard(file = file, onClick = { detailTarget = file })
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    // File detail sheet — matches React prototype completed-task detail
    detailTarget?.let { file ->
        val displayName = file.name.substringBeforeLast(".")
        TcpgytBottomSheet(onDismiss = { detailTarget = null }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        Text(displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text("已完成", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { detailTarget = null }) { Text("关闭") }
                }
                Spacer(Modifier.height(14.dp))

                // 任务 section
                SheetSection("任务", containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                    Text(
                        "文件已在原型文件库中。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // 文件 section
                SheetSection("文件") {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboard.setText(AnnotatedString(displayName))
                                detailTarget = null; onSnack("已复制标题")
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("复制标题", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                    listOf(
                        "格式" to file.format,
                        "模拟文件名" to file.name,
                        "保存位置" to "Download / TCPGYT",
                        "完成时间" to file.completedAt
                    ).forEachIndexed { i, (label, value) ->
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        if (i < 3) HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // 来源 section
                SheetSection("来源") {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("平台", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(file.platform, style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(file.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), maxLines = 1)
                        TextButton(onClick = {
                            clipboard.setText(AnnotatedString(file.url))
                            onSnack("已复制来源链接")
                        }) { Text("复制链接", style = MaterialTheme.typography.labelSmall) }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { detailTarget = null; onSnack("正在打开原型文件") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("打开文件", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { menuTarget = file; detailTarget = null },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("文件操作", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // File operations bottom sheet
    menuTarget?.let { file ->
        TcpgytBottomSheet(onDismiss = { menuTarget = null }) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${file.type} · ${file.meta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                listOf(
                    "打开文件" to { menuTarget = null; onSnack("正在打开原型文件") },
                    "查看位置" to { menuTarget = null; onSnack("Download / TCPGYT（原型位置）") },
                    "移除记录" to { files = files.filter { it.id != file.id }; menuTarget = null; onSnack("已移除任务记录") }
                ).forEach { (label, action) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { action() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { menuTarget = null; deleteTarget = file },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Text("删除原型文件", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                }
            }
        }
    }

    deleteTarget?.let { file ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("删除这个文件？") },
            text = { Text("这只是原型演示：将从文件库模拟列表移除，不会触碰设备文件。") },
            confirmButton = {
                TextButton(onClick = {
                    files = files.filter { it.id != file.id }
                    deleteTarget = null; onSnack("已从原型文件库移除")
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("取消") } }
        )
    }
}

@Composable
private fun FileCard(file: DemoFile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFE8F5EC)),
                contentAlignment = Alignment.Center
            ) { Icon(TcpgytIcons.Check, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color(0xFF5B9A77)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${file.type} · ${file.meta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(TcpgytIcons.More, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
