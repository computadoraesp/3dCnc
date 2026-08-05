package com.example.cnc3d.core.api.fluidnc

import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.core.network.HttpClientProvider
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import javax.inject.Inject

class FluidncApiService @Inject constructor(
    private val connectionManager: ConnectionManager
) {

    private val client: HttpClient get() = HttpClientProvider.create(connectionManager.baseUrl ?: "http://localhost")

    suspend fun getStatus(): String {
        return client.get("/status").bodyAsText()
    }

    suspend fun sendCommand(cmd: String): String {
        return client.post("/command") {
            contentType(ContentType.Text.Plain)
            setBody(cmd)
        }.bodyAsText()
    }

    suspend fun uploadFile(name: String, bytes: ByteArray): Boolean {
        val response = client.post("/upload") {
            contentType(ContentType.Application.OctetStream)
            header("X-Filename", name)
            setBody(bytes)
        }
        return response.status.isSuccess()
    }
}
