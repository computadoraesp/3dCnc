package com.example.cnc3d.domain.models.cnc

data class CNCPosition(
    val x: Float,
    val y: Float,
    val z: Float,
    val spindleRpm: Int,
    val feedRate: Int
)