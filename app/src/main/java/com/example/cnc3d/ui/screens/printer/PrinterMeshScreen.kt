package com.example.cnc3d.ui.screens.printer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.components.MeshViewer
import com.example.cnc3d.ui.theme.*
import com.example.cnc3d.viewmodels.PrinterViewModel

@Composable
fun PrinterMeshScreen(viewModel: PrinterViewModel) {
    val mesh by viewModel.mesh.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Bed Mesh Visualizer", modifier = Modifier.fillMaxWidth().weight(1f)) {
            MeshViewer(mesh, Modifier.fillMaxSize())
        }

        IndustrialPanel(title = "Mesh Statistics") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IndustrialDro("Variance", "0.142", modifier = Modifier.weight(1f))
                IndustrialDro("Min Z", "-0.082", modifier = Modifier.weight(1f))
                IndustrialDro("Max Z", "0.060", modifier = Modifier.weight(1f))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IndustrialButton("Calibrate Bed", onClick = { viewModel.sendMdi("BED_MESH_CALIBRATE") }, modifier = Modifier.weight(1f))
            IndustrialButton("Clear Mesh", onClick = { viewModel.sendMdi("BED_MESH_CLEAR") }, modifier = Modifier.weight(1f), containerColor = IndustrialColors.Emergency)
        }
    }
}
