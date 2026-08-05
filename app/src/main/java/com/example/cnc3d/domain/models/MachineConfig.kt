package com.example.cnc3d.domain.models

data class MachineConfig(
    val stepsPerMmX: Float? = null,
    val stepsPerMmY: Float? = null,
    val stepsPerMmZ: Float? = null,
    val maxFeedRate: Float? = null,
    val maxAcceleration: Float? = null
)
