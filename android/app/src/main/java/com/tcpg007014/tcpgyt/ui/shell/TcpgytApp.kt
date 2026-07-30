package com.tcpg007014.tcpgyt.ui.shell

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.theme.TcpgytTheme

enum class Destination(val label:String,val mark:String){ Tasks("任务","↓"), Files("文件","≋"), More("更多","•••") }
@Composable fun TcpgytApp(){ var selected by remember { mutableStateOf(Destination.Tasks) }; TcpgytTheme { Scaffold(bottomBar={ NavigationBar { Destination.entries.forEach { item -> NavigationBarItem(selected=item==selected,onClick={selected=item},icon={Text(item.mark)},label={Text(item.label)}) } } }) { padding -> Column(Modifier.fillMaxSize().padding(padding).padding(horizontal=20.dp,vertical=18.dp)) { when(selected){ Destination.Tasks->Tasks(); Destination.Files->Files(); Destination.More->More() } } } } }
@Composable private fun Tasks(){ Header("下载任务","本地演示状态 · 未连接下载引擎"); CardBlock("正在下载","晚风现场录音 · 42%","可暂停 · 仅界面原型"); CardBlock("等待中","旅行影像精选 · 已排队","不会创建真实任务"); CardBlock("最近完成","城市电台片段 · MP3","示例文件记录") }
@Composable private fun Files(){ Header("全部文件","仅展示原型数据，不读取设备文件"); CardBlock("城市电台片段.mp3","音频 · 12.4 MB · 今天","来源信息与复制功能将在后续阶段审核"); CardBlock("旅行影像精选.mp4","视频 · 84.6 MB · 昨天","本地媒体访问尚未启用") }
@Composable private fun More(){ Header("更多","所有设置均为本地演示入口"); listOf("外观" to "淡粉主题已启用","下载偏好" to "尚未接入下载引擎","保存位置" to "尚未申请文件权限","Cookie 管理" to "未启用","本地数据" to "没有创建数据库","关于与支持" to "TCPGYT · 本地优先").forEach{ CardBlock(it.first,it.second,"›") } }
@Composable private fun Header(title:String,sub:String){ Text(title,style=MaterialTheme.typography.headlineMedium); Spacer(Modifier.height(4.dp)); Text(sub,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(20.dp)) }
@Composable private fun CardBlock(title:String,detail:String,meta:String){ ElevatedCard(Modifier.fillMaxWidth().padding(bottom=12.dp)){ Column(Modifier.padding(16.dp)){ Text(title,style=MaterialTheme.typography.titleMedium); Spacer(Modifier.height(5.dp)); Text(detail,style=MaterialTheme.typography.bodyMedium); Spacer(Modifier.height(8.dp)); Text(meta,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary) } } }
