package com.example.cnc3d.ui.safety

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.domain.models.SafetyStatus
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.SafetyViewModel

@Composable
fun SafetyScreen(vm: SafetyViewModel) {
    val status by vm.status.collectAsState()
    SafetyContent(
        status = status,
        onResetAlarm = { vm.resetAlarm() }
    )
}

@Composable
fun SafetyContent(
    status: SafetyStatus,
    onResetAlarm: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {

        Text("Safety Status", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        if (!status.isSafe) {
            Text("⚠ MACHINE NOT SAFE", color = MaterialTheme.colorScheme.error)
        } else {
            Text("✓ Machine Safe", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(16.dp))

        status.alarm?.let {
            Text("Alarm: $it", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Text("Limits:")
        status.limits.forEach { (axis, active) ->
            Text("$axis: ${if (active) "TRIGGERED" else "OK"}")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onResetAlarm) {
            Text("Reset Alarm")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SafetyPreview() {
    _3dCncTheme {
        SafetyContent(
            status = SafetyStatus(
                alarm = "Hard Limit Triggered",
                limits = mapOf("X" to true, "Y" to false, "Z" to false),
                isSafe = false
            ),
            onResetAlarm = {}
        )
    }
}

