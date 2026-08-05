package com.example.cnc3d.ui.screens.mesh

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

@Composable
fun MeshScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        MeshHeader(navController)

        MeshControls()

        MeshStatus()
    }
}

@Composable
private fun MeshHeader(navController: NavHostController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp) // φ proportion
            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Bed Mesh Tools",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Klipper Mesh Active • No Errors",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB0BEC5)
            )
        }

        IconButton(onClick = { navController.navigate("settings") }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MeshControls() {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            MeshButton("Generate Mesh", Color(0xFF4CAF50))
            MeshButton("Clear Mesh", Color(0xFFE53935))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            MeshButton("Z-Offset Wizard", Color(0xFFFFC107))
            MeshButton("Probe Test", Color(0xFF00BCD4))
        }
    }
}

@Composable
private fun RowScope.MeshButton(label: String, color: Color) {

    var pulse by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        targetValue = if (pulse) color.copy(alpha = 0.20f) else color.copy(alpha = 0.12f),
        label = ""
    )

    LaunchedEffect(Unit) {
        while (true) {
            pulse = !pulse
            kotlinx.coroutines.delay(900)
        }
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .height(148.dp) // φ proportion
            .background(animatedColor, RoundedCornerShape(12.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}

@Composable
private fun MeshStatus() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        StatusMetric("Mesh Points", "49")
        StatusMetric("Max Deviation", "0.12mm")
        StatusMetric("Min Deviation", "-0.08mm")
        StatusMetric("Profile", "mesh_2026_08_02.json")
    }
}

@Composable
private fun StatusMetric(label: String, value: String) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = Color(0xFFB0BEC5))
        Text(value, style = MaterialTheme.typography.titleMedium, color = Color(0xFF00BCD4))
    }
}
