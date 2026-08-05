package com.example.cnc3d.domain.models

data class GcodeSegment(
    val x1: Float,
    val y1: Float,
    val z1: Float,
    val x2: Float,
    val y2: Float,
    val z2: Float,
    val rapid: Boolean = false,
    val extrude: Boolean = false
)

data class GcodePath(
    val segments: List<GcodeSegment>
)
