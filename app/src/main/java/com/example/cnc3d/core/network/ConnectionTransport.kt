package com.example.cnc3d.core.network

import kotlinx.coroutines.flow.Flow

interface ConnectionTransport {
    val type: ConnectionType
    val messages: Flow<String>
    val isConnected: Flow<Boolean>

    suspend fun connect(): Boolean
    suspend fun disconnect()
    suspend fun send(message: String): Boolean
}
