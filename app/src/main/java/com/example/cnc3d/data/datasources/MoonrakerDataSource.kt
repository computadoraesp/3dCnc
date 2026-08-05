package com.example.cnc3d.data.datasources

import com.example.cnc3d.core.api.moonraker.MoonrakerApiService
import com.example.cnc3d.domain.models.CameraInfo
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.models.MeshPoint
import com.example.cnc3d.domain.models.PrinterStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class MoonrakerDataSource @Inject constructor(
    private val api: MoonrakerApiService
) {

    suspend fun getStatus(): PrinterStatus {
        val raw = api.queryObjects()
        return parseStatus(raw)
    }

    private fun parseStatus(jsonString: String): PrinterStatus {
        try {
            val json = Json.parseToJsonElement(jsonString).jsonObject
            val result = json["result"]?.jsonObject
            val status = result?.get("status")?.jsonObject

            val extruder = status?.get("extruder")?.jsonObject
            val hotendTemp = extruder?.get("temperature")?.jsonPrimitive?.float ?: 0f

            val bed = status?.get("heater_bed")?.jsonObject
            val bedTemp = bed?.get("temperature")?.jsonPrimitive?.float ?: 0f

            val stats = status?.get("print_stats")?.jsonObject
            val state = stats?.get("state")?.jsonPrimitive?.content ?: "UNKNOWN"

            val virtualSd = status?.get("virtual_sdcard")?.jsonObject
            val progress = virtualSd?.get("progress")?.jsonPrimitive?.float ?: 0f

            return PrinterStatus(
                state = state,
                temperatureHotend = hotendTemp,
                temperatureBed = bedTemp,
                progress = progress * 100f
            )
        } catch (e: Exception) {
            return PrinterStatus("ERROR", 0f, 0f, 0f)
        }
    }

    suspend fun pause(): Boolean = api.pausePrint()
    suspend fun resume(): Boolean = api.resumePrint()
    suspend fun cancel(): Boolean = api.cancelPrint()

    suspend fun startPrint(file: String): Boolean {
        return api.startPrint(file)
    }

    suspend fun uploadFile(name: String, bytes: ByteArray): Boolean {
        return api.uploadFile(name, bytes)
    }

    suspend fun sendCommand(endpoint: String, script: String): String {
        return api.sendCommand(endpoint, script)
    }

    suspend fun sendCommand(cmd: String): String {
        return api.sendCommand(cmd)
    }

    suspend fun listFiles(): List<String> {
        val raw = api.listFiles()
        return raw
            .lines()
            .filter { it.contains("filename") }
            .map { it.substringAfter(":").trim() }
    }

    suspend fun delete(name: String): Boolean {
        return api.deleteFile(name)
    }

    suspend fun getBedMesh(): Mesh {
        val json = api.getBedMesh()

        val points = mutableListOf<MeshPoint>()

        val matrix = json["mesh_matrix"]?.jsonArray
        val xs = json["mesh_x"]?.jsonArray?.map { it.jsonPrimitive.float } ?: emptyList()
        val ys = json["mesh_y"]?.jsonArray?.map { it.jsonPrimitive.float } ?: emptyList()

        for (i in ys.indices) {
            for (j in xs.indices) {
                val z = matrix?.get(i)?.jsonArray?.get(j)?.jsonPrimitive?.float ?: 0f
                points.add(MeshPoint(xs[j], ys[i], z))
            }
        }

        return Mesh(points)
    }

    suspend fun getCameras(): List<CameraInfo> {
        val json = api.getWebcams()

        val cams = mutableListOf<CameraInfo>()

        val arr = json["webcams"]?.jsonArray
        if (arr != null) {
            for (item in arr) {
                val name = item.jsonObject["name"]!!.jsonPrimitive.content
                val stream = item.jsonObject["stream_url"]!!.jsonPrimitive.content
                val snap = item.jsonObject["snapshot_url"]!!.jsonPrimitive.content

                cams.add(CameraInfo(name, stream, snap))
            }
        }

        return cams
    }
}
