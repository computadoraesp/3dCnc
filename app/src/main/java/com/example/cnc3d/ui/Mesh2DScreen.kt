package com.example.cnc3d.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cnc3d.viewmodels.MeshViewModel

@Composable
fun Mesh2DScreen(vm: MeshViewModel) {

    val mesh by vm.mesh.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Mesh 2D", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.loadMesh() }) {
            Text("Load Mesh")
        }

        Spacer(Modifier.height(16.dp))

        Canvas(Modifier.fillMaxSize()) {
            mesh?.points?.forEach { p ->
                drawCircle(
                    color = Color.Blue,
                    radius = 3f,
                    center = Offset(p.x, p.y)
                )
            }
        }
    }
}
