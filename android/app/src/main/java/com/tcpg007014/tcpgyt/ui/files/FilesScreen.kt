package com.tcpg007014.tcpgyt.ui.files

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun FilesScreen(padding: PaddingValues){ var query by remember { mutableStateOf("") }; Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)){ Text("全部文件",style=MaterialTheme.typography.headlineMedium); Text("仅展示本地演示数据，不读取设备文件",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(16.dp)); OutlinedTextField(value=query,onValueChange={query=it},singleLine=true,label={Text("搜索文件")},modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(12.dp)); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ FilterChip(selected=true,onClick={},label={Text("最近")});FilterChip(selected=false,onClick={},label={Text("音频")});FilterChip(selected=false,onClick={},label={Text("视频")}) }; Spacer(Modifier.height(14.dp)); FileCard("城市电台片段.mp3","音频 · 12.4 MB · 今天"); FileCard("旅行影像精选.mp4","视频 · 84.6 MB · 昨天") } }
@Composable private fun FileCard(name:String,meta:String){ ElevatedCard(Modifier.fillMaxWidth().padding(bottom=10.dp)){ Column(Modifier.padding(16.dp)){ Text(name,style=MaterialTheme.typography.titleMedium); Text(meta,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(8.dp)); Text("来源信息与真实文件操作尚未启用",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary) } } }
