package com.example.cnc3d.ui.safety

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.viewmodels.SafetyViewModel


@Composable
fun SafetyScreen(vm: SafetyViewModel) {

    val status by vm.status.collectAsState()

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

        Button(onClick = { vm.resetAlarm() }) {
            Text("Reset Alarm")
        }
    }
}

