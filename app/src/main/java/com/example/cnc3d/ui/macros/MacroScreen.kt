package com.example.cnc3d.ui.macros

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cnc3d.domain.models.Macro
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.MacroViewModel

@Composable
fun MacroScreen(vm: MacroViewModel) {
    val macros by vm.macros.collectAsState()
    val status by vm.status.collectAsState()
    MacroContent(
        macros = macros,
        status = status,
        onAddMacro = { name, commands -> vm.addMacro(name, commands) },
        onExecute = { vm.execute(it) }
    )
}

@Composable
fun MacroContent(
    macros: List<Macro>,
    status: String,
    onAddMacro: (String, List<String>) -> Unit,
    onExecute: (Macro) -> Unit
) {
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
            label = { Text("Macro name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = commands,
            onValueChange = { commands = it },
            label = { Text("Commands (one per line)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val list = commands.lines().filter { it.isNotBlank() }
            onAddMacro(name, list)
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
                Button(onClick = { onExecute(macro) }) {
                    Text("Run")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MacroPreview() {
    _3dCncTheme {
        MacroContent(
            macros = listOf(
                Macro("Home All", listOf("G28")),
                Macro("Probe Bed", listOf("G29", "M117 Probing..."))
            ),
            status = "Ready",
            onAddMacro = { _, _ -> },
            onExecute = {}
        )
    }
}
