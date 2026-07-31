package com.tcpg007014.tcpgyt.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcpg007014.tcpgyt.data.AppPreferences
import com.tcpg007014.tcpgyt.ui.components.TcpgytIcons
import com.tcpg007014.tcpgyt.ui.files.FilesScreen
import com.tcpg007014.tcpgyt.ui.more.MoreScreen
import com.tcpg007014.tcpgyt.ui.tasks.TasksScreen
import com.tcpg007014.tcpgyt.ui.theme.AppTheme
import com.tcpg007014.tcpgyt.ui.theme.TcpgytTheme
import com.tcpg007014.tcpgyt.ui.theme.drawThemeBackground
import com.tcpg007014.tcpgyt.ui.theme.themeInk
import com.tcpg007014.tcpgyt.ui.theme.themeInkText
import com.tcpg007014.tcpgyt.ui.theme.themePrimaryWash
import kotlinx.coroutines.launch

enum class Destination(val label: String) { Tasks("任务"), Files("文件"), More("更多") }

@Composable
fun TcpgytApp() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(Destination.Tasks) }
    val savedTheme by AppPreferences.themeFlow(context).collectAsState(initial = AppTheme.Blush)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    TcpgytTheme(savedTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { drawThemeBackground(savedTheme) }
        ) {
            Scaffold(
                containerColor = Color.Transparent,
            ) { innerPadding ->
                val onSnack: (String) -> Unit = { msg ->
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                }
                val screenPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 96.dp
                )
                when (tab) {
                    Destination.Tasks -> TasksScreen(screenPadding, onSnack)
                    Destination.Files -> FilesScreen(screenPadding, onSnack)
                    Destination.More  -> MoreScreen(
                        padding = screenPadding,
                        current = savedTheme,
                        onSnack = onSnack
                    ) { newTheme ->
                        scope.launch { AppPreferences.saveTheme(context, newTheme) }
                    }
                }
            }

            FloatingNavBar(
                currentTab = tab,
                onTabChange = { tab = it },
                theme = savedTheme,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp)
            )

            // 画布 toast（App.tsx line181）：悬浮圆角药丸，--tcp-ink 底 + 白字 12sp 粗体，
            // 浮在导航栏上方、点击消失、带阴影。全局统一（三页 onSnack 都走此）。
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 104.dp)
            ) { data ->
                Surface(
                    modifier = Modifier.clickable { snackbarHostState.currentSnackbarData?.dismiss() },
                    shape = CircleShape,
                    color = themeInk(savedTheme),
                    shadowElevation = 10.dp
                ) {
                    Text(
                        text = data.visuals.message,
                        color = themeInkText(savedTheme),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavBar(
    currentTab: Destination,
    onTabChange: (Destination) -> Unit,
    theme: AppTheme,
    modifier: Modifier = Modifier
) {
    val isNight = theme == AppTheme.Night
    val pillColor = if (isNight) Color(0xFF1C3858).copy(alpha = 0.92f) else Color.White.copy(alpha = 0.72f)
    val primaryWash = themePrimaryWash(theme)
    val primaryColor = MaterialTheme.colorScheme.primary
    val mutedColor = MaterialTheme.colorScheme.onSurfaceVariant

    // 74% width + SpaceBetween mirrors the React prototype nav layout
    Surface(
        modifier = modifier.fillMaxWidth(0.74f),
        shape = CircleShape,
        color = pillColor,
        tonalElevation = 0.dp,
        shadowElevation = 14.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Destination.entries.forEach { dest ->
                val selected = currentTab == dest
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (selected) primaryWash else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { onTabChange(dest) },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = when (dest) {
                                Destination.Tasks -> TcpgytIcons.Tasks
                                Destination.Files -> TcpgytIcons.Link
                                Destination.More  -> TcpgytIcons.More
                            },
                            contentDescription = dest.label,
                            tint = if (selected) primaryColor else mutedColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
