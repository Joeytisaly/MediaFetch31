package com.tcpg007014.tcpgyt.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.theme.AppTheme

@Composable fun MoreScreen(padding:PaddingValues,current:AppTheme,onTheme:(AppTheme)->Unit){ var sheet by remember{mutableStateOf(false)}; Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)){ Text("更多",style=MaterialTheme.typography.headlineMedium); Text("所有设置均为本地演示入口",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(20.dp)); Section("下载",listOf("下载偏好" to "尚未接入下载引擎","保存位置" to "尚未申请文件权限")); Section("隐私",listOf("Cookie 管理" to "未启用","本地数据" to "没有创建数据库")); Section("应用",listOf("外观" to current.label,"关于与支持" to "TCPGYT · 本地优先"),{if(it=="外观")sheet=true}) }; if(sheet) ThemeSheet(current,{onTheme(it);sheet=false},{sheet=false}) }
@Composable private fun Section(title:String,items:List<Pair<String,String>>,click:(String)->Unit={}){ Text(title,style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary,modifier=Modifier.padding(top=8.dp,bottom=6.dp)); items.forEach{Setting(it.first,it.second){click(it.first)}} }
@Composable private fun Setting(title:String,detail:String,click:()->Unit){ ElevatedCard(Modifier.fillMaxWidth().padding(bottom=9.dp).clickable(onClick=click)){ Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){ Column{Text(title,style=MaterialTheme.typography.titleMedium);Text(detail,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)};Text("›",style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.primary) } } }
@OptIn(ExperimentalMaterial3Api::class) @Composable private fun ThemeSheet(current:AppTheme,pick:(AppTheme)->Unit,dismiss:()->Unit){ ModalBottomSheet(onDismissRequest=dismiss){ Column(Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(bottom=28.dp)){ Text("选择外观",style=MaterialTheme.typography.titleLarge); Text("仅在本次运行期间生效",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(16.dp)); AppTheme.entries.forEach{theme->ListItem(headlineContent={Text(theme.label)},supportingContent={Text(if(theme==AppTheme.Blush)"默认主题" else "预览主题")},trailingContent={if(theme==current)Text("已启用",color=MaterialTheme.colorScheme.primary)},modifier=Modifier.clickable{pick(theme)})} } } }
