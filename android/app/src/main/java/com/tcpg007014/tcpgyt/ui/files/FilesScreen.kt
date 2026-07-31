package com.tcpg007014.tcpgyt.ui.files

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class DemoFile(val name: String, val type: String, val meta: String)

@Composable
fun FilesScreen(padding: PaddingValues, onSnack: (String) -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("全部") }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("最近添加") }
    var deleteTarget by remember { mutableStateOf<DemoFile?>(null) }
    var menuTarget by remember { mutableStateOf<DemoFile?>(null) }
    var files by remember {
        mutableStateOf(
            listOf(
                DemoFile("城市电台片段.mp3", "音频", "12.4 MB · 今天"),
                DemoFile("旅行影像精选.mp4", "视频", "84.6 MB · 昨天")
            )
        )
    }

    val shown = files
        .filter { (filter == "全部" || it.type == filter) && it.name.contains(query, true) }
        .let { list -> if (sortBy == "名称") list.sortedBy { it.name } else list }

    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("全部文件", style = MaterialTheme.typography.headlineMedium)
        Text("仅展示本地演示数据，不读取设备文件", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            label = { Text("搜索文件") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("全部", "音频", "视频").forEach {
                    FilterChip(selected = filter == it, onClick = { filter = it }, label = { Text(it) })
                }
            }
            Box {
                TextButton(onClick = { sortMenuOpen = true }) { Text(sortBy) }
                DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                    listOf("最近添加", "名称").forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) }, onClick = { sortBy = opt; sortMenuOpen = false })
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        if (shown.isEmpty()) {
            Empty { query = ""; filter = "全部" }
        } else {
            shown.forEach { file ->
                FileCard(
                    file = file,
                    menuOpen = menuTarget == file,
                    onMenuOpen = { menuTarget = file },
                    onMenuDismiss = { menuTarget = null },
                    onShare = { menuTarget = null; onSnack("分享功能暂未接入") },
                    onDelete = { menuTarget = null; deleteTarget = file }
                )
            }
        }
    }

    deleteTarget?.let { file ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("删除文件") },
            text = { Text("确定要删除「${file.name}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    files = files.filter { it != file }
                    deleteTarget = null
                    onSnack("文件已删除")
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun FileCard(
    file: DemoFile,
    menuOpen: Boolean,
    onMenuOpen: () -> Unit,
    onMenuDismiss: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium)
                Text("${file.type} · ${file.meta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box {
                TextButton(onClick = onMenuOpen) {
                    Text("⋮", style = MaterialTheme.typography.titleLarge)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = onMenuDismiss) {
                    DropdownMenuItem(text = { Text("分享") }, onClick = onShare)
                    DropdownMenuItem(
                        text = { Text("删除", color = MaterialTheme.colorScheme.error) },
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun Empty(clear: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("没有匹配的文件", style = MaterialTheme.typography.titleMedium)
        Text("试试更换关键词或筛选条件", style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = clear) { Text("清除筛选") }
    }
}
