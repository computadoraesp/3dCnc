package com.example.cnc3d.domain.models

data class BedMeshPoint(
    val x: Float,
    val y: Float,
    val z: Float
)

data class BedMesh(
    val points: List<BedMeshPoint>,
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float
)
