package com.example.cnc3d.domain.models.cnc

data class CncStatus(
    val state: String = "Unknown",
    val position: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val spindleRpm: Int = 0,
    val feedRate: Int = 0,
    val sensors: Map<String, Boolean> = emptyMap(),
    val overrides: Triple<Int, Int, Int> = Triple(100, 100, 100), // Feed, Spindle, Rapid
    val mdiHistory: List<String> = emptyList(),
    val activeOffset: String = "G54",
    val firmwareVersion: String = "Unknown",
    val uptime: String = "00:00:00"
) {
    companion object {
        val DISCONNECTED = CncStatus(
            state = "Disconnected",
            position = Triple(0f, 0f, 0f),
            spindleRpm = 0,
            feedRate = 0
        )
    }
}
