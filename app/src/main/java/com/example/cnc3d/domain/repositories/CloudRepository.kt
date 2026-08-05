package com.example.cnc3d.domain.repositories

interface CloudRepository {
    suspend fun upload(path: String, bytes: ByteArray): Boolean
    suspend fun download(path: String): ByteArray?
    suspend fun list(path: String): List<String>
}
