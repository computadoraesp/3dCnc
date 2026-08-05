package com.example.cnc3d.ui.screens.timelapse

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
import com.example.cnc3d.viewmodels.TimelapseViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TimelapseScreen(
    navController: NavHostController,
    viewModel: TimelapseViewModel = viewModel()
) {
    val frameCount by viewModel.frameCount.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        TimelapseHeader(navController, if (isRecording) "Recording..." else "Camera Ready")

        TimelapseControls(viewModel)

        TimelapseStatus(frameCount, isRecording)
    }
}

@Composable
private fun TimelapseHeader(navController: NavHostController, status: String) {

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
                text = "Timelapse Control",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = status,
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
private fun TimelapseControls(viewModel: TimelapseViewModel) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        ControlButton("Start Timelapse", Color(0xFF4CAF50)) { viewModel.start("http://machine.local:8080/snapshot") }
        ControlButton("Stop Timelapse", Color(0xFFE53935)) { viewModel.stop() }
    }
}

@Composable
private fun RowScope.ControlButton(
    label: String, 
    color: Color,
    onClick: () -> Unit = {}
) {

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

    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(148.dp),
        color = animatedColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = color
            )
        }
    }
}

@Composable
private fun TimelapseStatus(frameCount: Int, isRecording: Boolean) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        StatusMetric("Frames Captured", frameCount.toString())
        StatusMetric("Interval", "5s")
        StatusMetric("Status", if (isRecording) "ACTIVE" else "IDLE")
        StatusMetric("Output", "/timelapse/session_live.mp4")
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

