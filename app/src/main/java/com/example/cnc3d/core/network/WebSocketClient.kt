package com.example.cnc3d.core.network

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

class WebSocketClient(
    private val client: HttpClient,
    private val scope: CoroutineScope
) {

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages

    private var session: WebSocketSession? = null

    suspend fun connect(url: String) {
        session = client.webSocketSession(url)

        scope.launch {
            while (isActive) {
                val frame = session?.incoming?.receive()
                if (frame is Frame.Text) {
                    _messages.emit(frame.readText())
                }
            }
        }
    }

    suspend fun send(text: String) {
        session?.send(Frame.Text(text))
    }

    suspend fun disconnect() {
        session?.close()
        session = null
    }
}
