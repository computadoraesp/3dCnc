package com.example.cnc3d.core.drivers

import com.example.cnc3d.core.api.fluidnc.FluidncApiService
import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.domain.models.cnc.CncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*

class FluidNCDriver(
    private val api: FluidncApiService,
    private val connectionManager: ConnectionManager,
    private val scope: CoroutineScope
) {
    private val _status = MutableStateFlow(
        CncStatus(
            state = "Disconnected",
            position = Triple(0f, 0f, 0f),
            spindleRpm = 0,
            feedRate = 0
        )
    )
    val status: StateFlow<CncStatus> = _status

    init {
        observeWebSocket()
    }

    private fun observeWebSocket() {
        connectionManager.messages()
            ?.conflate()
            ?.onEach { message ->
                parseMessage(message)
            }
            ?.launchIn(scope)
    }

    private fun parseMessage(message: String) {
        try {
            val json = Json.parseToJsonElement(message).jsonObject
            
            // FluidNC status messages typically look like <Idle|WPos:0.000,0.000,0.000|Bf:15,128|FS:0,0>
            // But if it's JSON:
            if (json.containsKey("status")) {
                val statusObj = json["status"]?.jsonObject
                val state = statusObj?.get("state")?.jsonPrimitive?.content ?: _status.value.state
                
                val posArr = statusObj?.get("pos")?.jsonArray
                val x = posArr?.get(0)?.jsonPrimitive?.float ?: _status.value.position.first
                val y = posArr?.get(1)?.jsonPrimitive?.float ?: _status.value.position.second
                val z = posArr?.get(2)?.jsonPrimitive?.float ?: _status.value.position.third

                val fsArr = statusObj?.get("fs")?.jsonArray
                val f = fsArr?.get(0)?.jsonPrimitive?.int ?: _status.value.feedRate
                val s = fsArr?.get(1)?.jsonPrimitive?.int ?: _status.value.spindleRpm

                val ovArr = statusObj?.get("ov")?.jsonArray
                val feedOv = ovArr?.get(0)?.jsonPrimitive?.int ?: _status.value.overrides.first
                val spindleOv = ovArr?.get(1)?.jsonPrimitive?.int ?: _status.value.overrides.second
                val rapidOv = ovArr?.get(2)?.jsonPrimitive?.int ?: _status.value.overrides.third

                val sensors = _status.value.sensors.toMutableMap()
                statusObj?.get("pins")?.jsonPrimitive?.content?.split(",")?.forEach { pin ->
                    // Example: "Pn:P" -> Probe active
                    if (pin.contains("P")) sensors["probe"] = true
                    if (pin.contains("X")) sensors["limits"] = true
                }

                _status.value = _status.value.copy(
                    state = state,
                    position = Triple(x, y, z),
                    spindleRpm = s,
                    feedRate = f,
                    overrides = Triple(feedOv, spindleOv, rapidOv),
                    sensors = sensors
                )
            }
        } catch (e: Exception) {
            // If not JSON, it might be raw GRBL string
            if (message.startsWith("<") && message.endsWith(">")) {
                parseGrblStatus(message)
            }
        }
    }

    private fun parseGrblStatus(message: String) {
        val parts = message.substring(1, message.length - 1).split("|")
        val state = parts[0]
        
        var x = _status.value.position.first
        var y = _status.value.position.second
        var z = _status.value.position.third
        var f = _status.value.feedRate
        var s = _status.value.spindleRpm

        parts.drop(1).forEach { part ->
            when {
                part.startsWith("WPos:") || part.startsWith("MPos:") -> {
                    val coords = part.substringAfter(":").split(",")
                    x = coords[0].toFloatOrNull() ?: x
                    y = coords[1].toFloatOrNull() ?: y
                    z = coords[2].toFloatOrNull() ?: z
                }
                part.startsWith("FS:") -> {
                    val rates = part.substringAfter(":").split(",")
                    f = rates[0].toIntOrNull() ?: f
                    s = rates[1].toIntOrNull() ?: s
                }
            }
        }

        _status.value = CncStatus(state, Triple(x, y, z), s, f)
    }

    suspend fun refreshStatus() {
        val raw = api.getStatus()
        parseMessage(raw)
    }

    suspend fun sendCommand(cmd: String) {
        if (!connectionManager.send(cmd)) {
            api.sendCommand(cmd)
        }
    }
}
