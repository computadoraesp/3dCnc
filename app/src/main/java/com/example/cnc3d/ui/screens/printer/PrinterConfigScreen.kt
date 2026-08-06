package com.example.cnc3d.ui.screens.printer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.theme.IndustrialButton
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialPanel
import com.example.cnc3d.viewmodels.PrinterViewModel

@Composable
fun PrinterConfigScreen(viewModel: PrinterViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Machine Limits") {
            ConfigItem("Max Velocity", "300 mm/s")
            ConfigItem("Max Acceleration", "3000 mm/s²")
            ConfigItem("Minimum Z-Stop", "0.000 mm")
        }

        IndustrialPanel(title = "Heater Presets") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton("PLA", onClick = { 
                    viewModel.setTargetTemp("extruder", 200f)
                    viewModel.setTargetTemp("heater_bed", 60f)
                }, modifier = Modifier.weight(1f))
                IndustrialButton("PETG", onClick = { 
                    viewModel.setTargetTemp("extruder", 240f)
                    viewModel.setTargetTemp("heater_bed", 80f)
                }, modifier = Modifier.weight(1f))
                IndustrialButton("ABS", onClick = { 
                    viewModel.setTargetTemp("extruder", 255f)
                    viewModel.setTargetTemp("heater_bed", 100f)
                }, modifier = Modifier.weight(1f))
            }
        }

        IndustrialPanel(title = "System Tuning") {
            IndustrialButton("PID Tune Hotend", onClick = { viewModel.sendMdi("PID_CALIBRATE HEATER=extruder TARGET=200") }, modifier = Modifier.fillMaxWidth())
            IndustrialButton("PID Tune Bed", onClick = { viewModel.sendMdi("PID_CALIBRATE HEATER=heater_bed TARGET=60") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        }

        IndustrialButton(
            text = "Save & Restart Firmware",
            onClick = { viewModel.sendMdi("SAVE_CONFIG") },
            modifier = Modifier.fillMaxWidth(),
            containerColor = IndustrialColors.Emergency
        )
    }
}

@Composable
private fun ConfigItem(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = IndustrialColors.TextSecondary)
        Text(value, color = IndustrialColors.Accent, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}
