package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.ui.theme.LocalAppTheme
import com.tcpg007014.tcpgyt.ui.theme.themePrimaryMuted
import com.tcpg007014.tcpgyt.ui.theme.themeSectionLabel

/**
 * 画布统一分段控件 —— 对照 React `bg-[var(--tcp-primary-muted)]` 底槽 +
 * 选中项白底 + 主色文字 + 轻阴影，且所有 tab 文本 font-black。
 * 用于文件筛选、下载偏好·默认类型等全部 segmented control，避免各处配色漂移。
 *
 * 每个 tab 用 Box 横纵居中；并关闭 includeFontPadding（Compose 默认会在字形上方
 * 补一段字体内边距，导致文字偏上、白色药丸显厚，不如画布协调），配合
 * LineHeightStyle 上下居中裁剪行高，使文字在药丸内真正纵向居中。
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
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        ),
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else themeSectionLabel(theme)
                    )
                }
            }
        }
    }
}
