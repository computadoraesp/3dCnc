package com.example.cnc3d.core.detection

import kotlinx.serialization.Serializable

@Serializable
enum class FirmwareType {
    FLUIDNC,
    MOONRAKER,
    UNKNOWN
}
