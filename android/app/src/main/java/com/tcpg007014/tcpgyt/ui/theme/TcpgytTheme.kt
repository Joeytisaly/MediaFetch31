package com.tcpg007014.tcpgyt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String) { Blush("淡粉"), Blue("淡蓝"), Mint("薄荷"), Lavender("薰衣草"), Night("深色") }
private fun scheme(theme: AppTheme) = when (theme) {
    AppTheme.Blush -> lightColorScheme(primary=Color(0xFFFF3D78),onPrimary=Color.White,background=Color(0xFFFFF7FA),surface=Color(0xFFFFFFFF),surfaceVariant=Color(0xFFFFEAF1),onSurface=Color(0xFF281820),onSurfaceVariant=Color(0xFF8B6572),outline=Color(0xFFF3D6E0),secondary=Color(0xFFFFD5E3))
    AppTheme.Blue -> lightColorScheme(primary=Color(0xFF4385E8),onPrimary=Color.White,background=Color(0xFFF7FAFF),surface=Color.White,surfaceVariant=Color(0xFFEAF2FF),onSurface=Color(0xFF18263D),onSurfaceVariant=Color(0xFF5C6B85),outline=Color(0xFFD1DDF2))
    AppTheme.Mint -> lightColorScheme(primary=Color(0xFF278C76),onPrimary=Color.White,background=Color(0xFFF7FCFA),surface=Color.White,surfaceVariant=Color(0xFFE6F6F0),onSurface=Color(0xFF18342D),onSurfaceVariant=Color(0xFF55736B),outline=Color(0xFFCDE6DD))
    AppTheme.Lavender -> lightColorScheme(primary=Color(0xFF8065D9),onPrimary=Color.White,background=Color(0xFFFAF8FF),surface=Color.White,surfaceVariant=Color(0xFFF0EBFF),onSurface=Color(0xFF2B2147),onSurfaceVariant=Color(0xFF6C6287),outline=Color(0xFFDED5F7))
    AppTheme.Night -> lightColorScheme(primary=Color(0xFFB8A2FF),onPrimary=Color(0xFF20153D),background=Color(0xFF17131F),surface=Color(0xFF211B2B),surfaceVariant=Color(0xFF302840),onSurface=Color(0xFFF0EAF8),onSurfaceVariant=Color(0xFFC9BDD7),outline=Color(0xFF50465F))
}
@Composable fun TcpgytTheme(theme: AppTheme=AppTheme.Blush, content: @Composable () -> Unit) { MaterialTheme(colorScheme=scheme(theme), content=content) }
