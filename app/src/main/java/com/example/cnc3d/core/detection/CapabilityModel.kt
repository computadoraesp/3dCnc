package com.example.cnc3d.core.detection

data class Capabilities(
    val axes: Int = 0,
    val hasSpindle: Boolean = false,
    val hasExtruder: Boolean = false,
    val hasHeatedBed: Boolean = false,
    val supportsGcodeStreaming: Boolean = false,
    val supportsRealtimeEvents: Boolean = false
)
