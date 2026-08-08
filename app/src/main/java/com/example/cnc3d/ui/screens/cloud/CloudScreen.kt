package com.example.cnc3d.ui.screens.cloud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cnc3d.ui.theme.IndustrialButton
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialInfoPanel
import com.example.cnc3d.ui.theme.IndustrialPanel
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.CloudViewModel

@Composable
fun CloudScreen(
    navController: NavHostController,
    viewModel: CloudViewModel = viewModel()
) {
    val status by viewModel.status.collectAsState()
    val lastSync by viewModel.lastSync.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()

    CloudContent(
        status = status,
        lastSync = lastSync,
        pendingCount = pendingCount,
        onUploadAll = { viewModel.uploadFiles() },
        onSyncRemote = { viewModel.sync() },
        onDownload = { viewModel.downloadFiles() },
        onWipeCache = { viewModel.clearCache() }
    )
}

@Composable
fun CloudContent(
    status: String,
    lastSync: String,
    pendingCount: Int,
    onUploadAll: () -> Unit,
    onSyncRemote: () -> Unit,
    onDownload: () -> Unit,
    onWipeCache: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Cloud Synchronization") {
            IndustrialInfoPanel(
                title = "Service Metrics",
                info = mapOf(
                    "Status" to status,
                    "Last Sync" to lastSync,
                    "Pending" to "$pendingCount files",
                    "Target" to "Google Drive"
                )
            )
        }

        IndustrialPanel(title = "Data Management") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton(
                    text = "Upload All",
                    onClick = onUploadAll,
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Success
                )
                IndustrialButton(
                    text = "Sync Remote",
                    onClick = onSyncRemote,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialButton(
                    text = "Download",
                    onClick = onDownload,
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Border
                )
                IndustrialButton(
                    text = "Wipe Cache",
                    onClick = onWipeCache,
                    modifier = Modifier.weight(1f),
                    containerColor = IndustrialColors.Emergency
                )
            }
        }

        IndustrialPanel(title = "Security & Logs") {
            Text(
                "SESSION ACTIVE",
                color = IndustrialColors.Accent,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                "CONNECTED AS: workshop-operator",
                color = IndustrialColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CloudPreview() {
    _3dCncTheme {
        CloudContent(
            status = "Online",
            lastSync = "10 minutes ago",
            pendingCount = 3,
            onUploadAll = {},
            onSyncRemote = {},
            onDownload = {},
            onWipeCache = {}
        )
    }
}
