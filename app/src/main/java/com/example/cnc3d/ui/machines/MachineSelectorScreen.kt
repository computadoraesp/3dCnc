package com.example.cnc3d.app.ui.machines

import androidx.compose.foundation.clickable
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
import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.domain.models.MachineProfile
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.MachineSelectorViewModel

@Composable
fun MachineSelectorScreen(vm: MachineSelectorViewModel) {
    val machines by vm.machines.collectAsState()
    val selected by vm.selected.collectAsState()
    MachineSelectorContent(
        machines = machines,
        selected = selected,
        onSelect = { vm.select(it) },
        onAdd = { name, ip -> vm.add(name, ip, FirmwareType.FLUIDNC, ConnectionType.WIFI) }
    )
}

@Composable
fun MachineSelectorContent(
    machines: List<MachineProfile>,
    selected: MachineProfile?,
    onSelect: (MachineProfile) -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("Machines", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        machines.forEach { machine ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(machine) }
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
            onAdd(name, ip)
        }) {
            Text("Add Machine")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MachineSelectorPreview() {
    _3dCncTheme {
        val m1 = MachineProfile(
            "1",
            "CNC Mill",
            ConnectionType.WIFI,
            "192.168.1.50",
            FirmwareType.FLUIDNC
        )
        MachineSelectorContent(
            machines = listOf(
                m1,
                MachineProfile(
                    "2",
                    "3D Printer",
                    ConnectionType.WIFI,
                    "192.168.1.60",
                    FirmwareType.MOONRAKER
                )
            ),
            selected = m1,
            onSelect = {},
            onAdd = { _, _ -> }
        )
    }
}
