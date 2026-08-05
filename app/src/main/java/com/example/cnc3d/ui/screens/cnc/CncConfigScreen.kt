package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.CncViewModel

@Composable
fun CncConfigScreen(viewModel: CncViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Machine Parameters") {
            ConfigRow("Units", "Metric (mm)")
            ConfigRow("Default Jog", "3000 mm/min")
            ConfigRow("Rapid Rate", "5000 mm/min")
        }

        IndustrialPanel(title = "Hardware Connectivity") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Connection", color = IndustrialColors.TextSecondary)
                Text("NETWORK (WiFi)", color = IndustrialColors.Accent, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            IndustrialButton("Manage Transport", onClick = { /* navigate to settings */ }, modifier = Modifier.fillMaxWidth())
        }

        IndustrialPanel(title = "Visual Theme") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Interface", color = IndustrialColors.TextSecondary)
                Text("SIEMENS INDUSTRIAL", color = IndustrialColors.Accent, fontWeight = FontWeight.Bold)
            }
        }

        IndustrialButton(
            text = "Save Settings",
            onClick = { /* Save logic */ },
            modifier = Modifier.fillMaxWidth(),
            containerColor = IndustrialColors.Success
        )
        
        IndustrialButton(
            text = "Hard Reset Controller",
            onClick = { viewModel.reset() },
            modifier = Modifier.fillMaxWidth(),
            containerColor = IndustrialColors.Emergency
        )
    }
}

@Composable
private fun ConfigRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = IndustrialColors.TextSecondary)
        Text(value, color = IndustrialColors.Accent, fontWeight = FontWeight.Bold)
    }
}
