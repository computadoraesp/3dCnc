package com.example.cnc3d.viewmodels

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.net.wifi.ScanResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.core.discovery.BluetoothScanner
import com.example.cnc3d.core.discovery.WifiScanner
import com.example.cnc3d.core.usb.UsbProber
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.MachineConfig
import com.example.cnc3d.domain.models.MachineProfile
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.models.ToolData
import com.example.cnc3d.domain.models.UsbDescriptor
import com.example.cnc3d.domain.models.cnc.CncStatus
import com.example.cnc3d.domain.repositories.MachineProfileRepository
import com.example.cnc3d.domain.usecases.CancelJobUseCase
import com.example.cnc3d.domain.usecases.GetStatusUseCase
import com.example.cnc3d.domain.usecases.ListFilesUseCase
import com.example.cnc3d.domain.usecases.SendCommandUseCase
import com.example.cnc3d.domain.usecases.StartJobUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CncViewModel @Inject constructor(
    private val getStatusUseCase: GetStatusUseCase,
    private val cancelJobUseCase: CancelJobUseCase,
    private val startJobUseCase: StartJobUseCase,
    private val sendCommandUseCase: SendCommandUseCase,
    private val listFilesUseCase: ListFilesUseCase,
    private val profileRepo: MachineProfileRepository,
    @ApplicationContext private val context: Context,
    private val wifiScanner: WifiScanner,
    private val bluetoothScanner: BluetoothScanner
) : ViewModel() {

    private val _status = MutableStateFlow(
        CncStatus("Disconnected", Triple(0f, 0f, 0f), 0, 0)
    )
    val status: StateFlow<CncStatus> = _status

    private val _wifiNetworks = MutableStateFlow<List<ScanResult>>(emptyList())
    val wifiNetworks: StateFlow<List<ScanResult>> = _wifiNetworks

    private val _bluetoothDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val bluetoothDevices: StateFlow<List<BluetoothDevice>> = _bluetoothDevices

    private val _usbDescriptors = MutableStateFlow<List<UsbDescriptor>>(emptyList())
    val usbDescriptors: StateFlow<List<UsbDescriptor>> = _usbDescriptors

    private val _manualUsbConfig = MutableStateFlow(UsbDescriptor(0, 0))
    val manualUsbConfig: StateFlow<UsbDescriptor> = _manualUsbConfig

    private val prober = UsbProber(context)

    private val _currentProfile = MutableStateFlow<MachineProfile?>(null)
    val currentProfile: StateFlow<MachineProfile?> = _currentProfile

    private val _editableProfile = MutableStateFlow<MachineProfile?>(null)
    val editableProfile: StateFlow<MachineProfile?> = _editableProfile

    private val _editableConfig = MutableStateFlow(MachineConfig())
    val editableConfig: StateFlow<MachineConfig> = _editableConfig

    private val _gcodePath = MutableStateFlow<GcodePath?>(null)
    val gcodePath: StateFlow<GcodePath?> = _gcodePath

    private val _mesh = MutableStateFlow<Mesh?>(null)
    val mesh: StateFlow<Mesh?> = _mesh

    private val _toolLibrary = MutableStateFlow(listOf(
        ToolData(1, "End Mill 6mm", 32.5f, 6.0f),
        ToolData(2, "Ball Mill 4mm", 45.0f, 4.0f)
    ))
    val toolLibrary: StateFlow<List<ToolData>> = _toolLibrary

    private val _availableFiles = MutableStateFlow<List<String>>(emptyList())
    val availableFiles: StateFlow<List<String>> = _availableFiles

    private val _selectedFile = MutableStateFlow<String?>(null)
    val selectedFile: StateFlow<String?> = _selectedFile

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val lastId = profileRepo.getLast()
            val profiles = profileRepo.getAll()
            val profile = profiles.find { it.id == lastId } ?: profiles.firstOrNull()
            _currentProfile.value = profile
            // Ensure editableProfile is never null for UI state purposes
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
            // Explicitly update current profile and its configuration
            _currentProfile.value = updatedProfile
            _editableProfile.value = updatedProfile
            _editableConfig.value = updatedConfig
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

            // Update local state
            _editableConfig.value = defaultConfig
            _editableProfile.value = resetProfile

            // Persist the reset
            val allProfiles = profileRepo.getAll().toMutableList()
            val index = allProfiles.indexOfFirst { it.id == resetProfile.id }
            if (index != -1) {
                allProfiles[index] = resetProfile
            }
            profileRepo.saveAll(allProfiles)
            _currentProfile.value = resetProfile

            // Optional: send reset command to hardware if connected
            sendMdi("\$RST=*") // FluidNC factory reset
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val result = getStatusUseCase()
            (result as? CncStatus)?.let {
                _status.value = it
            }
        }
    }

    fun start(file: String) {
        viewModelScope.launch {
            startJobUseCase(file)
        }
    }

    fun emergencyStop() {
        viewModelScope.launch {
            // In FluidNC, this would be a real reset or hard stop
            cancelJobUseCase()
        }
    }

    fun reset() {
        viewModelScope.launch {
            // Soft reset command for FluidNC
            sendMdi("\$Bye") 
            cancelJobUseCase()
        }
    }

    fun jog(axis: String, distance: Float, feed: Int = 3000) {
        viewModelScope.launch {
            val cmd = "\$J=G91 ${axis.uppercase()}$distance F$feed"
            sendMdi(cmd)
        }
    }

    fun sendMdi(command: String) {
        viewModelScope.launch {
            // Logic to send command to repo
            sendCommandUseCase(command)
            val current = _status.value
            _status.value = current.copy(mdiHistory = (current.mdiHistory + command).takeLast(100))
        }
    }

    fun zeroAxis(axis: String) {
        sendMdi("G10 L20 P1 ${axis.uppercase()}0")
    }

    fun goToZero() {
        sendMdi("G0 X0 Y0 Z0")
    }

    fun setWorkOffset(offset: String) {
        sendMdi(offset.uppercase())
        _status.value = _status.value.copy(activeOffset = offset.uppercase())
    }

    fun selectFile(name: String) {
        _selectedFile.value = name
    }

    fun addTool(name: String, length: Float, diameter: Float) {
        val nextId = (_toolLibrary.value.maxOfOrNull { it.id } ?: 0) + 1
        _toolLibrary.value += ToolData(nextId, name, length, diameter)
    }

    fun deleteTool(id: Int) {
        _toolLibrary.value = _toolLibrary.value.filter { it.id != id }
    }

    fun saveOffsets() {
        sendMdi("G10 L2") // Generic save offsets command for some controllers
    }

    fun startUsbDiscovery() {
        viewModelScope.launch {
            _usbDescriptors.value = prober.scanDevices()
            // If only one device, pre-fill manual config base
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
            _wifiNetworks.value = emptyList() // Clear previous results
            wifiScanner.scanNetworks().collect {
                _wifiNetworks.value = it
            }
        }
    }

    fun startBluetoothScan() {
        viewModelScope.launch {
            _bluetoothDevices.value = emptyList() // Clear previous results
            try {
                bluetoothScanner.scanDevices().collect { device ->
                    if (!_bluetoothDevices.value.contains(device)) {
                        _bluetoothDevices.value = _bluetoothDevices.value + device
                    }
                }
            } catch (e: Exception) {
                // Log error or notify UI
            }
        }
    }

    fun loadFiles() {
        viewModelScope.launch {
            try {
                _availableFiles.value = listFilesUseCase()
            } catch (_: Exception) {
                _availableFiles.value = emptyList()
            }
        }
    }
}
