package com.example.cnc3d.ui.control

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.viewmodels.MachineControlViewModel


@Composable
fun MachineControlScreen(vm: MachineControlViewModel) {

    val status by vm.status.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("Machine Control", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = { vm.jog("X", 10f) }) { Text("X+10") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.jog("X", -10f) }) { Text("X-10") }
        }

        Spacer(Modifier.height(8.dp))

        Row {
            Button(onClick = { vm.jog("Y", 10f) }) { Text("Y+10") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.jog("Y", -10f) }) { Text("Y-10") }
        }

        Spacer(Modifier.height(8.dp))

        Row {
            Button(onClick = { vm.jog("Z", 5f) }) { Text("Z+5") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.jog("Z", -5f) }) { Text("Z-5") }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.home() }) {
            Text("Home")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.spindle(true, 12000) }) {
            Text("Spindle ON")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { vm.spindle(false) }) {
            Text("Spindle OFF")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { vm.feedOverride(120) }) {
            Text("Feed +20%")
        }
    }
}
