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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class DemoFile(val id: Int, val name: String, val type: String, val meta: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(padding: PaddingValues, onSnack: (String) -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("全部") }
    var sortBy by remember { mutableStateOf("最近添加") }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<DemoFile?>(null) }
    var menuTarget by remember { mutableStateOf<DemoFile?>(null) }
    var files by remember {
        mutableStateOf(
            listOf(
                DemoFile(1, "城市电台片段.mp3", "音频", "12.4 MB · 今天"),
                DemoFile(2, "旅行影像精选.mp4", "视频", "84.6 MB · 昨天")
            )
        )
    }

    val shown = files
        .filter { (filter == "全部" || it.type == filter) && it.name.contains(query, true) }
        .let { list -> if (sortBy == "名称") list.sortedBy { it.name } else list }

    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("文件", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
        Text(
            if (files.isEmpty()) "还没有完成的原型文件" else "已完成 ${files.size} 个原型文件",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        // Search bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) { Text("📁", style = MaterialTheme.typography.bodyMedium) }
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
                    TextButton(onClick = { query = "" }) { Text("清除", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Filter + sort row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("全部", "音频", "视频").forEach { label ->
                    FilterChip(selected = filter == label, onClick = { filter = label }, label = { Text(label) })
                }
            }
            Box {
                TextButton(onClick = { sortMenuOpen = true }) {
                    Text("排序 · $sortBy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                    listOf("最近添加", "名称").forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) }, onClick = { sortBy = opt; sortMenuOpen = false })
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("显示文件", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${shown.size} 项", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.height(8.dp))
                    Text("没有匹配的原型文件", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (query.isNotEmpty() || filter != "全部") {
                        TextButton(onClick = { query = ""; filter = "全部" }) { Text("清除筛选") }
                    }
                }
            }
        } else {
            shown.forEach { file ->
                FileCard(
                    file = file,
                    onClick = { menuTarget = file }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    // File operations bottom sheet
    menuTarget?.let { file ->
        ModalBottomSheet(onDismissRequest = { menuTarget = null }) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${file.type} · ${file.meta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                listOf(
                    "📄  打开文件" to { menuTarget = null; onSnack("正在打开原型文件") },
                    "📂  查看位置" to { menuTarget = null; onSnack("Download / TCPGYT（原型位置）") },
                    "🗑️  移除记录" to { files = files.filter { it.id != file.id }; menuTarget = null; onSnack("已移除任务记录") }
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
                    Text("🗑️  删除原型文件", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
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
            ) { Text("✓", style = MaterialTheme.typography.titleLarge, color = Color(0xFF7EBE9A), fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${file.type} · ${file.meta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("⋯", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
