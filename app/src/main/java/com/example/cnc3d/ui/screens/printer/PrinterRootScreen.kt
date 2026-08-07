package com.example.cnc3d.ui.screens.printer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.ui.navigation.PrinterSubScreen
import com.example.cnc3d.ui.navigation.Routes
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialEmergencyButton
import com.example.cnc3d.ui.theme.LocalSnackbarHost
import com.example.cnc3d.viewmodels.PrinterViewModel
import kotlinx.coroutines.launch

@Composable
fun PrinterRootScreen(
    navController: NavHostController,
    viewModel: PrinterViewModel,
    initialSubScreen: PrinterSubScreen = PrinterSubScreen.RUN,
) {
    var activeSubScreen by remember { mutableStateOf(initialSubScreen) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onShowInfo: (String) -> Unit = { msg ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(viewModel.uiMessage) {
        viewModel.uiMessage.collect { msg ->
            onShowInfo(msg)
        }
    }

    val status by viewModel.status.collectAsState()

    CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column(
                    modifier = Modifier
                        .background(IndustrialColors.Background)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                navController.navigate(Routes.Home) {
                                    popUpTo(Routes.Home) { inclusive = true }
                                }
                            }) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Home",
                                    tint = IndustrialColors.TextPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            val isConnected = status.state != "Disconnected"
                            val connIcon = if (isConnected) {
                                when (status.connectionType) {
                                    ConnectionType.WIFI -> Icons.Default.Wifi
                                    ConnectionType.BLUETOOTH -> Icons.Default.Bluetooth
                                    ConnectionType.USB -> Icons.Default.Usb
                                    null -> Icons.Default.Wifi
                                }
                            } else {
                                Icons.Default.Warning
                            }

                            Icon(
                                connIcon,
                                contentDescription = "Connection Status",
                                tint = if (isConnected) IndustrialColors.Accent else IndustrialColors.Warning,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IndustrialEmergencyButton(
                            onClick = { viewModel.emergencyStop() },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    // Linear Progress for Print Job
                    if (status.progress > 0) {
                        LinearProgressIndicator(
                            progress = { status.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = IndustrialColors.Accent,
                            trackColor = IndustrialColors.Border
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = IndustrialColors.Panel,
                    contentColor = IndustrialColors.TextPrimary,
                    tonalElevation = 8.dp
                ) {
                    PrinterTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = activeSubScreen == tab.subScreen,
                            onClick = {
                                activeSubScreen = tab.subScreen
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Navigating to: ${tab.label.uppercase()}")
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IndustrialColors.Accent,
                                selectedTextColor = IndustrialColors.Accent,
                                indicatorColor = IndustrialColors.Border
                            )
                        )
                    }
                }
            },
            containerColor = IndustrialColors.Background
        ) { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {
                when (activeSubScreen) {
                    PrinterSubScreen.RUN -> PrinterRunScreen(viewModel, onShowInfo)
                    PrinterSubScreen.MDI -> PrinterMdiScreen(viewModel)
                    PrinterSubScreen.Z_OFFSET -> PrinterZOffsetScreen(viewModel)
                    PrinterSubScreen.MESH -> PrinterMeshScreen(viewModel)
                    PrinterSubScreen.FILAMENT -> PrinterFilamentScreen(viewModel)
                    PrinterSubScreen.CONFIG -> PrinterConfigScreen(viewModel)
                    PrinterSubScreen.RENDERER -> PrinterRendererScreen(viewModel)
                }
            }
        }
    }
}

enum class PrinterTab(val label: String, val icon: ImageVector, val subScreen: PrinterSubScreen) {
    RUN("Run", Icons.Default.PlayArrow, PrinterSubScreen.RUN),
    MDI("MDI", Icons.Default.Terminal, PrinterSubScreen.MDI),
    Z_OFFSET("Z-Off", Icons.Default.VerticalAlignBottom, PrinterSubScreen.Z_OFFSET),
    MESH("Mesh", Icons.Default.GridOn, PrinterSubScreen.MESH),
    FILAMENT("Filam", Icons.Default.Hardware, PrinterSubScreen.FILAMENT),
    CONFIG("Config", Icons.Default.Settings, PrinterSubScreen.CONFIG),
    RENDER("Render", Icons.Default.ViewInAr, PrinterSubScreen.RENDERER)
}
