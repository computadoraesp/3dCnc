package com.example.cnc3d.core.drivers

import com.example.cnc3d.core.api.moonraker.MoonrakerApiService
import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.domain.models.PrinterStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*

class MoonrakerDriver(
    private val api: MoonrakerApiService,
    private val connectionManager: ConnectionManager,
    private val scope: CoroutineScope
) {
    private val _status = MutableStateFlow(
        PrinterStatus(
            state = "Disconnected",
            temperatureHotend = 0f,
            temperatureBed = 0f,
            progress = 0f
        )
    )
    val status: StateFlow<PrinterStatus> = _status

    init {
        observeWebSocket()
    }

    private fun observeWebSocket() {
        connectionManager.messages()
            ?.conflate()
            ?.onEach { message ->
                parseNotification(message)
            }
            ?.launchIn(scope)
    }

    private fun parseNotification(message: String) {
        try {
            val json = Json.parseToJsonElement(message).jsonObject
            val method = json["method"]?.jsonPrimitive?.content

            if (method == "notify_status_update") {
                val params = json["params"]?.jsonArray
                val update = params?.get(0)?.jsonObject ?: return
                
                var hotend = _status.value.temperatureHotend
                var bed = _status.value.temperatureBed
                var progress = _status.value.progress
                var state = _status.value.state
                var posX = _status.value.position.first
                var posY = _status.value.position.second
                var posZ = _status.value.position.third
                var targetH = _status.value.targetHotend
                var targetB = _status.value.targetBed
                val sensors = _status.value.sensors.toMutableMap()

                if (update.containsKey("extruder")) {
                    val extruder = update["extruder"]?.jsonObject
                    hotend = extruder?.get("temperature")?.jsonPrimitive?.float ?: hotend
                    targetH = extruder?.get("target")?.jsonPrimitive?.float ?: targetH
                }
                if (update.containsKey("heater_bed")) {
                    val heaterBed = update["heater_bed"]?.jsonObject
                    bed = heaterBed?.get("temperature")?.jsonPrimitive?.float ?: bed
                    targetB = heaterBed?.get("target")?.jsonPrimitive?.float ?: targetB
                }
                if (update.containsKey("toolhead")) {
                    val toolhead = update["toolhead"]?.jsonObject
                    val pos = toolhead?.get("position")?.jsonArray
                    if (pos != null && pos.size >= 3) {
                        posX = pos[0].jsonPrimitive.float
                        posY = pos[1].jsonPrimitive.float
                        posZ = pos[2].jsonPrimitive.float
                    }
                }
                if (update.containsKey("virtual_sdcard")) {
                    progress = (update["virtual_sdcard"]?.jsonObject?.get("progress")?.jsonPrimitive?.float ?: (progress / 100f)) * 100f
                }
                if (update.containsKey("print_stats")) {
                    state = update["print_stats"]?.jsonObject?.get("state")?.jsonPrimitive?.content ?: state
                }

                _status.value = PrinterStatus(
                    state = state,
                    temperatureHotend = hotend,
                    temperatureBed = bed,
                    progress = progress,
                    position = Triple(posX, posY, posZ),
                    sensors = sensors,
                    targetHotend = targetH,
                    targetBed = targetB
                )
            }
        } catch (e: Exception) {
            // Ignore malformed messages
        }
    }

    suspend fun refreshStatus() {
        val raw = api.queryObjects()
        // Here we could parse and update _status, similar to parseNotification
        // but typically WebSocket covers real-time updates after initial state sync.
    }

    suspend fun pause() = api.pausePrint()
    suspend fun resume() = api.resumePrint()
    suspend fun cancel() = api.cancelPrint()
}
