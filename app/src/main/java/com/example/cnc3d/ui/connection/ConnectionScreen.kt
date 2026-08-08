package com.example.cnc3d.ui.connection

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.net.nsd.NsdServiceInfo
import android.net.wifi.ScanResult
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
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
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.ConnectionViewModel

@Composable
fun ConnectionScreen(vm: ConnectionViewModel) {
    val status by vm.status.collectAsState()
    val transport by vm.selectedTransport.collectAsState()
    val discoveredNetwork by vm.discoveredNetwork.collectAsState()
    val discoveredBluetooth by vm.discoveredBluetooth.collectAsState()
    val discoveredWifi by vm.discoveredWifi.collectAsState()

    ConnectionContent(
        status = status,
        transport = transport,
        discoveredNetwork = discoveredNetwork,
        discoveredBluetooth = discoveredBluetooth,
        discoveredWifi = discoveredWifi,
        onSetTransport = { vm.setTransport(it) },
        onConnect = { vm.connect(it) },
        onStartDiscovery = { vm.startDiscovery() }
    )
}

@Composable
fun ConnectionContent(
    status: String,
    transport: ConnectionType,
    discoveredNetwork: List<NsdServiceInfo>,
    discoveredBluetooth: List<BluetoothDevice>,
    discoveredWifi: List<ScanResult>,
    onSetTransport: (ConnectionType) -> Unit,
    onConnect: (String) -> Unit,
    onStartDiscovery: () -> Unit
) {
    var address by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("3dCNC# — Connect", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        // Transport Selection
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ConnectionType.entries.forEach { type ->
                FilterChip(
                    selected = transport == type,
                    onClick = { onSetTransport(type) },
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
            Button(onClick = { onConnect(address) }) {
                Text("Connect")
            }
            Button(onClick = { onStartDiscovery() }) {
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
                            supportingContent = { Text(service.host?.hostAddress ?: "") },
                            modifier = Modifier.clickable {
                                address = service.host?.hostAddress ?: ""
                            }
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
                            headlineContent = @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT) {
                                Text(
                                    device.name ?: "Unknown Device"
                                )
                            },
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

@Preview(showBackground = true)
@Composable
fun ConnectionPreview() {
    _3dCncTheme {
        ConnectionContent(
            status = "Disconnected",
            transport = ConnectionType.WIFI,
            discoveredNetwork = emptyList(),
            discoveredBluetooth = emptyList(),
            discoveredWifi = emptyList(),
            onSetTransport = {},
            onConnect = {},
            onStartDiscovery = {}
        )
    }
}
