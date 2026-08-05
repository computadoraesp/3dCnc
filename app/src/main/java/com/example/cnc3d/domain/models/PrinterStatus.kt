package com.example.cnc3d.domain.models

data class PrinterStatus(
    val state: String,
    val temperatureHotend: Float,
    val temperatureBed: Float,
    val progress: Float,
    val position: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val sensors: Map<String, Boolean> = emptyMap(),
    val targetHotend: Float = 0f,
    val targetBed: Float = 0f
)

