package com.example.cnc3d.ui.screens.analyzer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cnc3d.domain.usecases.DiagnosticsData
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.DiagnosticsViewModel

@Composable
fun AnalyzerScreen(
    navController: NavHostController,
    viewModel: DiagnosticsViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    val isRunning by viewModel.isRecording.collectAsState()

    AnalyzerContent(
        status = if (isRunning) "Scanning..." else "Diagnostics Active",
        data = data,
        onNavigateSettings = { navController.navigate("settings") },
        onRunDiagnostics = { viewModel.runDiagnostics() }
    )
}

@Composable
fun AnalyzerContent(
    status: String,
    data: DiagnosticsData,
    onNavigateSettings: () -> Unit,
    onRunDiagnostics: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        AnalyzerHeader(status, onNavigateSettings)

        AnalyzerMetrics(data)

        AnalyzerTools(onRunDiagnostics)
    }
}

@Composable
private fun AnalyzerHeader(status: String, onNavigateSettings: () -> Unit) {

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

        IconButton(onClick = onNavigateSettings) {
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
private fun AnalyzerTools(onRunDiagnostics: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AnalyzerButton("Run Diagnostics", Color(0xFF4CAF50)) { onRunDiagnostics() }
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

@Preview(showBackground = true)
@Composable
fun AnalyzerPreview() {
    _3dCncTheme {
        AnalyzerContent(
            status = "Diagnostics Active",
            data = DiagnosticsData(
                cpuLoad = 45,
                ramUsage = 62,
                networkLatency = 12,
                diskIO = "12 MB/s",
                temperature = "48°C"
            ),
            onNavigateSettings = {},
            onRunDiagnostics = {}
        )
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
