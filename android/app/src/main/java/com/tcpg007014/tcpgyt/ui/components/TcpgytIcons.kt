package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object TcpgytIcons {

    // ── 底部导航图标（保持不变）─────────────────────────────

    // ⏻ Power symbol: filled stem + arc ring with 60° gap at top
    val Tasks get() = icon("Tasks") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(11f, 3.5f); lineTo(13f, 3.5f); lineTo(13f, 12.5f); lineTo(11f, 12.5f); close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(15.5f, 5.94f)
            arcTo(7f, 7f, 0f, true, true, 8.5f, 5.94f)
        }
    }

    // ∞ Infinity symbol: two mirrored bezier lobes crossing at center
    val Files get() = icon("Files") {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(12f, 12f)
            curveTo(13f, 8f, 20f, 8f, 20f, 12f)
            curveTo(20f, 16f, 13f, 16f, 12f, 12f)
            curveTo(11f, 16f, 4f, 16f, 4f, 12f)
            curveTo(4f, 8f, 11f, 8f, 12f, 12f)
        }
    }

    val More get() = icon("More") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 10f); lineTo(8f, 10f); lineTo(8f, 13f); lineTo(5f, 13f); close()
            moveTo(10.5f, 10f); lineTo(13.5f, 10f); lineTo(13.5f, 13f); lineTo(10.5f, 13f); close()
            moveTo(16f, 10f); lineTo(19f, 10f); lineTo(19f, 13f); lineTo(16f, 13f); close()
        }
    }

    // ── 内容图标（对照画布 React `Icon` 组件的 24×24 描边 path）────

    // → 箭头
    val Arrow get() = icon("Arrow") {
        stroke { moveTo(5f, 12f); lineTo(19f, 12f) }
        stroke { moveTo(13f, 6f); lineToRelative(6f, 6f); lineToRelative(-6f, 6f) }
    }

    // ✓ 勾选
    val Check get() = icon("Check") {
        stroke { moveTo(5f, 12f); lineToRelative(4.2f, 4.2f); lineTo(19f, 6.5f) }
    }

    // › 右向 chevron
    val Chevron get() = icon("Chevron") {
        stroke { moveTo(9f, 5f); lineToRelative(7f, 7f); lineToRelative(-7f, 7f) }
    }

    // ↓ 下载
    val Download get() = icon("Download") {
        stroke { moveTo(12f, 3f); lineTo(12f, 15f) }
        stroke { moveTo(7.5f, 10.5f); lineToRelative(4.5f, 4.5f); lineToRelative(4.5f, -4.5f) }
        stroke { moveTo(5f, 20f); lineTo(19f, 20f) }
    }

    // 📁 文件夹（描边）
    val Folder get() = icon("Folder") {
        stroke {
            moveTo(3f, 7.5f)
            arcTo(2.5f, 2.5f, 0f, false, true, 5.5f, 5f)
            horizontalLineTo(10f)
            lineToRelative(2f, 2.5f)
            horizontalLineToRelative(6.5f)
            arcTo(2.5f, 2.5f, 0f, false, true, 21f, 10f)
            verticalLineToRelative(7.5f)
            arcToRelative(2.5f, 2.5f, 0f, false, true, -2.5f, 2.5f)
            horizontalLineToRelative(-13f)
            arcTo(2.5f, 2.5f, 0f, false, true, 3f, 17.5f)
            close()
        }
    }

    // 🔗 链接
    val Link get() = icon("Link") {
        stroke {
            moveTo(10.8f, 13.2f)
            arcToRelative(4f, 4f, 0f, false, false, 5.7f, 0.1f)
            lineToRelative(2.1f, -2.1f)
            arcToRelative(4f, 4f, 0f, false, false, -5.7f, -5.7f)
            lineTo(11.7f, 6.7f)
        }
        stroke {
            moveTo(13.2f, 10.8f)
            arcToRelative(4f, 4f, 0f, false, false, -5.7f, -0.1f)
            lineToRelative(-2.1f, 2.1f)
            arcToRelative(4f, 4f, 0f, false, false, 5.7f, 5.7f)
            lineToRelative(1.2f, -1.2f)
        }
    }

    // ⏸ 暂停
    val Pause get() = icon("Pause") {
        stroke { moveTo(8f, 5f); lineTo(8f, 19f) }
        stroke { moveTo(16f, 5f); lineTo(16f, 19f) }
    }

    // ▶ 播放（填充）
    val Play get() = icon("Play") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(9f, 6f); lineToRelative(9f, 6f); lineToRelative(-9f, 6f); close()
        }
    }

    // ⓘ 信息
    val Info get() = icon("Info") {
        stroke {
            moveTo(3.5f, 12f)
            arcTo(8.5f, 8.5f, 0f, true, true, 20.5f, 12f)
            arcTo(8.5f, 8.5f, 0f, true, true, 3.5f, 12f)
        }
        stroke { moveTo(12f, 11f); lineTo(12f, 16f) }
        stroke { moveTo(12f, 8f); lineTo(12.01f, 8f) }
    }

    // ⏻ 电源（内容态，与导航同形但独立命名）
    val Power get() = icon("Power") {
        stroke { moveTo(12f, 3f); lineTo(12f, 11f) }
        stroke {
            moveTo(7.05f, 5.05f)
            arcTo(8f, 8f, 0f, true, false, 16.95f, 5f)
        }
    }

    // ✦ 火花
    val Spark get() = icon("Spark") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 3f)
            lineToRelative(1.25f, 5.75f)
            lineTo(19f, 10f)
            lineToRelative(-5.75f, 1.25f)
            lineTo(12f, 17f)
            lineToRelative(-1.25f, -5.75f)
            lineTo(5f, 10f)
            lineToRelative(5.75f, -1.25f)
            close()
            moveTo(19f, 16f)
            lineToRelative(0.55f, 2.45f)
            lineTo(22f, 19f)
            lineToRelative(-2.45f, 0.55f)
            lineTo(19f, 22f)
            lineToRelative(-0.55f, -2.45f)
            lineTo(16f, 19f)
            lineToRelative(2.45f, -0.55f)
            close()
        }
    }

    private fun ImageVector.Builder.stroke(width: Float = 2f, block: PathBuilder.() -> Unit) =
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = width,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = block
        )

    private fun icon(name: String, block: ImageVector.Builder.() -> Unit) =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply(block).build()
}
