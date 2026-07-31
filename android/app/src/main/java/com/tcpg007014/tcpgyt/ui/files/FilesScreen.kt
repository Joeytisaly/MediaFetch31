package com.tcpg007014.tcpgyt.ui.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class DemoFile(val name:String,val type:String,val meta:String)
@Composable fun FilesScreen(padding: PaddingValues) {
    var query by remember { mutableStateOf("") }; var filter by remember { mutableStateOf("最近") }; var source by remember { mutableStateOf<String?>(null) }
    val files = listOf(DemoFile("城市电台片段.mp3","音频","12.4 MB · 今天"), DemoFile("旅行影像精选.mp4","视频","84.6 MB · 昨天"))
    val shown = files.filter { (filter == "最近" || it.type == filter) && it.name.contains(query, true) }
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
        Text("全部文件", style=MaterialTheme.typography.headlineMedium)
        Text("仅展示本地演示数据，不读取设备文件", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value=query,onValueChange={query=it},singleLine=true,label={Text("搜索文件")},modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { listOf("最近","音频","视频").forEach { FilterChip(selected=filter==it,onClick={filter=it},label={Text(it)}) } }
        Text("最近添加 · 演示排序",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary,modifier=Modifier.padding(vertical=12.dp))
        if (shown.isEmpty()) Empty { query=""; filter="最近" } else shown.forEach { file -> FileCard(file) { source=file.name } }
    }
    source?.let { name -> AlertDialog(onDismissRequest={source=null},title={Text("来源信息")},text={Text("$name\n这是本地演示来源信息，不会打开链接或访问网络。")},confirmButton={TextButton(onClick={source=null}){Text("知道了")}}) }
}
@Composable private fun FileCard(file:DemoFile,click:()->Unit) { ElevatedCard(Modifier.fillMaxWidth().padding(bottom=10.dp).clickable(onClick=click)) { Column(Modifier.padding(16.dp)) { Text(file.name,style=MaterialTheme.typography.titleMedium); Text("${file.type} · ${file.meta}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(8.dp)); Text("来源信息 ›",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary) } } }
@Composable private fun Empty(clear:()->Unit) { Column(Modifier.fillMaxWidth().padding(top=48.dp),horizontalAlignment=Alignment.CenterHorizontally) { Text("没有匹配的文件",style=MaterialTheme.typography.titleMedium); Text("试试更换关键词或筛选条件",style=MaterialTheme.typography.bodySmall); TextButton(onClick=clear){Text("清除筛选")} } }
