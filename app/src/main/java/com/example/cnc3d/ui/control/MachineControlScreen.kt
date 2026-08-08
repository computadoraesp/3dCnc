package com.example.cnc3d.ui.control

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.MachineControlViewModel

@Composable
fun MachineControlScreen(vm: MachineControlViewModel) {
    val status by vm.status.collectAsState()
    MachineControlContent(
        status = status,
        onJog = { axis, dist -> vm.jog(axis, dist) },
        onHome = { vm.home() },
        onSpindle = { on, rpm -> vm.spindle(on, rpm) },
        onFeedOverride = { vm.feedOverride(it) }
    )
}

@Composable
fun MachineControlContent(
    status: String,
    onJog: (String, Float) -> Unit,
    onHome: () -> Unit,
    onSpindle: (Boolean, Int) -> Unit,
    onFeedOverride: (Int) -> Unit
) {
    Column(Modifier.padding(16.dp)) {

        Text("Machine Control", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = { onJog("X", 10f) }) { Text("X+10") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onJog("X", -10f) }) { Text("X-10") }
        }

        Spacer(Modifier.height(8.dp))

        Row {
            Button(onClick = { onJog("Y", 10f) }) { Text("Y+10") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onJog("Y", -10f) }) { Text("Y-10") }
        }

        Spacer(Modifier.height(8.dp))

        Row {
            Button(onClick = { onJog("Z", 5f) }) { Text("Z+5") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onJog("Z", -5f) }) { Text("Z-5") }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onHome) {
            Text("Home")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { onSpindle(true, 12000) }) {
            Text("Spindle ON")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { onSpindle(false, 0) }) {
            Text("Spindle OFF")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { onFeedOverride(120) }) {
            Text("Feed +20%")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MachineControlPreview() {
    _3dCncTheme {
        MachineControlContent(
            status = "Idle",
            onJog = { _, _ -> },
            onHome = {},
            onSpindle = { _, _ -> },
            onFeedOverride = {}
        )
    }
}
