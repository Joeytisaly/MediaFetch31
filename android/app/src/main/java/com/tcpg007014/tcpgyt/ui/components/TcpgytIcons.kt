package com.tcpg007014.tcpgyt.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path

object TcpgytIcons {
    val Tasks: ImageVector get() = icon("Tasks") { path(fill = SolidColor(Color.Black)) { moveTo(4f,3f); lineTo(20f,3f); lineTo(20f,5f); lineTo(4f,5f); close(); moveTo(4f,8f); lineTo(15f,8f); lineTo(15f,10f); lineTo(4f,10f); close(); moveTo(4f,13f); lineTo(20f,13f); lineTo(20f,15f); lineTo(4f,15f); close(); moveTo(4f,18f); lineTo(15f,18f); lineTo(15f,20f); lineTo(4f,20f); close() } }
    val Files: ImageVector get() = icon("Files") { path(fill = SolidColor(Color.Black)) { moveTo(3f,5f); lineTo(10f,5f); lineTo(12f,7f); lineTo(21f,7f); lineTo(21f,19f); lineTo(3f,19f); close(); moveTo(5f,9f); lineTo(19f,9f); lineTo(19f,17f); lineTo(5f,17f); close() } }
    val More: ImageVector get() = icon("More") { path(fill = SolidColor(Color.Black)) { moveTo(5f,10f); lineTo(8f,10f); lineTo(8f,13f); lineTo(5f,13f); close(); moveTo(10.5f,10f); lineTo(13.5f,10f); lineTo(13.5f,13f); lineTo(10.5f,13f); close(); moveTo(16f,10f); lineTo(19f,10f); lineTo(19f,13f); lineTo(16f,13f); close() } }
    private fun icon(name:String, block: ImageVector.Builder.() -> Unit) = ImageVector.Builder(name,24f,24f,24f,24f).apply(block).build()
}
