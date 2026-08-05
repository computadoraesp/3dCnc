package com.example.cnc3d.core.detection

import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.core.network.HttpClientProvider
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import javax.inject.Inject

class FirmwareDetector @Inject constructor(
    private val connectionManager: ConnectionManager
) {

    private val client: HttpClient get() = HttpClientProvider.create(connectionManager.baseUrl ?: "http://localhost")

    suspend fun detect(): DetectionResult {
        return detectFluidnc()
            ?: detectMoonraker()
            ?: DetectionResult(FirmwareType.UNKNOWN, Capabilities())
    }

    private suspend fun detectFluidnc(): DetectionResult? {
        return try {
            val response = client.get("/status").bodyAsText()
            if (response.contains("state", ignoreCase = true)) {
                DetectionResult(
                    FirmwareType.FLUIDNC,
                    Capabilities(
                        axes = 3,
                        hasSpindle = true,
                        supportsGcodeStreaming = true,
                        supportsRealtimeEvents = true
                    )
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun detectMoonraker(): DetectionResult? {
        return try {
            val response = client.get("/printer/info").bodyAsText()
            if (response.contains("klipper_path", ignoreCase = true)) {
                DetectionResult(
                    FirmwareType.MOONRAKER,
                    Capabilities(
                        axes = 3,
                        hasExtruder = true,
                        hasHeatedBed = true,
                        supportsGcodeStreaming = true,
                        supportsRealtimeEvents = true
                    )
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
