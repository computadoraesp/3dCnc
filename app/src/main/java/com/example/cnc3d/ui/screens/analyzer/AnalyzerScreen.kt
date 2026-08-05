package com.example.cnc3d.ui.screens.analyzer

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavHostController
import com.example.cnc3d.viewmodels.DiagnosticsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cnc3d.domain.usecases.DiagnosticsData

@Composable
fun AnalyzerScreen(
    navController: NavHostController,
    viewModel: DiagnosticsViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    val isRunning by viewModel.isRecording.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        AnalyzerHeader(navController, if (isRunning) "Scanning..." else "Diagnostics Active")

        AnalyzerMetrics(data)

        AnalyzerTools(viewModel)
    }
}

@Composable
private fun AnalyzerHeader(navController: NavHostController, status: String) {

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
                text = "System Analyzer",
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
private fun AnalyzerMetrics(data: DiagnosticsData) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Metric("CPU Load", "${data.cpuLoad}%", Color(0xFF00BCD4))
        Metric("Memory Usage", "${data.ramUsage}%", Color(0xFFFFC107))
        Metric("Network Latency", "${data.networkLatency} ms", Color(0xFF4CAF50))
        Metric("Disk I/O", data.diskIO, Color(0xFF8BC34A))
        Metric("Temperature", data.temperature, Color(0xFFFF5722))
    }
}

@Composable
private fun Metric(label: String, value: String, color: Color) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = Color(0xFFB0BEC5))
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@Composable
private fun AnalyzerTools(viewModel: DiagnosticsViewModel) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AnalyzerButton("Run Diagnostics", Color(0xFF4CAF50)) { viewModel.runDiagnostics() }
            AnalyzerButton("Export Report", Color(0xFF00BCD4)) { /* Export logic */ }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AnalyzerButton("Check Sensors", Color(0xFFFFC107))
            AnalyzerButton("System Logs", Color(0xFFE53935))
        }
    }
}

@Composable
private fun RowScope.AnalyzerButton(
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
