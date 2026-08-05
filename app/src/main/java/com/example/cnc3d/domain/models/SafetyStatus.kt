package com.example.cnc3d.domain.models

data class SafetyStatus(
    val alarm: String? = null,
    val limits: Map<String, Boolean> = emptyMap(),
    val isSafe: Boolean = true
)
