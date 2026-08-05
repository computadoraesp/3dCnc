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
import androidx.compose.ui.unit.dp
import com.example.cnc3d.viewmodels.BedMeshCorrectionViewModel

@Composable
fun BedMeshCorrectionScreen(vm: BedMeshCorrectionViewModel) {

    val corrected by vm.corrected.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Bed Mesh Correction", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        corrected.forEach { line ->
            Text(line)
        }
    }
}
