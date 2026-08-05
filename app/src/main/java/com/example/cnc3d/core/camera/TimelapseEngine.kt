package com.example.cnc3d.core.camera

import com.example.cnc3d.viewmodels.CameraViewModel
import kotlinx.coroutines.delay

class TimelapseEngine(
    private val vm: CameraViewModel,
    private val url: String
) {

    private var running = false

    suspend fun start(intervalMs: Long, onFrame: (ByteArray) -> Unit) {
        running = true
        while (running) {
            vm.takeSnapshot(url)
            val frame = vm.snapshot.value
            if (frame != null) onFrame(frame)
            delay(intervalMs)
        }
    }

    fun stop() {
        running = false
    }
}
