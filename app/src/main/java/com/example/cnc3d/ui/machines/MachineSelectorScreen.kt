package com.example.cnc3d.app.ui.machines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.viewmodels.MachineSelectorViewModel

@Composable
fun MachineSelectorScreen(vm: MachineSelectorViewModel) {

    val machines by vm.machines.collectAsState()
    val selected by vm.selected.collectAsState()

    var name by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("Machines", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        machines.forEach { machine ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { vm.select(machine) }
                    .padding(8.dp)
            ) {
                Text(machine.name, Modifier.weight(1f))
                if (machine == selected) {
                    Text("✓")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = ip, onValueChange = { ip = it }, label = { Text("IP") })

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            vm.add(name, ip, FirmwareType.FLUIDNC) // o Moonraker según UI
        }) {
            Text("Add Machine")
        }
    }
}
