package com.example.cnc3d.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// -----------------------------
// FAKE DATA CLASSES PARA PREVIEW
// -----------------------------
data class CncPosition(
    val x: Float,
    val y: Float,
    val z: Float,
    val spindleRpm: Int,
    val feedRate: Int
)

data class PrinterTemps(
    val hotend: Int,
    val bed: Int
)

data class DashboardState(
    val cncPosition: CncPosition? = null,
    val printerTemps: PrinterTemps? = null,
    val machineState: String = "Idle",
    val jobProgress: Float = 0f
)

// -----------------------------
// FAKE VIEWMODEL PARA PREVIEW
// -----------------------------
class FakeRealtimeDashboardViewModel {

    var state by mutableStateOf(
        DashboardState(
            cncPosition = CncPosition(
                x = 10f,
                y = 20f,
                z = 5f,
                spindleRpm = 1200,
                feedRate = 300
            ),
            printerTemps = PrinterTemps(
                hotend = 200,
                bed = 60
            ),
            machineState = "Running",
            jobProgress = 0.65f
        )
    )

    fun start() {
        // No hace nada en preview
    }
}

// -----------------------------
// TU PANTALLA REAL
// -----------------------------
@Composable
fun RealtimeDashboardScreen(vm: FakeRealtimeDashboardViewModel) {

    val state = vm.state

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
            modifier = Modifier.fillMaxWidth(),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}

// -----------------------------
// PREVIEW QUE SÍ FUNCIONA
// -----------------------------
@Preview(showBackground = true)
@Composable
fun RealtimeDashboardPreview() {
    RealtimeDashboardScreen(FakeRealtimeDashboardViewModel())
}
