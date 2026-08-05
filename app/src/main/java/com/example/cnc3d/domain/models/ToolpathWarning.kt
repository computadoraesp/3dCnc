package com.example.cnc3d.domain.models



data class ToolpathWarning(
    val type: WarningType,
    val message: String,
    val line: Int
)

enum class WarningType {
    Z_NEGATIVE,
    FEEDRATE_HIGH,
    SPINDLE_OFF,
    RAPID_INTO_MATERIAL,
    COLLISION_RISK,
    UNKNOWN
}
