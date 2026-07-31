package com.tcpg007014.tcpgyt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String) { Blush("淡粉"), Blue("淡蓝"), Mint("薄荷"), Lavender("薰衣草"), Night("深色") }

fun themeGradient(theme: AppTheme): Brush = Brush.verticalGradient(
    colors = when (theme) {
        AppTheme.Blush    -> listOf(Color(0xFFF5BBC6), Color(0xFFFAD4D7), Color(0xFFFFF9F8))
        AppTheme.Blue     -> listOf(Color(0xFFB6D8EE), Color(0xFFD9EFFA), Color(0xFFF5FAFF))
        AppTheme.Mint     -> listOf(Color(0xFFB9E2D1), Color(0xFFDCEFE5), Color(0xFFF4FCF8))
        AppTheme.Lavender -> listOf(Color(0xFFD5C4EC), Color(0xFFECE4F8), Color(0xFFFAF8FF))
        AppTheme.Night    -> listOf(Color(0xFF102A42), Color(0xFF0B2034), Color(0xFF081827))
    }
)

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
