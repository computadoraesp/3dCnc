package com.example.cnc3d.domain.models

data class MeshPoint(
    val x: Float,
    val y: Float,
    val z: Float
)

data class Mesh(
    val points: List<MeshPoint>
)
