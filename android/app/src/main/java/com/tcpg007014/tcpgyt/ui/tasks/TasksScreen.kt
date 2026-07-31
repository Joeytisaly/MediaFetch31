package com.tcpg007014.tcpgyt.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class ParseState { Idle, Loading, Result, Error }
private enum class TaskStatus { Queued, Downloading, Paused, Done, Failed, Cancelled }

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
    var filterSheet by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf("进行中") }
    var selectedTaskId by remember { mutableStateOf<Int?>(null) }
    var tasks by remember {
        mutableStateOf(
            listOf(
                DemoTask(1, "城市夜行", "视频 · 72%", TaskStatus.Downloading, 0.72f),
                DemoTask(2, "雨天书店", "MP3 · 320 kbps", TaskStatus.Paused, 0.48f),
                DemoTask(3, "河岸片段", "MP4 · 720P", TaskStatus.Queued, 0f),
                DemoTask(4, "周末散步", "M4A · 128 kbps", TaskStatus.Done, 1f),
                DemoTask(5, "海岸线片段", "720P · MP4", TaskStatus.Failed, 0f)
            )
        )
    }

    LaunchedEffect(state) {
        if (state == ParseState.Loading) {
            kotlinx.coroutines.delay(700)
            state = if (link.contains("fail", ignoreCase = true)) ParseState.Error else ParseState.Result
        }
    }

    val filterCounts = mapOf(
        "进行中" to tasks.count { it.status in listOf(TaskStatus.Downloading, TaskStatus.Paused, TaskStatus.Queued) },
        "已完成" to tasks.count { it.status == TaskStatus.Done },
        "下载失败" to tasks.count { it.status == TaskStatus.Failed },
        "已取消" to tasks.count { it.status == TaskStatus.Cancelled }
    )

    val shown = tasks.filter {
        when (activeFilter) {
            "进行中" -> it.status in listOf(TaskStatus.Downloading, TaskStatus.Paused, TaskStatus.Queued)
            "已完成" -> it.status == TaskStatus.Done
            "下载失败" -> it.status == TaskStatus.Failed
            "已取消" -> it.status == TaskStatus.Cancelled
            else -> true
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("TCPGYT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("下载", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(16.dp))

        // Single-row input card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) { Text("🔗", style = MaterialTheme.typography.titleMedium) }
                BasicTextField(
                    value = link,
                    onValueChange = { link = it; state = ParseState.Idle },
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        Box {
                            if (link.isEmpty()) {
                                Text("粘贴链接", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            }
                            inner()
                        }
                    }
                )
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (link.isNotBlank() && state != ParseState.Loading) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .clickable(enabled = link.isNotBlank() && state != ParseState.Loading) { state = ParseState.Loading },
                    contentAlignment = Alignment.Center
                ) {
                    if (state == ParseState.Loading) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                    } else {
                        Text("→", style = MaterialTheme.typography.titleLarge, color = if (link.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    }
                }
            }
        }
        if (!link.isBlank() || state != ParseState.Idle) {
            Text(
                if (link.isBlank()) "粘贴你有权访问的资源链接" else "链接将进入本地原型准备流程",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
            )
        }

        if (state == ParseState.Error) {
            Spacer(Modifier.height(10.dp))
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("解析失败", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                        Text("链接无效或不支持，请检查后重试", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { state = ParseState.Idle; link = "" }) { Text("重置") }
                }
            }
        }

        if (state == ParseState.Result) {
            Spacer(Modifier.height(10.dp))
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.78f)),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("解析结果（演示）", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text("旅行影像精选", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("本地原型演示，不会发起网络请求", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { formatSheet = true }, modifier = Modifier.weight(1f)) {
                            Text(selectedFormat, maxLines = 1)
                        }
                        Button(
                            onClick = {
                                tasks = listOf(DemoTask(tasks.size + 1, "旅行影像精选", selectedFormat, TaskStatus.Queued, 0f)) + tasks
                                state = ParseState.Idle; link = ""
                                onSnack("已加入下载队列")
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("添加到队列") }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Section header + filter pill
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("任务", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Surface(
                onClick = { filterSheet = true },
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.65f)
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("筛选", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(activeFilter, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("${filterCounts[activeFilter] ?: 0}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✨", style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.height(8.dp))
                    Text("没有${activeFilter}任务", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("切换筛选条件，或在上方粘贴链接创建任务", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(shown, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { selectedTaskId = task.id },
                        onAction = { action ->
                            tasks = tasks.map { t ->
                                if (t.id != task.id) t
                                else when (action) {
                                    "pause"  -> t.copy(status = TaskStatus.Paused)
                                    "resume", "retry" -> t.copy(status = TaskStatus.Downloading)
                                    "cancel" -> t.copy(status = TaskStatus.Cancelled)
                                    else -> t
                                }
                            }
                            onSnack(when (action) {
                                "pause"  -> "原型任务已暂停"
                                "resume" -> "原型任务继续下载"
                                "cancel" -> "原型任务已取消"
                                "retry"  -> "原型任务重试中"
                                else -> "已更新任务状态"
                            })
                        }
                    )
                }
            }
        }
    }

    if (formatSheet) {
        FormatSheet(current = selectedFormat, onSelect = { selectedFormat = it; formatSheet = false }, onDismiss = { formatSheet = false })
    }
    if (filterSheet) {
        FilterSheet(active = activeFilter, counts = filterCounts, onSelect = { activeFilter = it; filterSheet = false }, onDismiss = { filterSheet = false })
    }
    selectedTaskId?.let { id ->
        tasks.find { it.id == id }?.let { task ->
            TaskDetailSheet(
                task = task,
                onDismiss = { selectedTaskId = null },
                onSimulateDone = {
                    tasks = tasks.map { if (it.id == id) it.copy(status = TaskStatus.Done, progress = 1f) else it }
                    selectedTaskId = null; onSnack("原型任务已模拟完成")
                },
                onSimulateFail = {
                    tasks = tasks.map { if (it.id == id) it.copy(status = TaskStatus.Failed) else it }
                    selectedTaskId = null; onSnack("原型任务已模拟失败")
                },
                onAction = { action ->
                    tasks = tasks.map { t ->
                        if (t.id != id) t
                        else when (action) {
                            "pause"  -> t.copy(status = TaskStatus.Paused)
                            "resume", "retry" -> t.copy(status = TaskStatus.Downloading)
                            "cancel" -> t.copy(status = TaskStatus.Cancelled)
                            else -> t
                        }
                    }
                    selectedTaskId = null
                    if (action == "open") onSnack("正在打开原型文件") else onSnack("已更新原型任务状态")
                }
            )
        }
    }
}

@Composable
private fun TaskCard(task: DemoTask, onClick: () -> Unit, onAction: (String) -> Unit) {
    val iconChar = when (task.status) {
        TaskStatus.Downloading, TaskStatus.Queued -> "↓"
        TaskStatus.Paused    -> "⏸"
        TaskStatus.Done      -> "✓"
        TaskStatus.Failed    -> "!"
        TaskStatus.Cancelled -> "×"
    }
    val iconBg = when (task.status) {
        TaskStatus.Downloading, TaskStatus.Paused, TaskStatus.Queued -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
        TaskStatus.Done      -> Color(0xFFE8F5EC)
        TaskStatus.Failed    -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        TaskStatus.Cancelled -> MaterialTheme.colorScheme.surfaceVariant
    }
    val iconTint = when (task.status) {
        TaskStatus.Done      -> Color(0xFF5B9A77)
        TaskStatus.Cancelled -> MaterialTheme.colorScheme.onSurfaceVariant
        else                 -> MaterialTheme.colorScheme.primary
    }
    val statusLabel = when (task.status) {
        TaskStatus.Downloading -> "下载中"; TaskStatus.Paused -> "已暂停"
        TaskStatus.Queued -> "排队中"; TaskStatus.Done -> "已完成"
        TaskStatus.Failed -> "下载失败"; TaskStatus.Cancelled -> "已取消"
    }
    val actionLabel = when (task.status) {
        TaskStatus.Downloading -> "暂停"; TaskStatus.Paused -> "继续"
        TaskStatus.Queued -> "取消"; TaskStatus.Done -> "详情"
        TaskStatus.Failed -> "重试"; TaskStatus.Cancelled -> ""
    }
    val actionKey = when (task.status) {
        TaskStatus.Downloading -> "pause"; TaskStatus.Paused -> "resume"
        TaskStatus.Queued -> "cancel"; TaskStatus.Failed -> "retry"
        else -> ""
    }
    val active = task.status == TaskStatus.Downloading || task.status == TaskStatus.Paused

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(iconBg),
                    contentAlignment = Alignment.Center
                ) { Text(iconChar, style = MaterialTheme.typography.titleLarge, color = iconTint, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(task.format, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (actionLabel.isNotEmpty()) {
                    Surface(
                        onClick = { if (actionKey.isNotEmpty()) onAction(actionKey) else onClick() },
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(actionLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when (task.status) {
                        TaskStatus.Done   -> Color(0xFFE8F5EC)
                        TaskStatus.Failed -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else              -> Color.White.copy(alpha = 0.7f)
                    }
                ) {
                    Text(
                        statusLabel,
                        style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                        color = when (task.status) {
                            TaskStatus.Done   -> Color(0xFF5B9A77)
                            TaskStatus.Failed -> MaterialTheme.colorScheme.primary
                            else              -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                if (active) Text("${(task.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (active || task.status == TaskStatus.Queued) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { task.progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(active: String, counts: Map<String, Int>, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val options = listOf(
        Triple("进行中", "↓", "排队中、下载中、已暂停"),
        Triple("已完成", "✓", "已加入文件库"),
        Triple("下载失败", "!", "下载未能完成"),
        Triple("已取消", "×", "已手动取消")
    )
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("筛选任务", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text("按当前原型任务状态查看", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            options.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (name, icon, detail) ->
                        val selected = active == name
                        Card(
                            modifier = Modifier.weight(1f).clickable { onSelect(name) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = if (selected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(32.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) { Text(icon, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Text("${counts[name] ?: 0} 项", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailSheet(
    task: DemoTask,
    onDismiss: () -> Unit,
    onSimulateDone: () -> Unit,
    onSimulateFail: () -> Unit,
    onAction: (String) -> Unit
) {
    val active = task.status == TaskStatus.Downloading || task.status == TaskStatus.Paused
    val ext = when { "MP3" in task.format -> "mp3"; "M4A" in task.format || "音频" in task.format -> "m4a"; "WebM" in task.format -> "webm"; else -> "mp4" }
    val statusText = when (task.status) {
        TaskStatus.Downloading -> "下载中"; TaskStatus.Paused -> "已暂停"; TaskStatus.Queued -> "排队中"
        TaskStatus.Done -> "已完成"; TaskStatus.Failed -> "下载失败"; TaskStatus.Cancelled -> "已取消"
    }
    val primaryAction = when (task.status) {
        TaskStatus.Downloading -> "暂停下载"; TaskStatus.Paused -> "继续下载"
        TaskStatus.Failed -> "重试下载"; TaskStatus.Done -> "打开文件"
        TaskStatus.Queued -> "取消任务"; TaskStatus.Cancelled -> "关闭详情"
    }
    val primaryKey = when (task.status) {
        TaskStatus.Downloading -> "pause"; TaskStatus.Paused -> "resume"
        TaskStatus.Failed -> "retry"; TaskStatus.Done -> "open"
        TaskStatus.Queued -> "cancel"; TaskStatus.Cancelled -> ""
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(statusText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onDismiss) { Text("关闭") }
            }
            Spacer(Modifier.height(14.dp))

            if (active) {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        LinearProgressIndicator(
                            progress = { task.progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            "${(task.progress * 100).toInt()}% · ${if (task.status == TaskStatus.Paused) "任务已暂停" else "正在下载"}",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            DetailSection("文件") {
                listOf("格式" to task.format, "模拟文件名" to "${task.title}.$ext", "保存位置" to "Download / TCPGYT").forEachIndexed { i, (label, value) ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                    if (i < 2) HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                }
            }

            Spacer(Modifier.height(10.dp))

            DetailSection("来源") {
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("平台", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("示例媒体平台", style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                Text("https://example.com/media", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = { if (primaryKey.isNotEmpty()) onAction(primaryKey) else onDismiss() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) { Text(primaryAction, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            if (task.status == TaskStatus.Downloading) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onSimulateDone, modifier = Modifier.weight(1f)) { Text("模拟完成") }
                    OutlinedButton(onClick = onSimulateFail, modifier = Modifier.weight(1f)) { Text("模拟失败") }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormatSheet(current: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    var advanced by remember { mutableStateOf(false) }
    var customCode by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("选择格式", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                TextButton(onClick = { advanced = !advanced }) { Text(if (advanced) "简单模式" else "高级模式") }
            }
            Spacer(Modifier.height(12.dp))
            if (advanced) {
                OutlinedTextField(value = customCode, onValueChange = { customCode = it }, label = { Text("格式码") }, placeholder = { Text("bestvideo+bestaudio") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Button(onClick = { if (customCode.isNotBlank()) onSelect("自定义 · $customCode") }, modifier = Modifier.fillMaxWidth()) { Text("使用此格式码") }
            } else {
                Text("音频", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                listOf("音频 · MP3", "音频 · M4A").forEach { fmt ->
                    ListItem(headlineContent = { Text(fmt) }, trailingContent = { if (current == fmt) Text("已选", color = MaterialTheme.colorScheme.primary) }, modifier = Modifier.clickable { onSelect(fmt) })
                    HorizontalDivider(thickness = 0.5.dp)
                }
                Spacer(Modifier.height(10.dp))
                Text("视频", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                listOf("视频 · 1080P", "视频 · 720P", "视频 · 480P").forEach { fmt ->
                    ListItem(headlineContent = { Text(fmt) }, trailingContent = { if (current == fmt) Text("已选", color = MaterialTheme.colorScheme.primary) }, modifier = Modifier.clickable { onSelect(fmt) })
                    HorizontalDivider(thickness = 0.5.dp)
                }
            }
        }
    }
}
