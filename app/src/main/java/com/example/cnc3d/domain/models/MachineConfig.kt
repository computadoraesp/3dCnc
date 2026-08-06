package com.example.cnc3d.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class MachineConfig(
    val stepsPerMmX: Float = 80f,
    val stepsPerMmY: Float = 80f,
    val stepsPerMmZ: Float = 400f,
    val maxFeedRate: Float = 5000f,
    val maxAcceleration: Float = 500f,
    val minSpindleSpeed: Float = 0f,
    val maxSpindleSpeed: Float = 24000f,
    val defaultSeekRate: Float = 3000f,
    val units: String = "mm",
    val baudRate: Int = 115200,
    val wifiSsid: String = "",
    val httpEndpoint: String = "/api/v1",
    val wsEndpoint: String = "/websocket"
)
