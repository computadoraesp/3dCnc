package com.example.cnc3d.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UsbDescriptor(
    val vendorId: Int,
    val productId: Int,
    val deviceClass: Int = -1,
    val deviceSubclass: Int = -1,
    val deviceProtocol: Int = -1,
    val interfaceIndex: Int = -1,
    val inputEndpoint: Int = -1,
    val outputEndpoint: Int = -1,
    val transferType: Int = -1, // 0=Control, 1=Iso, 2=Bulk, 3=Interrupt
    val deviceName: String = "Unknown Device"
) {
    fun isFullyVisible(): Boolean {
        // Full CDC-ACM: Class=2, Subclass=2, Protocol=1, and valid endpoints
        return deviceClass == 0x02 &&
                deviceSubclass == 0x02 &&
                deviceProtocol == 0x01 &&
                interfaceIndex != -1 &&
                inputEndpoint != -1 &&
                outputEndpoint != -1
    }

    fun isSemiInvisible(): Boolean {
        // VID/PID present but missing class, interface or endpoints
        return !isFullyVisible() && (deviceClass != -1 || interfaceIndex != -1 || inputEndpoint != -1)
    }

    fun isFullyInvisible(): Boolean {
        // Only VID/PID available
        return deviceClass == -1 && interfaceIndex == -1 && inputEndpoint == -1
    }
}
