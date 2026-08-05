package com.example.cnc3d.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.viewmodels.RealtimeDashboardViewModel

@Composable
fun RealtimeDashboardScreen(vm: RealtimeDashboardViewModel) {

    val state by vm.state.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Realtime Dashboard", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.start() }) {
            Text("Start realtime")
        }

        Spacer(Modifier.height(24.dp))

        // CNC Position
        state.cncPosition?.let {
            Text("CNC Position:")
            Text("X: ${it.x}")
            Text("Y: ${it.y}")
            Text("Z: ${it.z}")
            Text("RPM: ${it.spindleRpm}")
            Text("Feed: ${it.feedRate}")
        } ?: Text("CNC Position: waiting…")

        Spacer(Modifier.height(24.dp))

        // Printer Temps
        state.printerTemps?.let {
            Text("Printer Temps:")
            Text("Hotend: ${it.hotend}°C")
            Text("Bed: ${it.bed}°C")
        } ?: Text("Printer Temps: waiting…")

        Spacer(Modifier.height(24.dp))

        // Machine State
        Text("Machine State: ${state.machineState}")

        Spacer(Modifier.height(24.dp))

        // Job Progress
        Text("Job Progress:")
        LinearProgressIndicator(
        progress = { state.jobProgress },
        modifier = Modifier,
        color = ProgressIndicatorDefaults.linearColor,
        trackColor = ProgressIndicatorDefaults.linearTrackColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}
