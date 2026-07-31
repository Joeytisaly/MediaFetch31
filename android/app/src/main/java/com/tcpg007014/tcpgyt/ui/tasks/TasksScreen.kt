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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcpg007014.tcpgyt.ui.components.TcpgytIcons

private enum class ParseState { Idle, Loading, Result, Error }
private enum class TaskStatus { Queued, Downloading, Paused, Done, Failed, Cancelled }
private enum class MediaKind { Video, Audio }

private fun statusLabel(status: TaskStatus) = when (status) {
    TaskStatus.Downloading -> "下载中"
    TaskStatus.Paused      -> "已暂停"
    TaskStatus.Queued      -> "排队中"
    TaskStatus.Done        -> "已完成"
    TaskStatus.Failed      -> "下载失败"
    TaskStatus.Cancelled   -> "已取消"
}

private data class DemoTask(
    val id: Int,
    val title: String,
    val format: String,
    val status: TaskStatus,
    val progress: Float,
    val completedAt: String = "",
    val failReason: String = ""
)

/** 统一的矢量图标渲染，替代旧版 emoji / 文字符号。 */
@Composable
private fun Ic(icon: ImageVector, tint: Color, size: Dp = 21.dp) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(size), tint = tint)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(padding: PaddingValues, onSnack: (String) -> Unit = {}) {
    var link by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(ParseState.Idle) }
    var formatSheet by remember { mutableStateOf(false) }
    var filterSheet by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf("进行中") }
    var selectedTaskId by remember { mutableStateOf<Int?>(null) }
    var fileOpsTaskId by remember { mutableStateOf<Int?>(null) }
    val clipboard = LocalClipboardManager.current

    var tasks by remember {
        mutableStateOf(
            listOf(
                DemoTask(1, "城市夜行", "视频 · 72%", TaskStatus.Downloading, 0.72f),
                DemoTask(2, "雨天书店", "MP3 · 320 kbps", TaskStatus.Paused, 0.48f),
                DemoTask(3, "河岸片段", "MP4 · 720P", TaskStatus.Queued, 0f),
                DemoTask(4, "周末散步", "M4A · 128 kbps", TaskStatus.Done, 1f, completedAt = "2026-07-30 14:20"),
                DemoTask(5, "海岸线片段", "720P · MP4", TaskStatus.Failed, 0f, failReason = "链接无效")
            )
        )
    }

    LaunchedEffect(state) {
        if (state == ParseState.Loading) {
            kotlinx.coroutines.delay(1000)
            if (link.contains("fail", ignoreCase = true)) {
                state = ParseState.Error
            } else {
                state = ParseState.Result
                formatSheet = true   // 画布行为：解析完成后自动弹出格式选择器
            }
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

    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("TCPGYT", style = MaterialTheme.typography.labelMedium, color = muted, fontWeight = FontWeight.Bold)
        Text("下载", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(16.dp))

        // ── 链接输入卡 ─────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) { Ic(TcpgytIcons.Link, tint = primary) }
                BasicTextField(
                    value = link,
                    onValueChange = { link = it; state = ParseState.Idle },
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    singleLine = true,
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                    cursorBrush = SolidColor(primary),
                    decorationBox = { inner ->
                        Box {
                            if (link.isEmpty()) Text("粘贴链接", style = MaterialTheme.typography.bodyMedium, color = muted.copy(alpha = 0.6f))
                            inner()
                        }
                    }
                )
                val canParse = link.isNotBlank() && state != ParseState.Loading
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (canParse) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable(enabled = canParse) { state = ParseState.Loading },
                    contentAlignment = Alignment.Center
                ) {
                    if (state == ParseState.Loading) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = primary)
                    } else {
                        Ic(TcpgytIcons.Arrow, tint = if (link.isNotBlank()) primary else muted.copy(alpha = 0.3f))
                    }
                }
            }
        }
        if (link.isNotBlank() || state != ParseState.Idle) {
            Text(
                if (link.isBlank()) "粘贴你有权访问的资源链接" else "链接将进入本地原型准备流程",
                style = MaterialTheme.typography.labelSmall,
                color = muted,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
            )
        }

        // ── 解析状态条（画布行为：准备中 / 完成 / 失败）──────────
        if (state == ParseState.Loading || state == ParseState.Result || state == ParseState.Error) {
            Spacer(Modifier.height(12.dp))
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(36.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Ic(
                            when (state) {
                                ParseState.Error -> TcpgytIcons.Info
                                ParseState.Loading -> TcpgytIcons.Spark
                                else -> TcpgytIcons.Check
                            },
                            tint = primary, size = 17.dp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        when (state) {
                            ParseState.Error -> "原型准备失败，未执行真实解析"
                            ParseState.Loading -> "正在模拟准备链接…"
                            else -> "原型准备完成，可以选择格式"
                        },
                        style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    when (state) {
                        ParseState.Result -> TextButton(onClick = { clipboard.setText(AnnotatedString("城市夜行")); onSnack("已复制标题") }) {
                            Text("复制标题", color = primary, fontWeight = FontWeight.Black)
                        }
                        ParseState.Error -> TextButton(onClick = { state = ParseState.Loading }) {
                            Text("重试", color = primary, fontWeight = FontWeight.Black)
                        }
                        else -> {}
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── 任务区 ─────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("任务", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Surface(
                onClick = { filterSheet = true },
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.65f)
            ) {
                Row(
                    Modifier.padding(start = 12.dp, end = 8.dp).padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("筛选", style = MaterialTheme.typography.labelSmall, color = muted)
                    Text(activeFilter, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = primary)
                    Box(Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("${shown.size}", style = MaterialTheme.typography.labelSmall, color = primary, fontWeight = FontWeight.Bold)
                    }
                    Ic(TcpgytIcons.Chevron, tint = muted, size = 15.dp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) { Ic(TcpgytIcons.Spark, tint = primary, size = 18.dp) }
                    Spacer(Modifier.height(10.dp))
                    Text("没有${activeFilter}任务", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text("切换筛选条件，或在上方粘贴链接创建任务", style = MaterialTheme.typography.bodySmall, color = muted)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(shown, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { selectedTaskId = task.id },
                        onAction = { action ->
                            tasks = tasks.map { t ->
                                if (t.id != task.id) t
                                else when (action) {
                                    "pause"           -> t.copy(status = TaskStatus.Paused)
                                    "resume", "retry" -> t.copy(status = TaskStatus.Downloading)
                                    "cancel"          -> t.copy(status = TaskStatus.Cancelled)
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

    // ── 弹层 ─────────────────────────────
    if (formatSheet) {
        FormatSheet(
            onCreate = { format ->
                tasks = listOf(DemoTask(tasks.size + 1, "城市夜行", format, TaskStatus.Downloading, 0.72f)) + tasks
                formatSheet = false; state = ParseState.Idle; link = ""
                onSnack("已创建原型任务 · 未开始真实下载")
            },
            onDismiss = { formatSheet = false }
        )
    }
    if (filterSheet) {
        FilterSheet(
            active = activeFilter,
            counts = filterCounts,
            shown = shown,
            onSelect = { activeFilter = it; filterSheet = false },
            onSelectTask = { id -> filterSheet = false; selectedTaskId = id },
            onDismiss = { filterSheet = false }
        )
    }

    selectedTaskId?.let { id ->
        tasks.find { it.id == id }?.let { task ->
            TaskDetailSheet(
                task = task,
                onDismiss = { selectedTaskId = null },
                onSimulateDone = {
                    tasks = tasks.map { if (it.id == id) it.copy(status = TaskStatus.Done, progress = 1f, completedAt = "2026-07-31") else it }
                    selectedTaskId = null; onSnack("原型任务已模拟完成")
                },
                onSimulateFail = {
                    tasks = tasks.map { if (it.id == id) it.copy(status = TaskStatus.Failed) else it }
                    selectedTaskId = null; onSnack("原型任务已模拟失败")
                },
                onFileOps = { fileOpsTaskId = task.id; selectedTaskId = null },
                onCopyTitle = {
                    clipboard.setText(AnnotatedString(task.title))
                    selectedTaskId = null; onSnack("已复制标题")
                },
                onAction = { action ->
                    tasks = tasks.map { t ->
                        if (t.id != id) t
                        else when (action) {
                            "pause"           -> t.copy(status = TaskStatus.Paused)
                            "resume", "retry" -> t.copy(status = TaskStatus.Downloading)
                            "cancel"          -> t.copy(status = TaskStatus.Cancelled)
                            else -> t
                        }
                    }
                    selectedTaskId = null
                    if (action == "open") onSnack("正在打开原型文件") else onSnack("已更新原型任务状态")
                }
            )
        }
    }

    // 文件操作弹层（来自任务详情「文件操作」）
    fileOpsTaskId?.let { id ->
        tasks.find { it.id == id }?.let { task ->
            ModalBottomSheet(onDismissRequest = { fileOpsTaskId = null }) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(task.format, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    val ops = listOf(
                        Triple(TcpgytIcons.Folder, "打开文件") { fileOpsTaskId = null; onSnack("正在打开原型文件") },
                        Triple(TcpgytIcons.Folder, "查看位置") { fileOpsTaskId = null; onSnack("Download / TCPGYT（原型位置）") },
                        Triple(TcpgytIcons.Info, "移除记录") { tasks = tasks.filter { it.id != id }; fileOpsTaskId = null; onSnack("已移除任务记录") }
                    )
                    ops.forEach { (icon, label, action) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { action() },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Ic(icon, tint = MaterialTheme.colorScheme.primary, size = 20.dp)
                                Spacer(Modifier.width(12.dp))
                                Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: DemoTask, onClick: () -> Unit, onAction: (String) -> Unit) {
    val icon = when (task.status) {
        TaskStatus.Downloading, TaskStatus.Queued -> TcpgytIcons.Download
        TaskStatus.Paused    -> TcpgytIcons.Pause
        TaskStatus.Done      -> TcpgytIcons.Check
        TaskStatus.Failed    -> TcpgytIcons.Info
        TaskStatus.Cancelled -> TcpgytIcons.Power
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
    val detail = when (task.status) {
        TaskStatus.Downloading -> "${(task.progress * 100).toInt()}%"
        TaskStatus.Paused      -> "等待继续"
        TaskStatus.Queued      -> "等待开始"
        TaskStatus.Failed      -> task.failReason.ifEmpty { "暂时无法完成" }
        TaskStatus.Done        -> "已加入原型文件库"
        TaskStatus.Cancelled   -> "任务未进入文件库"
    }
    val actionLabel = when (task.status) {
        TaskStatus.Downloading -> "暂停"; TaskStatus.Paused -> "继续"
        TaskStatus.Queued -> "取消"; TaskStatus.Done -> "详情"
        TaskStatus.Failed -> "重试"; TaskStatus.Cancelled -> "详情"
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
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(iconBg), contentAlignment = Alignment.Center) {
                    Ic(icon, tint = iconTint, size = 24.dp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(task.format, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Surface(
                    onClick = { if (actionKey.isNotEmpty()) onAction(actionKey) else onClick() },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(actionLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
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
                        statusLabel(task.status),
                        style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black,
                        color = when (task.status) {
                            TaskStatus.Done   -> Color(0xFF5B9A77)
                            TaskStatus.Failed -> MaterialTheme.colorScheme.primary
                            else              -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(detail, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            if (active) {
                Spacer(Modifier.height(10.dp))
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
private fun FilterSheet(
    active: String,
    counts: Map<String, Int>,
    shown: List<DemoTask>,
    onSelect: (String) -> Unit,
    onSelectTask: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "进行中" to TcpgytIcons.Download,
        "已完成" to TcpgytIcons.Check,
        "下载失败" to TcpgytIcons.Info,
        "已取消" to TcpgytIcons.Power
    )
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("筛选任务", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text("按当前原型任务状态查看", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            options.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (name, icon) ->
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
                                ) { Ic(icon, tint = MaterialTheme.colorScheme.primary, size = 16.dp) }
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

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("当前结果", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$active · ${shown.size} 项", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            if (shown.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("没有匹配的任务", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                shown.forEach { task ->
                    Surface(
                        onClick = { onSelectTask(task.id) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(statusLabel(task.status), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    onFileOps: (() -> Unit)? = null,
    onCopyTitle: (() -> Unit)? = null,
    onAction: (String) -> Unit
) {
    val active = task.status == TaskStatus.Downloading || task.status == TaskStatus.Paused
    val ext = when { "MP3" in task.format -> "mp3"; "M4A" in task.format || "音频" in task.format -> "m4a"; "WebM" in task.format -> "webm"; else -> "mp4" }
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
                    Text(statusLabel(task.status), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            Text("文件", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onCopyTitle?.invoke() }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("复制标题", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(task.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)

                    val fileRows = buildList {
                        add("格式" to task.format)
                        add("模拟文件名" to "${task.title}.$ext")
                        add("保存位置" to "Download / TCPGYT")
                        if (task.status == TaskStatus.Done && task.completedAt.isNotEmpty()) {
                            add("完成时间" to task.completedAt)
                        }
                    }
                    fileRows.forEachIndexed { i, (label, value) ->
                        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        if (i < fileRows.size - 1) HorizontalDivider(color = Color.White.copy(alpha = 0.6f), thickness = 0.5.dp)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text("来源", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(0.dp)
            ) {
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

            if (task.status == TaskStatus.Done && onFileOps != null) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onFileOps,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(18.dp)
                ) { Text("文件操作", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormatSheet(onCreate: (String) -> Unit, onDismiss: () -> Unit) {
    var kind by remember { mutableStateOf(MediaKind.Video) }
    var showAll by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("推荐 · 1080P · MP4") }

    val simpleVideo = listOf("推荐 · 1080P · MP4" to "约 612 MB", "高清 · 最高质量" to "大小待确认", "省空间 · 480P · MP4" to "约 186 MB")
    val simpleAudio = listOf("原始音频 · M4A" to "约 18 MB", "MP3 · 高音质" to "转换后保存", "M4A · AAC" to "约 12 MB")
    val allVideo = listOf("2160P · WebM" to "需要本地合并", "1440P · MP4" to "大小待确认", "1080P · MP4" to "需要本地合并", "720P · MP4" to "约 348 MB")
    val allAudio = listOf("Opus · 原始音频" to "约 16 MB", "M4A · AAC" to "约 18 MB", "MP3 · 320 kbps" to "转换后保存")

    val options = when {
        kind == MediaKind.Video && !showAll -> simpleVideo
        kind == MediaKind.Video && showAll  -> allVideo
        kind == MediaKind.Audio && !showAll -> simpleAudio
        else                                -> allAudio
    }
    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text("城市夜行", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("12:48", style = MaterialTheme.typography.labelSmall, color = muted, fontWeight = FontWeight.SemiBold)
                }
                TextButton(onClick = onDismiss) { Text("关闭") }
            }
            Spacer(Modifier.height(16.dp))

            // 视频 / 音频 分段控件
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(MediaKind.Video to "视频", MediaKind.Audio to "音频").forEach { (k, label) ->
                    val on = kind == k
                    Surface(
                        onClick = { kind = k; showAll = false; selected = if (k == MediaKind.Video) "推荐 · 1080P · MP4" else "原始音频 · M4A" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (on) Color.White else Color.Transparent
                    ) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = if (on) primary else muted)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (showAll) "全部可用格式" else "为你推荐", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                TextButton(onClick = { showAll = !showAll }) {
                    Text(if (showAll) "返回推荐" else "更多格式", color = primary, fontWeight = FontWeight.Black)
                }
            }

            Spacer(Modifier.height(8.dp))
            options.forEachIndexed { index, (format, size) ->
                val on = selected == format
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { selected = format },
                    colors = CardDefaults.cardColors(containerColor = if (on) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.8f)),
                    border = if (on) androidx.compose.foundation.BorderStroke(1.5.dp, primary) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECE7EB)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(20.dp).clip(RoundedCornerShape(50))
                                .background(if (on) primary else Color.Transparent)
                                .then(if (on) Modifier else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            if (on) Ic(TcpgytIcons.Check, tint = Color.White, size = 12.dp)
                            else Box(Modifier.size(20.dp).clip(RoundedCornerShape(50)).background(Color(0xFFD9D5DA).copy(alpha = 0.6f)))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(format, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                if (!showAll && index == 0) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text("推荐", style = MaterialTheme.typography.labelSmall, color = primary, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Text(size, style = MaterialTheme.typography.labelSmall, color = muted, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Button(
                onClick = { onCreate(selected) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(20.dp)
            ) { Text("创建下载", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black) }

            Spacer(Modifier.height(10.dp))
            Text(
                "格式与任务均为本地原型演示，不会写入文件",
                style = MaterialTheme.typography.labelSmall, color = muted.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
