package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.CncViewModel

@Composable
fun CncDiagScreen(
    viewModel: CncViewModel,
    onShowInfo: (String) -> Unit = {}
) {
    val status by viewModel.status.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Controller Intelligence") {
            IndustrialInfoPanel(
                title = "System Stats",
                info = mapOf(
                    "Firmware" to status.firmwareVersion,
                    "Uptime" to status.uptime,
                    "CPU Load" to "12%",
                    "Heap Free" to "45KB"
                )
            )
        }

        IndustrialPanel(title = "I/O LED Matrix") {
            val inputs = listOf("X-Lim", "Y-Lim", "Z-Lim", "Probe", "E-Stop", "Door", "Hold", "Start")
            val inputLeds = inputs.map { label ->
                val key = label.lowercase().replace("-", "_")
                Triple(
                    if (status.sensors[key] == true) LedState.ACTIVE else LedState.INACTIVE,
                    label,
                    if (status.sensors[key] == true) "SIGNAL HIGH" else "SIGNAL LOW"
                )
            }
            Text("INPUTS", color = IndustrialColors.TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            IndustrialLedStrip(inputLeds, onShowInfo, Modifier.fillMaxWidth())

            Spacer(Modifier.height(12.dp))

            val outputs = listOf("Spindle", "Mist", "Flood", "Coolant", "Aux1", "Aux2")
            val outputLeds = outputs.map { label ->
                val key = label.lowercase()
                Triple(
                    if (status.sensors[key] == true) LedState.ACTIVE else LedState.INACTIVE,
                    label,
                    if (status.sensors[key] == true) "OUTPUT ON" else "OUTPUT OFF"
                )
            }
            Text("OUTPUTS", color = IndustrialColors.TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            IndustrialLedStrip(outputLeds, onShowInfo, Modifier.fillMaxWidth())
        }

        IndustrialPanel(title = "Recent Alarm Log", modifier = Modifier.height(150.dp)) {
            Text("NO ACTIVE ALARMS", color = IndustrialColors.Success, style = MaterialTheme.typography.bodySmall)
        }
    }
}
