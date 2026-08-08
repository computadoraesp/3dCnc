package com.example.cnc3d.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cnc3d.datastore.SettingsModel
import com.example.cnc3d.ui.theme._3dCncTheme

@Composable
fun SettingsScreen(
    navController: NavHostController? = null,
    settings: SettingsModel = SettingsModel(),
    onAutoConnectChange: (Boolean) -> Unit = {}
) {
    SettingsContent(
        settings = settings,
        onAutoConnectChange = onAutoConnectChange
    )
}

@Composable
fun SettingsContent(
    settings: SettingsModel,
    onAutoConnectChange: (Boolean) -> Unit
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

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    _3dCncTheme {
        SettingsContent(
            settings = SettingsModel(
                lastIp = "192.168.1.100",
                lastFirmware = "FLUIDNC",
                autoConnect = true
            ),
            onAutoConnectChange = {}
        )
    }
}
