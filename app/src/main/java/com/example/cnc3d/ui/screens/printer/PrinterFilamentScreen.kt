package com.example.cnc3d.ui.screens.printer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.PrinterViewModel

@Composable
fun PrinterFilamentScreen(viewModel: PrinterViewModel) {
    var amount by remember { mutableStateOf(10f) }
    var speed by remember { mutableStateOf(5f) }
    val status by viewModel.status.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Temperature Gauge Panel
        IndustrialPanel(title = "Hotend Thermal Status") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Column {
                    Text("ACTUAL TEMP", color = IndustrialColors.TextSecondary, fontSize = 10.sp)
                    Text("${status.temperatureHotend.toInt()}°C", color = if (status.temperatureHotend > 170) IndustrialColors.Success else IndustrialColors.Warning, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text("TARGET", color = IndustrialColors.TextSecondary, fontSize = 10.sp)
                    Text("${status.targetHotend.toInt()}°C", color = IndustrialColors.Accent, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            LinearProgressIndicator(
                progress = { (status.temperatureHotend / 300f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = IndustrialColors.Accent,
                trackColor = IndustrialColors.Border
            )
            if (status.temperatureHotend < 170) {
                Text("COLD EXTRUSION PREVENTED", color = IndustrialColors.Emergency, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        IndustrialPanel(title = "Extruder Control") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IndustrialButton("Extrude", onClick = { viewModel.extrude(amount, speed) }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Success)
                IndustrialButton("Retract", onClick = { viewModel.retract(amount, speed) }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Warning)
            }
        }

        IndustrialPanel(title = "Extrusion Parameters") {
            ParameterSlider("Amount", amount, 1f..100f, "mm") { amount = it }
            ParameterSlider("Speed", speed, 1f..50f, "mm/s") { speed = it }
        }

        IndustrialPanel(title = "Quick Actions") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton("Load", onClick = { viewModel.sendMdi("LOAD_FILAMENT") }, modifier = Modifier.weight(1f))
                IndustrialButton("Unload", onClick = { viewModel.sendMdi("UNLOAD_FILAMENT") }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ParameterSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, unit: String, onValueChange: (Float) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = IndustrialColors.TextSecondary, fontSize = 12.sp)
            Text("${value.toInt()} $unit", color = IndustrialColors.Accent, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = IndustrialColors.Accent,
                activeTrackColor = IndustrialColors.Accent,
                inactiveTrackColor = IndustrialColors.Border
            )
        )
    }
}
