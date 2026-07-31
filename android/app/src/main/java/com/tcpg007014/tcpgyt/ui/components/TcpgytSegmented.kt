package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.theme.LocalAppTheme
import com.tcpg007014.tcpgyt.ui.theme.themePrimaryMuted
import com.tcpg007014.tcpgyt.ui.theme.themeSectionLabel

/**
 * 画布统一分段控件 —— 对照 React `bg-[var(--tcp-primary-muted)]` 底槽 +
 * 选中项白底 + 主色文字 + 轻阴影，且所有 tab 文本 font-black。
 * 用于文件筛选、下载偏好·默认类型等全部 segmented control，避免各处配色漂移。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TcpgytSegmented(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(themePrimaryMuted(theme))
            .padding(4.dp)
    ) {
        options.forEach { label ->
            val isSelected = selected == label
            Surface(
                onClick = { onSelect(label) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color.White else Color.Transparent,
                shadowElevation = if (isSelected) 1.dp else 0.dp
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else themeSectionLabel(theme),
                    modifier = Modifier
                        .padding(vertical = 9.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
