package com.example.cnc3d.ui.mesh

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.BedMeshCorrectionViewModel

@Composable
fun BedMeshCorrectionScreen(vm: BedMeshCorrectionViewModel) {
    val corrected by vm.corrected.collectAsState()
    BedMeshCorrectionContent(corrected = corrected)
}

@Composable
fun BedMeshCorrectionContent(corrected: List<String>) {
    Column(Modifier.padding(16.dp)) {

        Text("Bed Mesh Correction", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        corrected.forEach { line ->
            Text(line)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BedMeshCorrectionPreview() {
    _3dCncTheme {
        BedMeshCorrectionContent(
            corrected = listOf("G1 X10 Y10 Z0.15", "G1 X20 Y10 Z0.12", "G1 X20 Y20 Z0.18")
        )
    }
}
