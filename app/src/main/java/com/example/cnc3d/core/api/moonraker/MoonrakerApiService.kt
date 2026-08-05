package com.example.cnc3d.core.api.moonraker

import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.core.network.HttpClientProvider
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class MoonrakerApiService @Inject constructor(
    private val connectionManager: ConnectionManager
) {

    private val client: HttpClient get() = HttpClientProvider.create(connectionManager.baseUrl ?: "http://localhost")

    suspend fun getPrinterInfo(): String {
        return client.get("/printer/info").bodyAsText()
    }

    suspend fun queryObjects(): String {
        return client.get("/printer/objects/query?toolhead&extruder&heater_bed&virtual_sdcard&print_stats").bodyAsText()
    }

    suspend fun pausePrint(): Boolean {
        val response = client.post("/printer/print/pause")
        return response.status.isSuccess()
    }

    suspend fun resumePrint(): Boolean {
        val response = client.post("/printer/print/resume")
        return response.status.isSuccess()
    }

    suspend fun cancelPrint(): Boolean {
        val response = client.post("/printer/print/cancel")
        return response.status.isSuccess()
    }

    suspend fun startPrint(file: String): Boolean {
        val response = client.post("/printer/print/start") {
            contentType(ContentType.Application.Json)
            setBody("""{"filename":"$file"}""")
        }
        return response.status.isSuccess()
    }

    suspend fun uploadFile(name: String, bytes: ByteArray): Boolean {
        val response = client.post("/server/files/upload") {
            contentType(ContentType.Application.OctetStream)
            header("X-Filename", name)
            setBody(bytes)
        }
        return response.status.isSuccess()
    }

    suspend fun sendCommand(endpoint: String, script: String): String {
        val path = endpoint.replace(".", "/")
        return client.post("/$path") {
            contentType(ContentType.Application.Json)
            setBody("""{"script":"$script"}""")
        }.bodyAsText()
    }

    suspend fun sendCommand(cmd: String): String {
        val path = cmd.replace(".", "/")
        return client.post("/$path").bodyAsText()
    }

    suspend fun listFiles(): String {
        return client.get("/server/files/list").bodyAsText()
    }

    suspend fun deleteFile(name: String): Boolean {
        val response = client.post("/server/files/delete") {
            contentType(ContentType.Application.Json)
            setBody("""{"filename":"$name"}""")
        }
        return response.status.isSuccess()
    }

    suspend fun getBedMesh(): JsonObject {
        val raw = client.get("/printer/objects/query").bodyAsText()
        return Json.parseToJsonElement(raw).jsonObject
    }

    suspend fun getWebcams(): JsonObject {
        val raw = client.get("/config").bodyAsText()
        return Json.parseToJsonElement(raw).jsonObject
    }
}
