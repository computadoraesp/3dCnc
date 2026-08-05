package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.core.camera.TimelapseEngine
import com.example.cnc3d.domain.repositories.CameraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimelapseViewModel(
    private val cameraRepo: CameraRepository,
    private val cameraViewModel: CameraViewModel
) : ViewModel() {

    private var engine: TimelapseEngine? = null

    private val _frameCount = MutableStateFlow(0)
    val frameCount: StateFlow<Int> = _frameCount

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    fun start(url: String) {
        if (_isRecording.value) return
        
        _isRecording.value = true
        _frameCount.value = 0
        engine = TimelapseEngine(cameraViewModel, url)
        
        viewModelScope.launch {
            engine?.start(5000) { frame ->
                _frameCount.value++
                // logic to save frame to disk
            }
        }
    }

    fun stop() {
        engine?.stop()
        _isRecording.value = false
    }
}
