package com.example.cnc3d.domain.models

data class PrinterWarning(
    val type: PrinterWarningType,
    val message: String,
    val line: Int
)

enum class PrinterWarningType {
    EXTRUSION_TOO_HIGH,
    RETRACTION_TOO_LONG,
    MOVE_WITHOUT_HEAT,
    LAYER_JUMP,
    SPEED_TOO_HIGH,
    NO_EXTRUSION,
    TEMP_TOO_LOW,
    TEMP_TOO_HIGH,
    FAN_OFF_HIGH_LAYER,
    UNKNOWN
}
