package com.example.cnc3d.ui.screens.printer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.navigation.PrinterSubScreen
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialEmergencyButton
import com.example.cnc3d.viewmodels.PrinterViewModel

@Composable
fun PrinterRootScreen(
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(IndustrialColors.Background)) {
                IndustrialEmergencyButton(
                    onClick = { viewModel.emergencyStop() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                // Linear Progress for Print Job
                val status by viewModel.status.collectAsState()
                if (status.progress > 0) {
                    LinearProgressIndicator(
                        progress = { status.progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
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
                        onClick = { activeSubScreen = tab.subScreen },
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
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

enum class PrinterTab(val label: String, val icon: ImageVector, val subScreen: PrinterSubScreen) {
    RUN("Run", Icons.Default.PlayArrow, PrinterSubScreen.RUN),
    MDI("MDI", Icons.Default.Terminal, PrinterSubScreen.MDI),
    Z_OFFSET("Z-Off", Icons.Default.VerticalAlignBottom, PrinterSubScreen.Z_OFFSET),
    MESH("Mesh", Icons.Default.GridOn, PrinterSubScreen.MESH),
    FILAMENT("Filam", Icons.Default.Hardware, PrinterSubScreen.FILAMENT),
    CONFIG("Config", Icons.Default.Settings, PrinterSubScreen.CONFIG),
    RENDER("Render", Icons.Default.ViewInAr, PrinterSubScreen.RENDERER)
}
