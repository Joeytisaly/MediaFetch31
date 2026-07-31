package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.theme.LocalAppTheme
import com.tcpg007014.tcpgyt.ui.theme.themeSectionLabel

/**
 * 全局统一的底部弹窗容器。
 *
 * 画布(网页原型)的弹窗点击即完全展开；而 Material3 的 ModalBottomSheet 默认存在
 * “半展开(partiallyExpanded)”档位，导致安卓端弹窗只弹出一半、内容被截断、显得拥挤。
 *
 * 该组件通过 [rememberModalBottomSheetState] 的 skipPartiallyExpanded = true 跳过半展开档位，
 * 一次性完全展开；并把内容包进可垂直滚动的 Column，内容较长时也不会裁切。
 * 各页面弹窗统一走此容器，保证行为与画布一致、避免逐页打补丁。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TcpgytBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            content = content
        )
    }
}

/**
 * 详情卡片的分区容器，对齐画布(src/App.tsx)的设计语言：
 * 分区标题(文件 / 来源 / 任务)作为卡片内部的一条带下边框的表头，与下方内容行
 * 共用相同的左右内边距(16dp)，因此标题与「格式 / 复制文件名 / 保存位置」等行完全左对齐。
 *
 * 旧实现把标题放在卡片外部(start=4dp)、内容行用 14dp，导致标题与内容错位。
 * 内容行请统一使用 padding(horizontal = 16.dp) 以保持左对齐。
 */
@Composable
fun SheetSection(
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White.copy(alpha = 0.7f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = themeSectionLabel(LocalAppTheme.current)
                )
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.7f), thickness = 0.5.dp)
            content()
        }
    }
}
