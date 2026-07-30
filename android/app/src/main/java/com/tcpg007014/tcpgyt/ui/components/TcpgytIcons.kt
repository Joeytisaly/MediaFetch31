package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object TcpgytIcons {
    val Tasks get() = icon("Tasks") { path(fill = SolidColor(Color.Black)) { moveTo(4f,3f); lineTo(20f,3f); lineTo(20f,5f); lineTo(4f,5f); close(); moveTo(4f,9f); lineTo(16f,9f); lineTo(16f,11f); lineTo(4f,11f); close(); moveTo(4f,15f); lineTo(20f,15f); lineTo(20f,17f); lineTo(4f,17f); close() } }
    val Files get() = icon("Files") { path(fill = SolidColor(Color.Black)) { moveTo(3f,6f); lineTo(10f,6f); lineTo(12f,8f); lineTo(21f,8f); lineTo(21f,19f); lineTo(3f,19f); close() } }
    val More get() = icon("More") { path(fill = SolidColor(Color.Black)) { moveTo(5f,10f); lineTo(8f,10f); lineTo(8f,13f); lineTo(5f,13f); close(); moveTo(10.5f,10f); lineTo(13.5f,10f); lineTo(13.5f,13f); lineTo(10.5f,13f); close(); moveTo(16f,10f); lineTo(19f,10f); lineTo(19f,13f); lineTo(16f,13f); close() } }
    private fun icon(name:String, block: ImageVector.Builder.() -> Unit) = ImageVector.Builder(name,24.dp,24.dp,24f,24f).apply(block).build()
}
