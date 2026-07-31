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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcpg007014.tcpgyt.ui.theme.LocalAppTheme
import com.tcpg007014.tcpgyt.ui.theme.themePrimaryMuted
import com.tcpg007014.tcpgyt.ui.theme.themeSectionLabel

/**
 * 画布统一分段控件 —— 对照 React `bg-[var(--tcp-primary-muted)]` 底槽 +
 * 选中项白底 + 主色文字 + 轻阴影，且所有 tab 文本 font-black。
 *
 * ❗ 画布里不同位置的 segmented 尺寸本就不同，不能一刀切：
 *   - 文件筛选(App.tsx line172)：`py-2` + `text-[12px]` → fontSize 12sp / lineHeight 18sp / 纵向 8dp
 *   - 下载偏好·默认类型(line173)：`py-2.5` + `text-sm` → fontSize 14sp / lineHeight 20sp / 纵向 10dp
 * 故将字号/行高/内边距参数化（默认=文件筛选规格），调用方按画布传入对应值。
 * 行高按 web 继承的 1.5 还原，使选中白矩形高度贴合画布；关闭 includeFontPadding +
 * Box/TextAlign 居中，保证文字横纵向都居中不偏上。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TcpgytSegmented(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    lineHeight: TextUnit = 18.sp,
    verticalPadding: Dp = 8.dp
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
                        .padding(vertical = verticalPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
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
