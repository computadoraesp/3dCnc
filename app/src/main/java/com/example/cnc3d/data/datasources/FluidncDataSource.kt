package com.example.cnc3d.data.datasources

import com.example.cnc3d.core.api.fluidnc.FluidncApiService
import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.domain.models.CameraInfo
import com.example.cnc3d.domain.models.MeshPoint
import com.example.cnc3d.domain.models.cnc.CncStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class FluidncDataSource @Inject constructor(
    private val api: FluidncApiService,
    private val connectionManager: ConnectionManager
) {

    val ip: String get() = connectionManager.baseUrl ?: ""

    suspend fun getStatus(): CncStatus {
        val raw = api.getStatus()
        return parseStatus(raw)
    }

    private fun parseStatus(message: String): CncStatus {
        try {
            val json = Json.parseToJsonElement(message).jsonObject
            if (json.containsKey("status")) {
                val statusObj = json["status"]?.jsonObject
                val state = statusObj?.get("state")?.jsonPrimitive?.content ?: "Unknown"

                val posArr = statusObj?.get("pos")?.jsonArray
                val x = posArr?.get(0)?.jsonPrimitive?.float ?: 0f
                val y = posArr?.get(1)?.jsonPrimitive?.float ?: 0f
                val z = posArr?.get(2)?.jsonPrimitive?.float ?: 0f

                val fsArr = statusObj?.get("fs")?.jsonArray
                val f = fsArr?.get(0)?.jsonPrimitive?.int ?: 0
                val s = fsArr?.get(1)?.jsonPrimitive?.int ?: 0

                val sensors = mutableMapOf<String, Boolean>()
                statusObj?.get("pins")?.jsonPrimitive?.content?.let { pins ->
                    sensors["x_min"] = pins.contains("X")
                    sensors["y_min"] = pins.contains("Y")
                    sensors["z_min"] = pins.contains("Z")
                    sensors["probe"] = pins.contains("P")
                    sensors["e_stop"] = pins.contains("E")
                    sensors["door"] = pins.contains("D")
                }

                return CncStatus(state, Triple(x, y, z), s, f, sensors = sensors)
            }
        } catch (e: Exception) {
            if (message.startsWith("<") && message.endsWith(">")) {
                return parseGrblStatus(message)
            }
        }
        return CncStatus("Error", Triple(0f, 0f, 0f), 0, 0)
    }

    private fun parseGrblStatus(message: String): CncStatus {
        val parts = message.substring(1, message.length - 1).split("|")
        val state = parts[0]

        var x = 0f
        var y = 0f
        var z = 0f
        var f = 0
        var s = 0
        val sensors = mutableMapOf<String, Boolean>()

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
                part.startsWith("Pn:") -> {
                    val pins = part.substringAfter(":")
                    sensors["x_min"] = pins.contains("X")
                    sensors["y_min"] = pins.contains("Y")
                    sensors["z_min"] = pins.contains("Z")
                    sensors["x_max"] = pins.contains("x")
                    sensors["y_max"] = pins.contains("y")
                    sensors["z_max"] = pins.contains("z")
                    sensors["probe"] = pins.contains("P")
                    sensors["e_stop"] = pins.contains("E")
                    sensors["door"] = pins.contains("D")
                }
            }
        }
        return CncStatus(state, Triple(x, y, z), s, f, sensors = sensors)
    }

    suspend fun sendCommand(cmd: String): String {
        return api.sendCommand(cmd)
    }

    suspend fun uploadFile(name: String, bytes: ByteArray): Boolean {
        return api.uploadFile(name, bytes)
    }

    suspend fun listFiles(): List<String> {
        val response = api.sendCommand("\$dir")
        return response
            .lines()
            .filter { it.endsWith(".gcode") || it.endsWith(".nc") }
    }

    suspend fun delete(name: String): Boolean {
        val response = api.sendCommand("\$delete $name")
        return response.contains("ok", ignoreCase = true)
    }

    suspend fun probe(): MeshPoint? {
        val raw = sendCommand("G38.2 Z-10 F100")
        if (!raw.contains("probe", ignoreCase = true)) return null

        val x = raw.substringAfter("X:").substringBefore(" ").toFloatOrNull() ?: return null
        val y = raw.substringAfter("Y:").substringBefore(" ").toFloatOrNull() ?: return null
        val z = raw.substringAfter("Z:").substringBefore(" ").toFloatOrNull() ?: return null

        return MeshPoint(x, y, z)
    }

    fun getIpCamera(ip: String): CameraInfo {
        return CameraInfo(
            name = "IP Camera",
            streamUrl = "http://$ip:8080/stream",
            snapshotUrl = "http://$ip:8080/snapshot.jpg"
        )
    }
}
