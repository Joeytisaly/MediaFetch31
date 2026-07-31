package com.tcpg007014.tcpgyt.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class ParseState { Idle, Loading, Result, Error }
private enum class TaskStatus { Queued, Downloading, Paused, Done, Failed, Cancelled }
private enum class StatusFilter { All, Active, Done, Failed }

private data class DemoTask(
    val id: Int,
    val title: String,
    val format: String,
    val status: TaskStatus,
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(padding: PaddingValues, onSnack: (String) -> Unit = {}) {
    var link by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(ParseState.Idle) }
    var selectedFormat by remember { mutableStateOf("音频 · MP3") }
    var formatSheet by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf(StatusFilter.All) }
    var tasks by remember {
        mutableStateOf(
            listOf(
                DemoTask(1, "晚风现场录音", "MP3 · 音频", TaskStatus.Downloading, 0.42f),
                DemoTask(2, "城市电台片段", "MP3 · 音频", TaskStatus.Done, 1f)
            )
        )
    }

    LaunchedEffect(state) {
        if (state == ParseState.Loading) {
            kotlinx.coroutines.delay(700)
            state = if (link.contains("fail", ignoreCase = true)) ParseState.Error else ParseState.Result
        }
    }

    val shown = tasks.filter {
        when (activeFilter) {
            StatusFilter.All -> true
            StatusFilter.Active -> it.status == TaskStatus.Downloading || it.status == TaskStatus.Queued || it.status == TaskStatus.Paused
            StatusFilter.Done -> it.status == TaskStatus.Done
            StatusFilter.Failed -> it.status == TaskStatus.Failed || it.status == TaskStatus.Cancelled
        }
    }

    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("下载任务", style = MaterialTheme.typography.headlineMedium)
        Text("粘贴你有权下载的媒体链接", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it; state = ParseState.Idle },
                    label = { Text("粘贴链接") },
                    placeholder = { Text("https://…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { formatSheet = true },
                        modifier = Modifier.weight(1f)
                    ) { Text(selectedFormat, maxLines = 1) }
                    Button(
                        onClick = {
                            if (link.isBlank()) onSnack("请先粘贴有效链接")
                            else state = ParseState.Loading
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("解析") }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        when (state) {
            ParseState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
            ParseState.Error -> ElevatedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("解析失败", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                        Text("链接无效或不支持，请检查后重试", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { state = ParseState.Idle; link = "" }) { Text("重置") }
                }
            }
            ParseState.Result -> ParseResult(
                format = selectedFormat,
                onAdd = {
                    val newTask = DemoTask(tasks.size + 1, "旅行影像精选", selectedFormat, TaskStatus.Queued, 0f)
                    tasks = listOf(newTask) + tasks
                    state = ParseState.Idle
                    link = ""
                    onSnack("已加入下载队列")
                }
            )
            else -> Unit
        }

        Spacer(Modifier.height(16.dp))

        PrimaryScrollableTabRow(
            selectedTabIndex = StatusFilter.entries.indexOf(activeFilter),
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatusFilter.entries.forEach { f ->
                Tab(
                    selected = activeFilter == f,
                    onClick = { activeFilter = f },
                    text = {
                        Text(
                            when (f) {
                                StatusFilter.All -> "全部"
                                StatusFilter.Active -> "进行中"
                                StatusFilter.Done -> "已完成"
                                StatusFilter.Failed -> "失败/取消"
                            }
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                Text("暂无任务", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(shown, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onPauseResume = {
                            tasks = tasks.map {
                                if (it.id == task.id) it.copy(
                                    status = if (it.status == TaskStatus.Paused) TaskStatus.Downloading else TaskStatus.Paused
                                ) else it
                            }
                        },
                        onCancel = {
                            tasks = tasks.map {
                                if (it.id == task.id) it.copy(status = TaskStatus.Cancelled) else it
                            }
                        }
                    )
                }
            }
        }
    }

    if (formatSheet) {
        FormatSheet(
            current = selectedFormat,
            onSelect = { selectedFormat = it; formatSheet = false },
            onDismiss = { formatSheet = false }
        )
    }
}

@Composable
private fun ParseResult(format: String, onAdd: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("解析结果（演示）", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text("旅行影像精选", style = MaterialTheme.typography.titleLarge)
            Text("本地原型演示，不会发起网络请求", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) { Text("添加到任务队列") }
        }
    }
}

@Composable
private fun TaskCard(task: DemoTask, onPauseResume: () -> Unit, onCancel: () -> Unit) {
    val statusLabel = when (task.status) {
        TaskStatus.Queued -> "排队中"
        TaskStatus.Downloading -> "下载中"
        TaskStatus.Paused -> "已暂停"
        TaskStatus.Done -> "已完成"
        TaskStatus.Failed -> "下载失败"
        TaskStatus.Cancelled -> "已取消"
    }
    val active = task.status == TaskStatus.Downloading || task.status == TaskStatus.Paused

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(statusLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                    Text(task.format, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (active) {
                    TextButton(onClick = onPauseResume) {
                        Text(if (task.status == TaskStatus.Paused) "继续" else "暂停")
                    }
                    TextButton(onClick = onCancel) {
                        Text("取消", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            if (active || task.status == TaskStatus.Queued) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { task.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "${(task.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormatSheet(current: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    var advanced by remember { mutableStateOf(false) }
    var customCode by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("选择格式", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { advanced = !advanced }) {
                    Text(if (advanced) "简单模式" else "高级模式")
                }
            }
            Spacer(Modifier.height(12.dp))
            if (advanced) {
                Text("自定义格式码", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customCode,
                    onValueChange = { customCode = it },
                    label = { Text("格式码") },
                    placeholder = { Text("bestvideo+bestaudio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { if (customCode.isNotBlank()) onSelect("自定义 · $customCode") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("使用此格式码") }
            } else {
                Text("音频", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                listOf("音频 · MP3", "音频 · M4A").forEach { fmt ->
                    ListItem(
                        headlineContent = { Text(fmt) },
                        trailingContent = { if (current == fmt) Text("已选", color = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                }
                Spacer(Modifier.height(12.dp))
                Text("视频", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                listOf("视频 · 1080P", "视频 · 720P", "视频 · 480P").forEach { fmt ->
                    ListItem(
                        headlineContent = { Text(fmt) },
                        trailingContent = { if (current == fmt) Text("已选", color = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
