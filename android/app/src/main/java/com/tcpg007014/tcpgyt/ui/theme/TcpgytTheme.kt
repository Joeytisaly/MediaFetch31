package com.tcpg007014.tcpgyt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

enum class AppTheme(val label: String) { Blush("淡粉"), Blue("淡蓝"), Mint("薄荷"), Lavender("熏衣草"), Night("深色") }

// top/bot = base linear gradient; s1/s2 = radial color-cloud fills
// s1 and s2 must be noticeably more saturated than top/bot so clouds are visible
private data class GradColors(val top: Color, val bot: Color, val s1: Color, val s2: Color)

private fun gradColors(theme: AppTheme) = when (theme) {
    AppTheme.Blush    -> GradColors(Color(0xFFF5BBC6), Color(0xFFFFF9F8), Color(0xFFE8768E), Color(0xFFF09AB8))
    AppTheme.Blue     -> GradColors(Color(0xFFB6D8EE), Color(0xFFF5FAFF), Color(0xFF78B8E0), Color(0xFF9ACCE8))
    AppTheme.Mint     -> GradColors(Color(0xFFB9E2D1), Color(0xFFF4FCF8), Color(0xFF68C4AA), Color(0xFF90D8C0))
    AppTheme.Lavender -> GradColors(Color(0xFFD5C4EC), Color(0xFFFAF8FF), Color(0xFFA688D4), Color(0xFFBEAEE8))
    AppTheme.Night    -> GradColors(Color(0xFF102A42), Color(0xFF081827), Color(0xFF1C3858), Color(0xFF0E2438))
}

/**
 * Multi-layer radial + linear gradient matching the React prototype canvas.
 * Layers: base vertical gradient, upper-left color cloud, lower-right color cloud,
 * upper-right white glow — mirrors CSS radial-gradient stack at 17%/25%, 68%/68%, 80%/28%.
 * Cloud colors (s1/s2) are deliberately more saturated than the base so they are visible.
 */
fun DrawScope.drawThemeBackground(theme: AppTheme) {
    val c = gradColors(theme)

    // 1. Base linear gradient
    drawRect(
        brush = Brush.verticalGradient(listOf(c.top, c.bot), startY = 0f, endY = size.height)
    )

    if (theme == AppTheme.Night) {
        // Night: single cool accent glow in upper area
        val hx = size.width * 0.75f
        val hy = size.height * 0.15f
        val hr = size.width * 0.60f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFF76C2ED).copy(alpha = 0.14f), Color.Transparent),
                Offset(hx, hy), hr
            ),
            radius = hr, center = Offset(hx, hy)
        )
        return
    }

    // 2. Large color cloud — upper-left (matches React: circle at 17% 25%)
    val s1 = Offset(size.width * 0.17f, size.height * 0.25f)
    val r1 = size.width * 0.70f
    drawCircle(
        brush = Brush.radialGradient(listOf(c.s1.copy(alpha = 0.80f), Color.Transparent), s1, r1),
        radius = r1, center = s1
    )

    // 3. Secondary cloud — lower center-right (matches React: circle at 68% 68%)
    val s2 = Offset(size.width * 0.68f, size.height * 0.68f)
    val r2 = size.width * 0.58f
    drawCircle(
        brush = Brush.radialGradient(listOf(c.s2.copy(alpha = 0.60f), Color.Transparent), s2, r2),
        radius = r2, center = s2
    )

    // 4. White glow — upper-right (matches React: circle at 80% 28%)
    val sh = Offset(size.width * 0.80f, size.height * 0.28f)
    val rh = size.width * 0.34f
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.90f), Color.Transparent), sh, rh),
        radius = rh, center = Offset(size.width * 0.80f, size.height * 0.28f)
    )
}

fun themePrimaryWash(theme: AppTheme): Color = when (theme) {
    AppTheme.Blush    -> Color(0xFFFFD4DF)
    AppTheme.Blue     -> Color(0xFFCFE9F9)
    AppTheme.Mint     -> Color(0xFFCBEEE1)
    AppTheme.Lavender -> Color(0xFFE2D7F5)
    AppTheme.Night    -> Color(0xFF1F4B6E)
}

private fun scheme(theme: AppTheme) = when (theme) {
    AppTheme.Blush -> lightColorScheme(
        primary = Color(0xFFED1D55), onPrimary = Color.White,
        secondary = Color(0xFFAFCBE7), onSecondary = Color.White,
        primaryContainer = Color(0xFFFFD4DF), onPrimaryContainer = Color(0xFF3D0012),
        background = Color(0xFFFFF9F8), surface = Color.White,
        surfaceVariant = Color(0xFFFFEAF1), onSurface = Color(0xFF281820),
        onSurfaceVariant = Color(0xFF8B6572), outline = Color(0xFFF3D6E0)
    )
    AppTheme.Blue -> lightColorScheme(
        primary = Color(0xFF287FBD), onPrimary = Color.White,
        secondary = Color(0xFF76B9E6), onSecondary = Color.White,
        primaryContainer = Color(0xFFCFE9F9), onPrimaryContainer = Color(0xFF001E30),
        background = Color(0xFFF5FAFF), surface = Color.White,
        surfaceVariant = Color(0xFFEAF2FF), onSurface = Color(0xFF18263D),
        onSurfaceVariant = Color(0xFF5C6B85), outline = Color(0xFFD1DDF2)
    )
    AppTheme.Mint -> lightColorScheme(
        primary = Color(0xFF218C72), onPrimary = Color.White,
        secondary = Color(0xFF78C8B1), onSecondary = Color.White,
        primaryContainer = Color(0xFFCBEEE1), onPrimaryContainer = Color(0xFF002018),
        background = Color(0xFFF4FCF8), surface = Color.White,
        surfaceVariant = Color(0xFFE6F6F0), onSurface = Color(0xFF18342D),
        onSurfaceVariant = Color(0xFF55736B), outline = Color(0xFFCDE6DD)
    )
    AppTheme.Lavender -> lightColorScheme(
        primary = Color(0xFF7659AD), onPrimary = Color.White,
        secondary = Color(0xFFA99AD8), onSecondary = Color.White,
        primaryContainer = Color(0xFFE2D7F5), onPrimaryContainer = Color(0xFF1D0A45),
        background = Color(0xFFFAF8FF), surface = Color.White,
        surfaceVariant = Color(0xFFF0EBFF), onSurface = Color(0xFF2B2147),
        onSurfaceVariant = Color(0xFF6C6287), outline = Color(0xFFDED5F7)
    )
    AppTheme.Night -> lightColorScheme(
        primary = Color(0xFF76C2ED), onPrimary = Color(0xFF0D2236),
        secondary = Color(0xFF70BDE8), onSecondary = Color(0xFF0D2236),
        primaryContainer = Color(0xFF1F4B6E), onPrimaryContainer = Color(0xFFCEEAFF),
        background = Color(0xFF0C1B2B), surface = Color(0xFF142D46),
        surfaceVariant = Color(0xFF1C3858), onSurface = Color(0xFFEEF7FF),
        onSurfaceVariant = Color(0xFFACC1D1), outline = Color(0xFF2D4A62)
    )
}

@Composable
fun TcpgytTheme(theme: AppTheme = AppTheme.Blush, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme(theme), content = content)
}
