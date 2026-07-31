package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
