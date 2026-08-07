package com.example.cnc3d.viewmodels

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.core.discovery.BluetoothScanner
import com.example.cnc3d.core.discovery.WifiScanner
import com.example.cnc3d.core.service.MachineMonitoringService
import com.example.cnc3d.core.usb.UsbProber
import com.example.cnc3d.domain.models.MachineConfig
import com.example.cnc3d.domain.models.MachineProfile
import com.example.cnc3d.domain.models.UsbDescriptor
import com.example.cnc3d.domain.repositories.MachineProfileRepository
import com.example.cnc3d.domain.usecases.ConnectUseCase
import com.example.cnc3d.domain.usecases.SendCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CncConfigViewModel @Inject constructor(
    private val profileRepo: MachineProfileRepository,
    @ApplicationContext private val context: Context,
    private val wifiScanner: WifiScanner,
    private val bluetoothScanner: BluetoothScanner,
    private val connectUseCase: ConnectUseCase,
    private val sendCommandUseCase: SendCommandUseCase
) : ViewModel() {

    private val _currentProfile = MutableStateFlow<MachineProfile?>(null)
    val currentProfile: StateFlow<MachineProfile?> = _currentProfile

    private val _editableProfile = MutableStateFlow<MachineProfile?>(null)
    val editableProfile: StateFlow<MachineProfile?> = _editableProfile

    private val _editableConfig = MutableStateFlow(MachineConfig())
    val editableConfig: StateFlow<MachineConfig> = _editableConfig

    private val _wifiNetworks = MutableStateFlow<List<ScanResult>>(emptyList())
    val wifiNetworks: StateFlow<List<ScanResult>> = _wifiNetworks

    private val _bluetoothDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val bluetoothDevices: StateFlow<List<BluetoothDevice>> = _bluetoothDevices

    private val _usbDescriptors = MutableStateFlow<List<UsbDescriptor>>(emptyList())
    val usbDescriptors: StateFlow<List<UsbDescriptor>> = _usbDescriptors

    private val _manualUsbConfig = MutableStateFlow(UsbDescriptor(0, 0))
    val manualUsbConfig: StateFlow<UsbDescriptor> = _manualUsbConfig

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    private val prober = UsbProber(context)

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val lastId = profileRepo.getLast()
            val profiles = profileRepo.getAll()
            val profile = profiles.find { it.id == lastId } ?: profiles.firstOrNull()
            _currentProfile.value = profile
            _editableProfile.value = profile ?: MachineProfile(
                id = "temporary",
                name = "New Machine",
                address = "192.168.0.1",
                firmware = com.example.cnc3d.core.detection.FirmwareType.UNKNOWN
            )
            _editableConfig.value = profile?.config ?: MachineConfig()
        }
    }

    fun updateConfig(update: (MachineConfig) -> MachineConfig) {
        _editableConfig.value = update(_editableConfig.value)
    }

    fun updateProfile(update: (MachineProfile) -> MachineProfile) {
        _editableProfile.value = _editableProfile.value?.let { update(it) }
    }

    fun saveConfiguration() {
        viewModelScope.launch {
            val baseProfile = _editableProfile.value ?: return@launch
            val updatedConfig = _editableConfig.value
            val updatedProfile = baseProfile.copy(config = updatedConfig)

            val allProfiles = profileRepo.getAll().toMutableList()
            val index = allProfiles.indexOfFirst { it.id == updatedProfile.id }
            if (index != -1) {
                allProfiles[index] = updatedProfile
            } else {
                allProfiles.add(updatedProfile)
            }

            profileRepo.saveAll(allProfiles)
            _currentProfile.value = updatedProfile
            _editableProfile.value = updatedProfile
            _editableConfig.value = updatedConfig
            _uiMessage.emit("Configuration Saved")
        }
    }

    fun factoryReset() {
        viewModelScope.launch {
            val defaultConfig = MachineConfig()
            val current = _currentProfile.value ?: return@launch

            val resetProfile = current.copy(
                config = defaultConfig,
                connectionType = com.example.cnc3d.core.network.ConnectionType.WIFI,
                address = "192.168.0.1"
            )

            _editableConfig.value = defaultConfig
            _editableProfile.value = resetProfile

            val allProfiles = profileRepo.getAll().toMutableList()
            val index = allProfiles.indexOfFirst { it.id == resetProfile.id }
            if (index != -1) {
                allProfiles[index] = resetProfile
            }
            profileRepo.saveAll(allProfiles)
            _currentProfile.value = resetProfile

            sendCommandUseCase("\$RST=*")
            _uiMessage.emit("Factory Reset Complete")
        }
    }

    fun startUsbDiscovery() {
        viewModelScope.launch {
            _usbDescriptors.value = prober.scanDevices()
            if (_usbDescriptors.value.size == 1) {
                _manualUsbConfig.value = _usbDescriptors.value[0]
            }
        }
    }

    fun updateManualUsb(update: (UsbDescriptor) -> UsbDescriptor) {
        _manualUsbConfig.value = update(_manualUsbConfig.value)
    }

    fun startWifiScan() {
        viewModelScope.launch {
            _wifiNetworks.value = emptyList()
            wifiScanner.scanNetworks().collect {
                _wifiNetworks.value = it
            }
        }
    }

    fun startBluetoothScan() {
        viewModelScope.launch {
            _bluetoothDevices.value = emptyList()
            try {
                bluetoothScanner.scanDevices().collect { device ->
                    if (!_bluetoothDevices.value.contains(device)) {
                        _bluetoothDevices.value = _bluetoothDevices.value + device
                    }
                }
            } catch (e: Exception) {
                _uiMessage.emit("Bluetooth Error: ${e.message}")
            }
        }
    }

    fun connect() {
        viewModelScope.launch {
            _currentProfile.value?.let {
                val ok = connectUseCase(it.address, it.connectionType)
                if (ok) {
                    val intent = Intent(context, MachineMonitoringService::class.java)
                    if (android.os.Build.VERSION.SDK_INT >= 26) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                }
            }
        }
    }
}
