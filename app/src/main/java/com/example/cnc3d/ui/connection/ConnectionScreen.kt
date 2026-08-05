package com.example.cnc3d.app.ui.connection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.viewmodels.ConnectionViewModel

@Composable
fun ConnectionScreen(vm: ConnectionViewModel) {

    var address by remember { mutableStateOf("") }
    val status by vm.status.collectAsState()
    val transport by vm.selectedTransport.collectAsState()
    val discoveredNetwork by vm.discoveredNetwork.collectAsState()
    val discoveredBluetooth by vm.discoveredBluetooth.collectAsState()
    val discoveredWifi by vm.discoveredWifi.collectAsState()

    Column(Modifier.padding(16.dp)) {

        Text("3dCNC# — Connect", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        // Transport Selection
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ConnectionType.entries.forEach { type ->
                FilterChip(
                    selected = transport == type,
                    onClick = { vm.setTransport(type) },
                    label = { Text(type.name) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { 
                Text(when(transport) {
                    ConnectionType.WIFI -> "Machine IP"
                    ConnectionType.BLUETOOTH -> "Bluetooth MAC"
                    ConnectionType.USB -> "Direct USB (Auto)"
                })
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.connect(address) }) {
                Text("Connect")
            }
            Button(onClick = { vm.startDiscovery() }) {
                Text("Discover Devices")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Discovered Devices:", style = MaterialTheme.typography.titleMedium)
        
        LazyColumn(Modifier.weight(1f)) {
            when (transport) {
                ConnectionType.WIFI -> {
                    item { Text("MDNS Services:", style = MaterialTheme.typography.labelLarge) }
                    items(discoveredNetwork) { service ->
                        ListItem(
                            headlineContent = { Text(service.serviceName) },
                            supportingContent = { Text(service.host.hostAddress ?: "") },
                            modifier = Modifier.clickable { address = service.host.hostAddress ?: "" }
                        )
                    }
                    item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
                    item { Text("WiFi Networks (AP Mode):", style = MaterialTheme.typography.labelLarge) }
                    items(discoveredWifi) { result ->
                        ListItem(
                            headlineContent = { Text(result.SSID) },
                            supportingContent = { Text("Signal: ${result.level} dBm") },
                            modifier = Modifier.clickable { /* Logic to connect to AP if needed */ }
                        )
                    }
                }
                ConnectionType.BLUETOOTH -> {
                    items(discoveredBluetooth) { device ->
                        ListItem(
                            headlineContent = { Text(device.name ?: "Unknown Device") },
                            supportingContent = { Text(device.address) },
                            modifier = Modifier.clickable { address = device.address }
                        )
                    }
                }
                ConnectionType.USB -> {
                    item { Text("USB device will be connected automatically.") }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Status: $status")
    }
}
