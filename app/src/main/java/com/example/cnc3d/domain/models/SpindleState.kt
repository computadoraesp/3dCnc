package com.example.cnc3d.domain.models

data class SpindleState(
    val enabled: Boolean = false,
    val speed: Int = 0,
    val direction: String = "CW" // CW or CCW
)
