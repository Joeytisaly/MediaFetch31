package com.tcpg007014.tcpgyt.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.theme.AppTheme

@Composable fun MoreScreen(padding:PaddingValues,current:AppTheme,onTheme:(AppTheme)->Unit){ var dialog by remember{mutableStateOf(false)}; Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)){ Text("更多",style=MaterialTheme.typography.headlineMedium); Text("所有设置均为本地演示入口",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(20.dp)); Section("下载",listOf("下载偏好" to "尚未接入下载引擎","保存位置" to "尚未申请文件权限")); Section("隐私",listOf("Cookie 管理" to "未启用","本地数据" to "没有创建数据库")); Text("应用",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary,modifier=Modifier.padding(top=10.dp,bottom=6.dp)); Setting("外观",current.label,{dialog=true}); Setting("关于与支持","TCPGYT · 本地优先",{}) }; if(dialog) ThemeDialog(current,{onTheme(it);dialog=false},{dialog=false}) }
@Composable private fun Section(title:String,items:List<Pair<String,String>>){ Text(title,style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary,modifier=Modifier.padding(top=8.dp,bottom=6.dp)); items.forEach{Setting(it.first,it.second,{})} }
@Composable private fun Setting(title:String,detail:String,click:()->Unit){ ElevatedCard(Modifier.fillMaxWidth().padding(bottom=9.dp).clickable{click()}){ Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){ Column{Text(title,style=MaterialTheme.typography.titleMedium);Text(detail,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)};Text("›",style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.primary) } } }
@Composable private fun ThemeDialog(current:AppTheme,pick:(AppTheme)->Unit,dismiss:()->Unit){ AlertDialog(onDismissRequest=dismiss,title={Text("选择外观")},text={Column{AppTheme.entries.forEach{t->Row(Modifier.fillMaxWidth().clickable{pick(t)}.padding(vertical=12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(t.label);if(t==current)Text("已启用",color=MaterialTheme.colorScheme.primary)}}}},confirmButton={TextButton(onClick=dismiss){Text("关闭")}}) }
