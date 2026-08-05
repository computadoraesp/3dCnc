package com.example.cnc3d.ui.macros

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.viewmodels.MacroViewModel


@Composable
fun MacroScreen(vm: MacroViewModel) {

    val macros by vm.macros.collectAsState()
    val status by vm.status.collectAsState()

    var name by remember { mutableStateOf("") }
    var commands by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("Macros", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Macro name") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = commands,
            onValueChange = { commands = it },
            label = { Text("Commands (one per line)") }
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val list = commands.lines().filter { it.isNotBlank() }
            vm.addMacro(name, list)
        }) {
            Text("Add Macro")
        }

        Spacer(Modifier.height(24.dp))

        macros.forEach { macro ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(macro.name, Modifier.weight(1f))
                Button(onClick = { vm.execute(macro) }) {
                    Text("Run")
                }
            }
        }
    }
}
