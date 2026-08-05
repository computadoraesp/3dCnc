package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.navigation.CncSubScreen
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialEmergencyButton
import com.example.cnc3d.viewmodels.CncViewModel

@Composable
fun CncRootScreen(
    viewModel: CncViewModel,
    initialSubScreen: CncSubScreen = CncSubScreen.RUN
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
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = IndustrialColors.Panel,
                contentColor = IndustrialColors.TextPrimary,
                tonalElevation = 8.dp
            ) {
                CncTab.entries.forEach { tab ->
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
                CncSubScreen.RUN -> CncRunScreen(viewModel, onShowInfo)
                CncSubScreen.MDI -> CncMdiScreen(viewModel)
                CncSubScreen.OFFSETS -> CncOffsetScreen(viewModel)
                CncSubScreen.TOOLS -> CncToolScreen(viewModel)
                CncSubScreen.DIAGNOSTICS -> CncDiagScreen(viewModel, onShowInfo)
                CncSubScreen.CONFIG -> CncConfigScreen(viewModel)
                CncSubScreen.RENDERER -> CncRendererScreen(viewModel)
            }
        }
    }
}

enum class CncTab(val label: String, val icon: ImageVector, val subScreen: CncSubScreen) {
    RUN("Run", Icons.Default.PlayArrow, CncSubScreen.RUN),
    MDI("MDI", Icons.Default.Terminal, CncSubScreen.MDI),
    OFFSETS("Offset", Icons.Default.Straighten, CncSubScreen.OFFSETS),
    TOOLS("Tools", Icons.Default.Build, CncSubScreen.TOOLS),
    DIAG("Diag", Icons.AutoMirrored.Filled.FactCheck, CncSubScreen.DIAGNOSTICS),
    CONFIG("Config", Icons.Default.Settings, CncSubScreen.CONFIG),
    RENDER("Render", Icons.Default.ViewInAr, CncSubScreen.RENDERER)
}
