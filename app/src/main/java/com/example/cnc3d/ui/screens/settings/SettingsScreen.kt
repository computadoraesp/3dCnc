package com.example.cnc3d.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.example.cnc3d.datastore.SettingsModel

@Composable
fun SettingsScreen(
    navController: NavHostController? = null,
    settings: SettingsModel = SettingsModel(),
    onAutoConnectChange: (Boolean) -> Unit = {}
) {

    Column(Modifier.padding(16.dp)) {

        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Row {
            Checkbox(
                checked = settings.autoConnect,
                onCheckedChange = onAutoConnectChange
            )
            Spacer(Modifier.width(8.dp))
            Text("Auto-connect on startup")
        }

        Spacer(Modifier.height(16.dp))

        Text("Last IP: ${settings.lastIp}")
        Text("Last Firmware: ${settings.lastFirmware}")
    }
}
