package com.example.cnc3d.core.network

import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.math.pow

class ReconnectManager(
    private val scope: CoroutineScope,
    private val onReconnect: suspend () -> Boolean,
    private val baseDelayMs: Long = 1000,
    private val maxDelayMs: Long = 30000
) {
    private var reconnectJob: Job? = null
    private var attempts = 0

    fun startReconnecting() {
        if (reconnectJob?.isActive == true) return

        reconnectJob = scope.launch {
            while (isActive) {
                val delayTime = min(maxDelayMs, baseDelayMs * 2.0.pow(attempts).toLong())
                delay(delayTime)
                
                attempts++
                if (onReconnect()) {
                    reset()
                    break
                }
            }
        }
    }

    fun reset() {
        reconnectJob?.cancel()
        attempts = 0
    }
}
