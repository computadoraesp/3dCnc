package com.example.cnc3d.ui.screens.cloud

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
import com.example.cnc3d.viewmodels.CloudViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CloudScreen(
    navController: NavHostController,
    viewModel: CloudViewModel = viewModel()
) {
    val status by viewModel.status.collectAsState()
    val lastSync by viewModel.lastSync.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        CloudHeader(navController, status)

        CloudControls(viewModel)

        CloudStatus(lastSync, pendingCount)
    }
}

@Composable
private fun CloudHeader(navController: NavHostController, status: String) {

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
                text = "Cloud Sync",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Status: $status",
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
private fun CloudControls(viewModel: CloudViewModel) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CloudButton("Upload Files", Color(0xFF00BCD4)) { viewModel.uploadFiles() }
            CloudButton("Download Files", Color(0xFF4CAF50)) { viewModel.downloadFiles() }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CloudButton("Sync Now", Color(0xFFFFC107)) { viewModel.sync() }
            CloudButton("Clear Cache", Color(0xFFE53935)) { viewModel.clearCache() }
        }
    }
}

@Composable
private fun RowScope.CloudButton(
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
private fun CloudStatus(lastSync: String, pendingCount: Int) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        StatusMetric("Last Sync", lastSync)
        StatusMetric("Pending Uploads", pendingCount.toString())
        StatusMetric("Server", "google.drive.api")
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
