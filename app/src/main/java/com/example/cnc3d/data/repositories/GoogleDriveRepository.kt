package com.example.cnc3d.data.repositories

import com.example.cnc3d.domain.repositories.CloudRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GoogleDriveRepository(
    private val token: String,
    private val http: HttpClient
) : CloudRepository {

    override suspend fun upload(path: String, bytes: ByteArray): Boolean {
        val url = "https://www.googleapis.com/upload/drive/v3/files?uploadType=media"
        val resp = http.post(url) {
            header("Authorization", "Bearer $token")
            setBody(bytes)
        }
        return resp.status.isSuccess()
    }

    override suspend fun download(path: String): ByteArray? {
        val url = "https://www.googleapis.com/drive/v3/files/$path?alt=media"
        return http.get(url) {
            header("Authorization", "Bearer $token")
        }.bodyAsBytes()
    }

    override suspend fun list(path: String): List<String> {
        val url = "https://www.googleapis.com/drive/v3/files"
        val raw = http.get(url) {
            header("Authorization", "Bearer $token")
        }.bodyAsText()
        val json = Json.parseToJsonElement(raw).jsonObject
        return json["files"]?.jsonArray?.map { it.jsonObject["id"]!!.jsonPrimitive.content } ?: emptyList()
    }
}
