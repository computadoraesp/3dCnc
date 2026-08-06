package com.example.cnc3d.core.network

import kotlinx.serialization.Serializable

@Serializable
enum class ConnectionType {
    WIFI,
    BLUETOOTH,
    USB
}
