package com.tcpg007014.tcpgyt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

enum class AppTheme(val label: String) { Blush("淡粉"), Blue("淡蓝"), Mint("薄荷"), Lavender("熏衣草"), Night("深色") }

// top/bot = base linear gradient; s1/s2 = radial color-cloud fills.
// s1/s2 mirror the React canvas --tcp-canvas radial stops (17%/25% and 68%/68%).
// Keep them LIGHT (not more saturated than the base) so the upper-left corner reads
// as soft as the prototype and never appears darker than the canvas.
private data class GradColors(val top: Color, val bot: Color, val s1: Color, val s2: Color)

private fun gradColors(theme: AppTheme) = when (theme) {
    AppTheme.Blush    -> GradColors(Color(0xFFF5BBC6), Color(0xFFFFF9F8), Color(0xFFF3B8C4), Color(0xFFF4BBC6))
    AppTheme.Blue     -> GradColors(Color(0xFFB6D8EE), Color(0xFFF5FAFF), Color(0xFFB9D9EE), Color(0xFFC4E3F4))
    AppTheme.Mint     -> GradColors(Color(0xFFB9E2D1), Color(0xFFF4FCF8), Color(0xFFBFE5D5), Color(0xFFCFEEE0))
    AppTheme.Lavender -> GradColors(Color(0xFFD5C4EC), Color(0xFFFAF8FF), Color(0xFFD8C8EE), Color(0xFFE4D9F5))
    AppTheme.Night    -> GradColors(Color(0xFF102A42), Color(0xFF081827), Color(0xFF1B4261), Color(0xFF102E49))
}

/**
 * Multi-layer radial + linear gradient matching the React prototype canvas.
 * Layers: base vertical gradient, upper-left color cloud, lower-right color cloud,
 * upper-right white glow — mirrors CSS radial-gradient stack at 17%/25%, 68%/68%, 80%/28%.
 * Cloud colors (s1/s2) are the same light hues as the canvas so no corner reads darker.
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

// ── 画布辅助色映射（严格对照 src/index.css 每主题 token）──────
// 画布区分三档极浅底色：wash（中）> pale ≈ soft（都很浅），并有专用的
// section-label 分区标题灰。M3 colorScheme 无对应槽位，这里按主题集中提供，
// 供详情页 / 筛选页复用，避免散落硬编码。

/** = --tcp-primary-wash（中等浅色，如图标圆底选中态、按钮底）。 */
fun themePrimaryWash(theme: AppTheme): Color = when (theme) {
    AppTheme.Blush    -> Color(0xFFFFD4DF)
    AppTheme.Blue     -> Color(0xFFCFE9F9)
    AppTheme.Mint     -> Color(0xFFCBEEE1)
    AppTheme.Lavender -> Color(0xFFE2D7F5)
    AppTheme.Night    -> Color(0xFF1F4B6E)
}

/** = --tcp-primary-soft（极浅底，如未选中图标圆底、关闭按钮底）。 */
fun themePrimarySoft(theme: AppTheme): Color = when (theme) {
    AppTheme.Blush    -> Color(0xFFFFF0F4)
    AppTheme.Blue     -> Color(0xFFEDF8FF)
    AppTheme.Mint     -> Color(0xFFEDFAF4)
    AppTheme.Lavender -> Color(0xFFF5F0FF)
    AppTheme.Night    -> Color(0xFF173A58)
}

/** = --tcp-primary-pale（极浅底，如选中卡片底、结果行底）。 */
fun themePrimaryPale(theme: AppTheme): Color = when (theme) {
    AppTheme.Blush    -> Color(0xFFFFF3F6)
    AppTheme.Blue     -> Color(0xFFF2FAFF)
    AppTheme.Mint     -> Color(0xFFF2FBF7)
    AppTheme.Lavender -> Color(0xFFFAF7FF)
    AppTheme.Night    -> Color(0xFF1A405E)
}

/** = --tcp-section-label（分区标题专用灰）。 */
fun themeSectionLabel(theme: AppTheme): Color = when (theme) {
    AppTheme.Blush    -> Color(0xFFA18D94)
    AppTheme.Blue     -> Color(0xFF6D91AA)
    AppTheme.Mint     -> Color(0xFF6D9C8D)
    AppTheme.Lavender -> Color(0xFF9282B2)
    AppTheme.Night    -> Color(0xFF89A9C0)
}

/** = --tcp-primary-muted（分段控件底槽的中性淡色）。 */
fun themePrimaryMuted(theme: AppTheme): Color = when (theme) {
    AppTheme.Blush    -> Color(0xFFF5EDF0)
    AppTheme.Blue     -> Color(0xFFEDF4F8)
    AppTheme.Mint     -> Color(0xFFEDF6F1)
    AppTheme.Lavender -> Color(0xFFF2EEF8)
    AppTheme.Night    -> Color(0xFF1C3C58)
}

/** = --tcp-ink（toast 药丸底色）：淡色系 4 套深墨，深色套翻浅。 */
fun themeInk(theme: AppTheme): Color = when (theme) {
    AppTheme.Night -> Color(0xFFEEF7FF)
    else           -> Color(0xFF151521)
}

/** toast 药丸文字色：深墨底上白字；深色套 ink 翻浅，文字翻深保证可读。 */
fun themeInkText(theme: AppTheme): Color = when (theme) {
    AppTheme.Night -> Color(0xFF0C1B2B)
    else           -> Color.White
}

/** 当前生效的主题，供无法直接拿到 AppTheme 的子 composable 读取辅助色。 */
val LocalAppTheme = staticCompositionLocalOf { AppTheme.Blush }

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
    CompositionLocalProvider(LocalAppTheme provides theme) {
        MaterialTheme(colorScheme = scheme(theme), content = content)
    }
}
