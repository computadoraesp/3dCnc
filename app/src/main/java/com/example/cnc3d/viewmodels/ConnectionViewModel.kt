package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.bluetooth.BluetoothDevice
import android.net.nsd.NsdServiceInfo
import android.net.wifi.ScanResult
import com.example.cnc3d.core.discovery.BluetoothScanner
import com.example.cnc3d.core.discovery.NetworkScanner
import com.example.cnc3d.core.discovery.WifiScanner
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.domain.repositories.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val repo: MachineRepository,
    private val networkScanner: NetworkScanner,
    private val bluetoothScanner: BluetoothScanner,
    private val wifiScanner: WifiScanner
) : ViewModel() {

    private val _status = MutableStateFlow("Disconnected")
    val status: StateFlow<String> = _status

    private val _firmware = MutableStateFlow("Unknown")
    val firmware: StateFlow<String> = _firmware

    private val _selectedTransport = MutableStateFlow(ConnectionType.WIFI)
    val selectedTransport: StateFlow<ConnectionType> = _selectedTransport

    private val _discoveredNetwork = MutableStateFlow<List<NsdServiceInfo>>(emptyList())
    val discoveredNetwork: StateFlow<List<NsdServiceInfo>> = _discoveredNetwork

    private val _discoveredBluetooth = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredBluetooth: StateFlow<List<BluetoothDevice>> = _discoveredBluetooth

    private val _discoveredWifi = MutableStateFlow<List<ScanResult>>(emptyList())
    val discoveredWifi: StateFlow<List<ScanResult>> = _discoveredWifi

    fun setTransport(type: ConnectionType) {
        _selectedTransport.value = type
    }

    fun startDiscovery() {
        viewModelScope.launch {
            when (_selectedTransport.value) {
                ConnectionType.WIFI -> {
                    // Scan for both MDNS services and SSIDs
                    launch {
                        networkScanner.scanServices().collect { service ->
                            _discoveredNetwork.value = (_discoveredNetwork.value + service).distinctBy { it.serviceName }
                        }
                    }
                    launch {
                        wifiScanner.scanNetworks().collect { results ->
                            _discoveredWifi.value = results.distinctBy { it.SSID }
                        }
                    }
                }
                ConnectionType.BLUETOOTH -> {
                    bluetoothScanner.scanDevices().collect { device ->
                        _discoveredBluetooth.value = (_discoveredBluetooth.value + device).distinctBy { it.address }
                    }
                }
                ConnectionType.USB -> {
                    // USB is usually direct, no "scan" needed other than listing drivers
                }
            }
        }
    }

    fun connect(address: String) {
        viewModelScope.launch {
            try {
                val ok = repo.connect(address, _selectedTransport.value)
                _status.value = if (ok) "Connected" else "Error"
            } catch (e: Exception) {
                _status.value = "Error: ${e.message}"
            }
        }
    }

    fun detect(ip: String) {
        viewModelScope.launch {
            try {
                val fw = repo.detectFirmware(ip)
                _firmware.value = fw
                _status.value = "Firmware: $fw"
            } catch (e: Exception) {
                _status.value = "Detection error: ${e.message}"
            }
        }
    }
}
