package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.cnc3d.ui.navigation.CncSubScreen
import com.example.cnc3d.ui.navigation.Routes
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialEmergencyButton
import com.example.cnc3d.ui.theme.LocalSnackbarHost
import com.example.cnc3d.viewmodels.CncViewModel
import kotlinx.coroutines.launch

@Composable
fun CncRootScreen(
    navController: NavHostController,
    viewModel: CncViewModel,
    initialSubScreen: CncSubScreen = CncSubScreen.RUN
) {
    var activeSubScreen by remember { mutableStateOf(initialSubScreen) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val status by viewModel.status.collectAsState()
    
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

    CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IndustrialColors.Background)
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
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 40.dp,
                            bottomEnd = 40.dp
                        )
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
