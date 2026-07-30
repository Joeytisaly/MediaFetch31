package com.tcpg007014.tcpgyt.ui.shell

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.tcpg007014.tcpgyt.ui.files.FilesScreen
import com.tcpg007014.tcpgyt.ui.more.MoreScreen
import com.tcpg007014.tcpgyt.ui.tasks.TasksScreen
import com.tcpg007014.tcpgyt.ui.theme.AppTheme
import com.tcpg007014.tcpgyt.ui.theme.TcpgytTheme

enum class Destination(val label:String,val mark:String){ Tasks("任务","↓"), Files("文件","≋"), More("更多","•••") }
@Composable fun TcpgytApp(){ var tab by remember { mutableStateOf(Destination.Tasks) }; var theme by remember { mutableStateOf(AppTheme.Blush) }; TcpgytTheme(theme) { Scaffold(containerColor=MaterialTheme.colorScheme.background,bottomBar={ NavigationBar(containerColor=MaterialTheme.colorScheme.surface) { Destination.entries.forEach { item -> NavigationBarItem(selected=tab==item,onClick={tab=item},icon={Text(item.mark)},label={Text(item.label)}) } } }) { p -> when(tab){ Destination.Tasks->TasksScreen(p); Destination.Files->FilesScreen(p); Destination.More->MoreScreen(p,theme,{theme=it}) } } } }
