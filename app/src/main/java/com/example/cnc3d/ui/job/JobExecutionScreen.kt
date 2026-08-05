package com.example.cnc3d.ui.job

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.app.viewmodels.JobExecutionViewModel

@Composable
fun JobExecutionScreen(vm: JobExecutionViewModel) {

    val status by vm.status.collectAsState()
    val progress by vm.progress.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Job Execution", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(16.dp))

        LinearProgressIndicator(progress = progress)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.refreshProgress() }) {
            Text("Refresh Progress")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.stop() }) {
            Text("Stop Job")
        }
    }
}
