package com.tcpg007014.tcpgyt.ui.shell

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.components.TcpgytIcons
import com.tcpg007014.tcpgyt.ui.files.FilesScreen
import com.tcpg007014.tcpgyt.ui.more.MoreScreen
import com.tcpg007014.tcpgyt.ui.tasks.TasksScreen
import com.tcpg007014.tcpgyt.ui.theme.AppTheme
import com.tcpg007014.tcpgyt.ui.theme.TcpgytTheme

enum class Destination(val label:String){ Tasks("任务"), Files("文件"), More("更多") }
private fun destinationIcon(d:Destination):ImageVector = when(d){Destination.Tasks->TcpgytIcons.Tasks;Destination.Files->TcpgytIcons.Files;Destination.More->TcpgytIcons.More}
@Composable fun TcpgytApp(){ var tab by remember{mutableStateOf(Destination.Tasks)};var theme by remember{mutableStateOf(AppTheme.Blush)};TcpgytTheme(theme){Scaffold(containerColor=MaterialTheme.colorScheme.background,bottomBar={NavigationBar(containerColor=MaterialTheme.colorScheme.surface,tonalElevation=2.dp){Destination.entries.forEach{d->NavigationBarItem(selected=tab==d,onClick={tab=d},icon={Icon(destinationIcon(d),null)},label={Text(d.label)})}}){p->when(tab){Destination.Tasks->TasksScreen(p);Destination.Files->FilesScreen(p);Destination.More->MoreScreen(p,theme,{theme=it})}}}}