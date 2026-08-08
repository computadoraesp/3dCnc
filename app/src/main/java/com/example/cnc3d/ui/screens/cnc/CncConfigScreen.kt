package com.example.cnc3d.ui.screens.cnc

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.domain.models.MachineConfig
import com.example.cnc3d.domain.models.MachineProfile
import com.example.cnc3d.domain.models.UsbDescriptor
import com.example.cnc3d.ui.theme.IndustrialButton
import com.example.cnc3d.ui.theme.IndustrialColors
import com.example.cnc3d.ui.theme.IndustrialPanel
import com.example.cnc3d.ui.theme.IndustrialTextField
import com.example.cnc3d.ui.theme.LocalSnackbarHost
import com.example.cnc3d.ui.theme._3dCncTheme
import com.example.cnc3d.viewmodels.CncConfigViewModel
import kotlinx.coroutines.launch

@Composable
fun CncConfigScreen(viewModel: CncConfigViewModel) {
    val config by viewModel.editableConfig.collectAsState()
    val profile by viewModel.editableProfile.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState("")
    val wifiNetworks by viewModel.wifiNetworks.collectAsState()
    val bluetoothDevices by viewModel.bluetoothDevices.collectAsState()
    val usbDescriptors by viewModel.usbDescriptors.collectAsState()
    val manualUsbConfig by viewModel.manualUsbConfig.collectAsState()

    CncConfigContent(
        config = config,
        profile = profile,
        uiMessage = uiMessage,
        wifiNetworks = wifiNetworks,
        bluetoothDevices = bluetoothDevices,
        usbDescriptors = usbDescriptors,
        manualUsbConfig = manualUsbConfig,
        onUpdateProfile = { viewModel.updateProfile(it) },
        onUpdateConfig = { viewModel.updateConfig(it) },
        onSave = { viewModel.saveConfiguration() },
        onFactoryReset = { viewModel.factoryReset() },
        onStartUsbDiscovery = { viewModel.startUsbDiscovery() },
        onUpdateManualUsb = { viewModel.updateManualUsb(it) },
        onStartWifiScan = { viewModel.startWifiScan() },
        onStartBluetoothScan = { viewModel.startBluetoothScan() }
    )
}

@Composable
fun CncConfigContent(
    config: MachineConfig,
    profile: MachineProfile?,
    uiMessage: String,
    wifiNetworks: List<android.net.wifi.ScanResult>,
    bluetoothDevices: List<android.bluetooth.BluetoothDevice>,
    usbDescriptors: List<UsbDescriptor>,
    manualUsbConfig: UsbDescriptor,
    onUpdateProfile: ((MachineProfile) -> MachineProfile) -> Unit,
    onUpdateConfig: ((MachineConfig) -> MachineConfig) -> Unit,
    onSave: () -> Unit,
    onFactoryReset: () -> Unit,
    onStartUsbDiscovery: () -> Unit,
    onUpdateManualUsb: ((UsbDescriptor) -> UsbDescriptor) -> Unit,
    onStartWifiScan: () -> Unit,
    onStartBluetoothScan: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialPanel(title = "Machine Parameters") {
            IndustrialTextField(
                value = profile?.name ?: "",
                onValueChange = { name -> onUpdateProfile { it.copy(name = name) } },
                label = "Machine Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Toggle Units on click
            val unitLabel = if (config.units == "mm") "Metric (MM)" else "Imperial (INCH)"
            val snackbarHost = LocalSnackbarHost.current
            val scope = rememberCoroutineScope()

            ConfigRow(
                label = "Units of Measurement",
                value = unitLabel,
                modifier = Modifier.clickable {
                    val next = if (config.units == "mm") "inch" else "mm"
                    onUpdateConfig { it.copy(units = next) }
                    scope.launch {
                        snackbarHost.currentSnackbarData?.dismiss()
                        snackbarHost.showSnackbar("Units set to: ${next.uppercase()}")
                    }
                }
            )

            ConfigRow("Firmware", profile?.firmware?.name ?: "UNKNOWN")
        }

        IndustrialPanel(title = "Axis Calibration") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialTextField(
                    value = config.stepsPerMmX.toString(),
                    onValueChange = {
                        val v = it.toFloatOrNull() ?: 0f
                        onUpdateConfig { it.copy(stepsPerMmX = v) }
                    },
                    label = "X Steps/unit",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                IndustrialTextField(
                    value = config.stepsPerMmY.toString(),
                    onValueChange = {
                        val v = it.toFloatOrNull() ?: 0f
                        onUpdateConfig { it.copy(stepsPerMmY = v) }
                    },
                    label = "Y Steps/unit",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            IndustrialTextField(
                value = config.stepsPerMmZ.toString(),
                onValueChange = {
                    val v = it.toFloatOrNull() ?: 0f
                    onUpdateConfig { it.copy(stepsPerMmZ = v) }
                },
                label = "Z Steps/unit",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        IndustrialPanel(title = "Motion & Spindle") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialTextField(
                    value = config.maxSpindleSpeed.toString(),
                    onValueChange = {
                        val v = it.toFloatOrNull() ?: 0f
                        onUpdateConfig { it.copy(maxSpindleSpeed = v) }
                    },
                    label = "Max RPM",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                IndustrialTextField(
                    value = config.minSpindleSpeed.toString(),
                    onValueChange = {
                        val v = it.toFloatOrNull() ?: 0f
                        onUpdateConfig { it.copy(minSpindleSpeed = v) }
                    },
                    label = "Min RPM",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialTextField(
                    value = config.maxFeedRate.toString(),
                    onValueChange = {
                        val v = it.toFloatOrNull() ?: 0f
                        onUpdateConfig { it.copy(maxFeedRate = v) }
                    },
                    label = "Max Feed",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                IndustrialTextField(
                    value = config.defaultSeekRate.toString(),
                    onValueChange = {
                        val v = it.toFloatOrNull() ?: 0f
                        onUpdateConfig { it.copy(defaultSeekRate = v) }
                    },
                    label = "Seek Rate",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        IndustrialPanel(title = "Hardware Connectivity") {
            var showTypeSelector by remember { mutableStateOf(false) }
            var showParamsSelector by remember { mutableStateOf(true) }
            val snackbarHost = LocalSnackbarHost.current
            val scope = rememberCoroutineScope()

            // Level 1: Selection Trigger
            ConfigRow(
                label = "Active Connection",
                value = profile?.connectionType?.name ?: "TAP TO SELECT TYPE",
                modifier = Modifier.clickable {
                    showTypeSelector = !showTypeSelector
                },
                trailingIcon = if (showTypeSelector) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight
            )

            // Level 2: Connection Type Selection Chips
            if (showTypeSelector) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ConnectionType.entries.forEach { type ->
                        FilterChip(
                            selected = profile?.connectionType == type,
                            onClick = {
                                onUpdateProfile { it.copy(connectionType = type) }
                                showTypeSelector = false
                                showParamsSelector = true // Auto-expand technical params
                                scope.launch {
                                    snackbarHost.currentSnackbarData?.dismiss()
                                    snackbarHost.showSnackbar("Mode: ${type.name} - Settings visible below")
                                }
                            },
                            label = { Text(type.name, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndustrialColors.Accent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                HorizontalDivider(color = IndustrialColors.Border, thickness = 0.5.dp)
            }

            // Level 3: Immediate Parameters & Discovery
            if (profile?.connectionType != null) {
                if (showParamsSelector) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (profile.connectionType) {
                            ConnectionType.WIFI -> {
                                LaunchedEffect(Unit) { onStartWifiScan() }

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(
                                        "SELECT NETWORK",
                                        color = IndustrialColors.TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IndustrialButton(
                                        text = "Scan",
                                        onClick = onStartWifiScan,
                                        modifier = Modifier.height(32.dp),
                                        containerColor = IndustrialColors.Border
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .border(1.dp, IndustrialColors.Border)
                                        .padding(4.dp)
                                ) {
                                    if (wifiNetworks.isEmpty()) {
                                        Text(
                                            "Searching for Wi-Fi...",
                                            color = IndustrialColors.TextSecondary,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    wifiNetworks.take(5).forEach { net ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onUpdateConfig { it.copy(wifiSsid = net.SSID) }
                                                    scope.launch {
                                                        snackbarHost.currentSnackbarData?.dismiss()
                                                        snackbarHost.showSnackbar("Network: ${net.SSID}")
                                                    }
                                                }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                net.SSID,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "${net.level} dBm",
                                                color = IndustrialColors.Accent,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                IndustrialTextField(
                                    value = profile.address,
                                    onValueChange = { addr ->
                                        onUpdateProfile { it.copy(address = addr) }
                                    },
                                    label = "Static IP / Hostname",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                IndustrialTextField(
                                    value = config.wifiSsid,
                                    onValueChange = { ssid ->
                                        onUpdateConfig { it.copy(wifiSsid = ssid) }
                                    },
                                    label = "Selected SSID",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IndustrialTextField(
                                        value = config.httpEndpoint,
                                        onValueChange = { ep ->
                                            onUpdateConfig { it.copy(httpEndpoint = ep) }
                                        },
                                        label = "API Path",
                                        modifier = Modifier.weight(1f)
                                    )
                                    IndustrialTextField(
                                        value = config.wsEndpoint,
                                        onValueChange = { ep ->
                                            onUpdateConfig { it.copy(wsEndpoint = ep) }
                                        },
                                        label = "WS Path",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            ConnectionType.BLUETOOTH -> {
                                LaunchedEffect(Unit) { onStartBluetoothScan() }

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(
                                        "SELECT DEVICE",
                                        color = IndustrialColors.TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IndustrialButton(
                                        text = "Discover",
                                        onClick = onStartBluetoothScan,
                                        modifier = Modifier.height(32.dp),
                                        containerColor = IndustrialColors.Border
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .border(1.dp, IndustrialColors.Border)
                                        .padding(4.dp)
                                ) {
                                    if (bluetoothDevices.isEmpty()) {
                                        Text(
                                            "Searching... Ensure Bluetooth is ON and permissions granted",
                                            color = IndustrialColors.Warning,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    bluetoothDevices.forEach { dev ->
                                        @SuppressLint("MissingPermission")
                                        val name = dev.name ?: "Unknown Device"
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onUpdateProfile { it.copy(address = dev.address) }
                                                    scope.launch {
                                                        snackbarHost.currentSnackbarData?.dismiss()
                                                        snackbarHost.showSnackbar("Device: $name")
                                                    }
                                                }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                name,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                dev.address,
                                                color = IndustrialColors.Accent,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                IndustrialTextField(
                                    value = profile.address,
                                    onValueChange = { addr ->
                                        onUpdateProfile { it.copy(address = addr) }
                                    },
                                    label = "Manual MAC Address",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            ConnectionType.USB -> {
                                LaunchedEffect(Unit) { onStartUsbDiscovery() }

                                if (usbDescriptors.isEmpty()) {
                                    Column(
                                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                    ) {
                                        Text(
                                            "No USB device is connected.",
                                            color = IndustrialColors.Error,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        IndustrialButton(
                                            "Verify physical connection",
                                            onClick = onStartUsbDiscovery,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                } else {
                                    usbDescriptors.forEach { desc ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(IndustrialColors.Border.copy(alpha = 0.3f))
                                                .border(1.dp, IndustrialColors.Border)
                                                .padding(12.dp)
                                        ) {
                                            ConfigRow("Physical Device", desc.deviceName)
                                            ConfigRow(
                                                "USB ID",
                                                "${
                                                    Integer.toHexString(desc.vendorId).uppercase()
                                                }:${
                                                    Integer.toHexString(desc.productId).uppercase()
                                                }"
                                            )

                                            Spacer(Modifier.height(8.dp))

                                            if (desc.isFullyVisible()) {
                                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                                    Box(
                                                        Modifier
                                                            .size(8.dp)
                                                            .background(
                                                                IndustrialColors.Success,
                                                                RoundedCornerShape(4.dp)
                                                            )
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        "STATUS: READY (PLUG-AND-PLAY)",
                                                        color = IndustrialColors.Success,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            } else {
                                                val isInvisible = desc.isFullyInvisible()
                                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                                    Box(
                                                        Modifier
                                                            .size(8.dp)
                                                            .background(
                                                                IndustrialColors.Warning,
                                                                RoundedCornerShape(4.dp)
                                                            )
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        if (isInvisible) "STATUS: INVISIBLE (ONLY VID/PID)" else "STATUS: SEMI-VISIBLE (MISSING DATA)",
                                                        color = IndustrialColors.Warning,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                Spacer(Modifier.height(12.dp))
                                                Text(
                                                    "MANUALLY ENTER MISSING PARAMETERS:",
                                                    color = IndustrialColors.TextSecondary,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(Modifier.height(8.dp))

                                                if (desc.deviceClass == -1) {
                                                    IndustrialTextField(
                                                        value = manualUsbConfig.deviceClass.takeIf { it != -1 }
                                                            ?.toString() ?: "",
                                                        onValueChange = {
                                                            it.toIntOrNull()?.let { v ->
                                                                onUpdateManualUsb {
                                                                    it.copy(
                                                                        deviceClass = v
                                                                    )
                                                                }
                                                            } ?: onUpdateManualUsb {
                                                                it.copy(
                                                                    deviceClass = -1
                                                                )
                                                            }
                                                        },
                                                        label = "USB Device Class",
                                                        keyboardOptions = KeyboardOptions(
                                                            keyboardType = KeyboardType.Number
                                                        )
                                                    )
                                                }
                                                if (desc.interfaceIndex == -1) {
                                                    IndustrialTextField(
                                                        value = manualUsbConfig.interfaceIndex.takeIf { it != -1 }
                                                            ?.toString() ?: "",
                                                        onValueChange = {
                                                            it.toIntOrNull()?.let { v ->
                                                                onUpdateManualUsb { u ->
                                                                    u.copy(
                                                                        interfaceIndex = v
                                                                    )
                                                                }
                                                            } ?: onUpdateManualUsb { u ->
                                                                u.copy(
                                                                    interfaceIndex = -1
                                                                )
                                                            }
                                                        },
                                                        label = "Interface Index",
                                                        keyboardOptions = KeyboardOptions(
                                                            keyboardType = KeyboardType.Number
                                                        )
                                                    )
                                                }
                                                if (desc.inputEndpoint == -1) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            8.dp
                                                        )
                                                    ) {
                                                        IndustrialTextField(
                                                            value = manualUsbConfig.inputEndpoint.takeIf { it != -1 }
                                                                ?.toString() ?: "",
                                                            onValueChange = {
                                                                it.toIntOrNull()?.let { v ->
                                                                    onUpdateManualUsb { u ->
                                                                        u.copy(
                                                                            inputEndpoint = v
                                                                        )
                                                                    }
                                                                } ?: onUpdateManualUsb { u ->
                                                                    u.copy(
                                                                        inputEndpoint = -1
                                                                    )
                                                                }
                                                            },
                                                            label = "Input EP",
                                                            modifier = Modifier.weight(1f),
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                            )
                                                        )
                                                        IndustrialTextField(
                                                            value = manualUsbConfig.outputEndpoint.takeIf { it != -1 }
                                                                ?.toString() ?: "",
                                                            onValueChange = {
                                                                it.toIntOrNull()?.let { v ->
                                                                    onUpdateManualUsb { u ->
                                                                        u.copy(
                                                                            outputEndpoint = v
                                                                        )
                                                                    }
                                                                } ?: onUpdateManualUsb { u ->
                                                                    u.copy(
                                                                        outputEndpoint = -1
                                                                    )
                                                                }
                                                            },
                                                            label = "Output EP",
                                                            modifier = Modifier.weight(1f),
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(12.dp))
                                    }
                                }

                                IndustrialTextField(
                                    value = config.baudRate.toString(),
                                    onValueChange = {
                                        val v = it.toIntOrNull() ?: 115200
                                        onUpdateConfig { it.copy(baudRate = v) }
                                    },
                                    label = "Serial Baud Rate",
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    }
                }
            }
        }

        IndustrialButton(
            text = "Save Settings",
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            containerColor = IndustrialColors.Success
        )

        IndustrialButton(
            text = "Factory Reset (Defaults)",
            onClick = onFactoryReset,
            modifier = Modifier.fillMaxWidth(),
            containerColor = IndustrialColors.Emergency
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CncConfigPreview() {
    _3dCncTheme {
        CncConfigContent(
            config = MachineConfig(),
            profile = MachineProfile(
                id = "1",
                name = "Test CNC",
                connectionType = ConnectionType.WIFI,
                address = "192.168.1.100",
                firmware = com.example.cnc3d.core.detection.FirmwareType.FLUIDNC
            ),
            uiMessage = "",
            wifiNetworks = emptyList(),
            bluetoothDevices = emptyList(),
            usbDescriptors = emptyList(),
            manualUsbConfig = UsbDescriptor(0, 0),
            onUpdateProfile = {},
            onUpdateConfig = {},
            onSave = {},
            onFactoryReset = {},
            onStartUsbDiscovery = {},
            onUpdateManualUsb = {},
            onStartWifiScan = {},
            onStartBluetoothScan = {}
        )
    }
}

@Composable
private fun ConfigRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(label, color = IndustrialColors.TextSecondary, fontSize = 12.sp)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(
                value,
                color = IndustrialColors.Accent,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = IndustrialColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
