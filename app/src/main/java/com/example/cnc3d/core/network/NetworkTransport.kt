package com.example.cnc3d.core.network

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class NetworkTransport @Inject constructor(
    private val httpClient: HttpClient,
    private val scope: CoroutineScope,
    private val wsUrl: String
) : ConnectionTransport {

    override val type: ConnectionType = ConnectionType.WIFI
    
    private val wsClient = WebSocketClient(httpClient, scope)
    
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    override val messages: Flow<String> = wsClient.messages

    override suspend fun connect(): Boolean {
        return try {
            wsClient.connect(wsUrl)
            _isConnected.value = true
            true
        } catch (e: Exception) {
            _isConnected.value = false
            false
        }
    }

    override suspend fun disconnect() {
        wsClient.disconnect()
        _isConnected.value = false
    }

    override suspend fun send(message: String): Boolean {
        return try {
            wsClient.send(message)
            true
        } catch (e: Exception) {
            false
        }
    }
}
