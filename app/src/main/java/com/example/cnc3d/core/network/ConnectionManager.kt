package com.example.cnc3d.core.network

import io.ktor.client.*
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ConnectionState {
    Connected, Connecting, Disconnected, Error
}

class ConnectionManager(
    private val scope: CoroutineScope
) {

    private val _state = MutableStateFlow(ConnectionState.Disconnected)
    val state: StateFlow<ConnectionState> = _state

    private val _isStale = MutableStateFlow(false)
    val isStale: StateFlow<Boolean> = _isStale

    private var activeTransport: ConnectionTransport? = null
    
    var httpClient: HttpClient? = null
        private set
    
    var baseUrl: String? = null
        private set

    private val heartbeat = HeartbeatManager(scope) {
        checkStatus()
    }

    private val reconnect = ReconnectManager(scope, {
        reconnect()
    })

    fun setTransport(transport: ConnectionTransport) {
        activeTransport = transport
        if (transport is NetworkTransport) {
            // If it's network, we might want to initialize HTTP separately if needed
            // but usually configure() handles it.
        }
    }

    fun configure(baseUrl: String) {
        this.baseUrl = baseUrl
        httpClient?.close()
        httpClient = HttpClientProvider.create(baseUrl)
    }

    suspend fun connect() {
        val transport = activeTransport ?: return
        _state.value = ConnectionState.Connecting

        try {
            val ok = transport.connect()
            if (ok) {
                _state.value = ConnectionState.Connected
                heartbeat.start()
                reconnect.reset()
            } else {
                _state.value = ConnectionState.Error
                reconnect.startReconnecting()
            }
        } catch (e: Exception) {
            _state.value = ConnectionState.Error
            reconnect.startReconnecting()
        }
    }

    private suspend fun checkStatus(): Boolean {
        if (activeTransport?.type != ConnectionType.WIFI) {
            // Heartbeat for Serial/BT is different, maybe just check transport isConnected
            return true 
        }
        return try {
            httpClient?.get("/")
            _isStale.value = false
            true
        } catch (e: Exception) {
            _isStale.value = true
            if (_state.value == ConnectionState.Connected) {
                _state.value = ConnectionState.Error
                reconnect.startReconnecting()
            }
            false
        }
    }

    private suspend fun reconnect(): Boolean {
        val transport = activeTransport ?: return false
        return try {
            transport.disconnect()
            val ok = transport.connect()
            if (ok) {
                _state.value = ConnectionState.Connected
                _isStale.value = false
                heartbeat.start()
            }
            ok
        } catch (e: Exception) {
            false
        }
    }

    suspend fun disconnect() {
        heartbeat.stop()
        reconnect.reset()
        _isStale.value = false
        activeTransport?.disconnect()
        _state.value = ConnectionState.Disconnected
    }

    suspend fun send(message: String): Boolean {
        return activeTransport?.send(message) ?: false
    }

    fun messages(): Flow<String>? = activeTransport?.messages
}
