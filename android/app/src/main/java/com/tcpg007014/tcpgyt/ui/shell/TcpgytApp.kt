package com.tcpg007014.tcpgyt.ui.shell

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tcpg007014.tcpgyt.data.AppPreferences
import com.tcpg007014.tcpgyt.ui.components.TcpgytIcons
import com.tcpg007014.tcpgyt.ui.files.FilesScreen
import com.tcpg007014.tcpgyt.ui.more.MoreScreen
import com.tcpg007014.tcpgyt.ui.tasks.TasksScreen
import com.tcpg007014.tcpgyt.ui.theme.AppTheme
import com.tcpg007014.tcpgyt.ui.theme.TcpgytTheme
import kotlinx.coroutines.launch

enum class Destination(val label: String) { Tasks("任务"), Files("文件"), More("更多") }

private fun destinationIcon(destination: Destination): ImageVector = when (destination) {
    Destination.Tasks -> TcpgytIcons.Tasks
    Destination.Files -> TcpgytIcons.Files
    Destination.More -> TcpgytIcons.More
}

@Composable
fun TcpgytApp() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(Destination.Tasks) }
    val savedTheme by AppPreferences.themeFlow(context).collectAsState(initial = AppTheme.Blush)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    TcpgytTheme(savedTheme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                    Destination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = tab == destination,
                            onClick = { tab = destination },
                            icon = { Icon(destinationIcon(destination), contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        ) { padding ->
            val onSnack: (String) -> Unit = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
            when (tab) {
                Destination.Tasks -> TasksScreen(padding, onSnack)
                Destination.Files -> FilesScreen(padding, onSnack)
                Destination.More -> MoreScreen(padding, savedTheme) { newTheme ->
                    scope.launch { AppPreferences.saveTheme(context, newTheme) }
                }
            }
        }
    }
}
