package com.example.cnc3d.domain.repositories

interface MachineControlRepository {

    // CNC
    suspend fun jog(axis: String, amount: Float): Boolean
    suspend fun home(): Boolean
    suspend fun spindle(on: Boolean, rpm: Int = 0): Boolean
    suspend fun feedOverride(percent: Int): Boolean

    // Printer
    suspend fun extrude(amount: Float, speed: Int): Boolean
    suspend fun setHotendTemp(temp: Int): Boolean
    suspend fun setBedTemp(temp: Int): Boolean
    suspend fun moveAxis(axis: String, amount: Float): Boolean
    suspend fun homePrinter(): Boolean
}
