package com.tcpg007014.tcpgyt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blush = lightColorScheme(primary=Color(0xFFFF4E86), onPrimary=Color.White, secondary=Color(0xFFFFE3EC), background=Color(0xFFFFF8FA), surface=Color.White, onSurface=Color(0xFF2A1C22), surfaceVariant=Color(0xFFFFEEF3), outline=Color(0xFFEDCCD7))
@Composable fun TcpgytTheme(content: @Composable () -> Unit) { MaterialTheme(colorScheme=Blush, content=content) }
