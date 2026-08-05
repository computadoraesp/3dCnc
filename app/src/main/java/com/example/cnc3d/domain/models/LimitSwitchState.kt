package com.example.cnc3d.domain.models

data class LimitSwitchState(
    val xMin: Boolean = false,
    val xMax: Boolean = false,
    val yMin: Boolean = false,
    val yMax: Boolean = false,
    val zMin: Boolean = false,
    val zMax: Boolean = false
)
