package com.example.cnc3d.domain.models

data class AxisState(
    val position: Float = 0f,
    val machineLimitMin: Float? = null,
    val machineLimitMax: Float? = null,
    val homed: Boolean = false
)