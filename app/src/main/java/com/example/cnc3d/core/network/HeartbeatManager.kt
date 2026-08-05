package com.example.cnc3d.core.network

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HeartbeatManager(
    private val scope: CoroutineScope,
    private val intervalMs: Long = 5000,
    private val onPing: suspend () -> Boolean
) {
    private val _isAlive = MutableStateFlow(true)
    val isAlive: StateFlow<Boolean> = _isAlive

    private var job: Job? = null

    fun start() {
        job?.cancel()
        job = scope.launch {
            while (isActive) {
                delay(intervalMs)
                val success = try {
                    onPing()
                } catch (e: Exception) {
                    false
                }
                _isAlive.value = success
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        _isAlive.value = true
    }
}
