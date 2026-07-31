package com.tcpg007014.tcpgyt.ui.dev

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcpg007014.tcpgyt.engine.EngineSmokeTest
import com.tcpg007014.tcpgyt.engine.YoutubeDlEngine
import kotlinx.coroutines.launch

/**
 * 引擎自检屏(开发用,最小实现)。
 *
 * 输入一个公开 URL,点「运行自检」触发 [EngineSmokeTest.run](init + probe),
 * 结果以文本呈现。用于真机验证底层 yt-dlp 是否真正跑通。
 */
@Composable
fun EngineSmokeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("https://www.youtube.com/watch?v=aqz-KE-bpKQ") }
    var running by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "对指定公开 URL 执行 init + probe,验证底层引擎能否解压并解析元信息。不会下载媒体。",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("媒体 URL") },
            singleLine = true,
            enabled = !running,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                running = true
                result = ""
                scope.launch {
                    result = EngineSmokeTest.run(YoutubeDlEngine(context.applicationContext), url.trim())
                    running = false
                }
            },
            enabled = !running && url.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (running) "运行中…" else "运行自检")
        }
        if (result.isNotEmpty()) {
            Text(result, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
