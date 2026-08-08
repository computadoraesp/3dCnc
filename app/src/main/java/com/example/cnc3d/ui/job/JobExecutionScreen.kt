package com.example.cnc3d.ui.job

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.app.viewmodels.JobExecutionViewModel
import com.example.cnc3d.ui.theme._3dCncTheme

@Composable
fun JobExecutionScreen(vm: JobExecutionViewModel) {
    val status by vm.status.collectAsState()
    val progress by vm.progress.collectAsState()
    JobExecutionContent(
        status = status,
        progress = progress,
        onRefreshProgress = { vm.refreshProgress() },
        onStop = { vm.stop() }
    )
}

@Composable
fun JobExecutionContent(
    status: String,
    progress: Float,
    onRefreshProgress: () -> Unit,
    onStop: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {

        Text("Job Execution", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = onRefreshProgress) {
            Text("Refresh Progress")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onStop) {
            Text("Stop Job")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JobExecutionPreview() {
    _3dCncTheme {
        JobExecutionContent(
            status = "Printing...",
            progress = 0.45f,
            onRefreshProgress = {},
            onStop = {}
        )
    }
}
