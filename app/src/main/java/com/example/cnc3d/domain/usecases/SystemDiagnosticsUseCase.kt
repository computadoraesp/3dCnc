package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.models.SafetyStatus
import kotlinx.coroutines.delay
import kotlin.random.Random

data class DiagnosticsData(
    val cpuLoad: Int,
    val ramUsage: Int,
    val networkLatency: Int,
    val diskIO: String,
    val temperature: String
)

class SystemDiagnosticsUseCase {
    suspend fun getDiagnostics(): DiagnosticsData {
        // Mocking real-time system diagnostics
        return DiagnosticsData(
            cpuLoad = Random.nextInt(10, 80),
            ramUsage = Random.nextInt(40, 90),
            networkLatency = Random.nextInt(5, 50),
            diskIO = if (Random.nextBoolean()) "Normal" else "Busy",
            temperature = if (Random.nextBoolean()) "Stable" else "Nominal"
        )
    }
}
