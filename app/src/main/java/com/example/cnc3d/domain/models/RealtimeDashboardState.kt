package com.example.cnc3d.domain.models

import com.example.cnc3d.domain.models.cnc.CNCPosition


data class RealtimeDashboardState(
    val cncPosition: CNCPosition? = null,
    val printerTemps: PrinterTemps? = null,
    val jobProgress: Float = 0f,
    val machineState: String = "Idle"
)
