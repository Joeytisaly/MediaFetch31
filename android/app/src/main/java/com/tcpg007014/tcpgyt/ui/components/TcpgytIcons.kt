package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object TcpgytIcons {

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
            // Arc from upper-right to upper-left (300°, clockwise), gap at top
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
            cubicTo(13f, 8f, 20f, 8f, 20f, 12f)
            cubicTo(20f, 16f, 13f, 16f, 12f, 12f)
            cubicTo(11f, 16f, 4f, 16f, 4f, 12f)
            cubicTo(4f, 8f, 11f, 8f, 12f, 12f)
        }
    }

    val More get() = icon("More") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 10f); lineTo(8f, 10f); lineTo(8f, 13f); lineTo(5f, 13f); close()
            moveTo(10.5f, 10f); lineTo(13.5f, 10f); lineTo(13.5f, 13f); lineTo(10.5f, 13f); close()
            moveTo(16f, 10f); lineTo(19f, 10f); lineTo(19f, 13f); lineTo(16f, 13f); close()
        }
    }

    private fun icon(name: String, block: ImageVector.Builder.() -> Unit) =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply(block).build()
}
