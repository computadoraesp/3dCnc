package com.example.cnc3d.domain.models

import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.core.network.ConnectionType

data class MachineProfile(
    val id: String,
    val name: String,
    val connectionType: ConnectionType = ConnectionType.WIFI,
    val address: String, // IP, MAC, or USB ID
    val firmware: FirmwareType,
    val autoConnect: Boolean = false
)
