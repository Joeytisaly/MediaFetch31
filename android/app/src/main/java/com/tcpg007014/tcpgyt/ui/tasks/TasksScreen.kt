package com.tcpg007014.tcpgyt.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class ParseState { Idle, Loading, Result }
@Composable fun TasksScreen(padding: PaddingValues) {
    var link by remember { mutableStateOf("") }; var state by remember { mutableStateOf(ParseState.Idle) }; var format by remember { mutableStateOf("音频 · MP3") }; var added by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
        Text("下载任务", style=MaterialTheme.typography.headlineMedium)
        Text("粘贴你有权下载的媒体链接", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp)) {
            OutlinedTextField(value=link,onValueChange={link=it;state=ParseState.Idle},label={Text("粘贴链接")},placeholder={Text("https://...")},singleLine=true,modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp)); Button(onClick={if(link.isNotBlank()) state=ParseState.Loading},modifier=Modifier.fillMaxWidth()){Text("解析链接")}
            if(link.isBlank()) Text("请输入有效链接以查看原型解析结果",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.error,modifier=Modifier.padding(top=6.dp))
        } }
        Spacer(Modifier.height(16.dp))
        when(state){ ParseState.Loading -> { LinearProgressIndicator(Modifier.fillMaxWidth()); Text("正在生成本地演示解析结果…",modifier=Modifier.padding(top=10.dp)) }
            ParseState.Result -> Result(format,{format=it},{added=true;state=ParseState.Idle;link=""})
            else -> Unit }
        if(state==ParseState.Loading) LaunchedEffect(state){ kotlinx.coroutines.delay(700);state=ParseState.Result }
        Spacer(Modifier.height(16.dp)); Text(if(added) "任务已添加" else "最近任务",style=MaterialTheme.typography.titleMedium)
        TaskCard(if(added) "等待中" else "正在下载",if(added) "原型解析媒体" else "晚风现场录音",if(added) "$format · 演示任务" else "42% · 可暂停")
        TaskCard("已完成","城市电台片段","MP3 · 12.4 MB")
    }
}
@Composable private fun Result(format:String,pick:(String)->Unit,add:()->Unit){ ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("原型解析结果",style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary);Text("旅行影像精选",style=MaterialTheme.typography.titleLarge);Text("不会访问网络或创建真实下载",style=MaterialTheme.typography.bodySmall);Spacer(Modifier.height(10.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(selected=format.startsWith("音频"),onClick={pick("音频 · MP3")},label={Text("音频")});FilterChip(selected=format.startsWith("视频"),onClick={pick("视频 · MP4")},label={Text("视频")})};Spacer(Modifier.height(10.dp));Button(onClick=add,modifier=Modifier.fillMaxWidth()){Text("添加到任务")}}} }
@Composable private fun TaskCard(state:String,title:String,detail:String){ ElevatedCard(Modifier.fillMaxWidth().padding(top=10.dp)){Column(Modifier.padding(16.dp)){Text(state,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary);Text(title,style=MaterialTheme.typography.titleMedium);Text(detail,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}} }
