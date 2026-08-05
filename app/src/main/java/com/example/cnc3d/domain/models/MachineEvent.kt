package com.example.cnc3d.domain.models

data class MachineEvent(
    val type: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
