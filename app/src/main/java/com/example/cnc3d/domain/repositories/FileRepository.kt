package com.example.cnc3d.domain.repositories



interface FileRepository {
    suspend fun upload(name: String, bytes: ByteArray): Boolean
    suspend fun listFiles(): List<String>
    suspend fun delete(name: String): Boolean
}
