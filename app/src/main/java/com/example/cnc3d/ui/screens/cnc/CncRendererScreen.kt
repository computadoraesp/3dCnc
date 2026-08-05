package com.example.cnc3d.ui.screens.cnc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.components.ToolpathViewer
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.CncViewModel

@Composable
fun CncRendererScreen(viewModel: CncViewModel) {
    val gcodePath by viewModel.gcodePath.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "3D Toolpath Renderer", modifier = Modifier.weight(1f)) {
            ToolpathViewer(gcodePath, Modifier.fillMaxSize())
        }

        IndustrialPanel(title = "Execution Progress") {
            LinearProgressIndicator(
                progress = { 0.45f }, // Mock progress
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = IndustrialColors.Accent,
                trackColor = IndustrialColors.Border
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Line: 1245 / 8000", color = IndustrialColors.TextSecondary)
                Text("Time: 00:12:30", color = IndustrialColors.TextSecondary)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IndustrialButton("Zoom +", onClick = { /* Zoom logic */ }, modifier = Modifier.weight(1f))
            IndustrialButton("Zoom -", onClick = { /* Zoom logic */ }, modifier = Modifier.weight(1f))
            IndustrialButton("Reset View", onClick = { /* Reset logic */ }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Border)
        }
    }
}
