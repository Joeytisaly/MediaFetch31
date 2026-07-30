package com.tcpg007014.tcpgyt.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun TasksScreen(padding: PaddingValues){ Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)){ Text("下载任务",style=MaterialTheme.typography.headlineMedium); Text("本地演示状态 · 未连接下载引擎",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(20.dp)); Summary(); Spacer(Modifier.height(16.dp)); TaskCard("正在下载","晚风现场录音","42% · 可暂停",true); TaskCard("等待中","旅行影像精选","已排队 · 不会创建真实任务",false); Text("最近完成",style=MaterialTheme.typography.titleMedium,modifier=Modifier.padding(top=8.dp,bottom=8.dp)); TaskCard("已完成","城市电台片段","MP3 · 12.4 MB",false) } }
@Composable private fun Summary(){ Surface(color=MaterialTheme.colorScheme.surfaceVariant,shape=MaterialTheme.shapes.large){ Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){ Stat("3","全部");Stat("1","进行中");Stat("1","已完成") } } }
@Composable private fun Stat(n:String,l:String){ Column { Text(n,style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.primary); Text(l,style=MaterialTheme.typography.labelMedium) } }
@Composable private fun TaskCard(state:String,title:String,detail:String,progress:Boolean){ ElevatedCard(Modifier.fillMaxWidth().padding(bottom=12.dp)){ Column(Modifier.padding(16.dp)){ Text(state,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary); Spacer(Modifier.height(5.dp)); Text(title,style=MaterialTheme.typography.titleMedium); Text(detail,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); if(progress){ Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress={.42f},modifier=Modifier.fillMaxWidth()) } } } }
