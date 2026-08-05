package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.CameraInfo
import com.example.cnc3d.domain.repositories.CameraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    private val repo: CameraRepository
) : ViewModel() {

    private val _cameras = MutableStateFlow<List<CameraInfo>>(emptyList())
    val cameras: StateFlow<List<CameraInfo>> = _cameras

    private val _snapshot = MutableStateFlow<ByteArray?>(null)
    val snapshot: StateFlow<ByteArray?> = _snapshot

    fun load() {
        viewModelScope.launch {
            _cameras.value = repo.getCameras()
        }
    }

    fun takeSnapshot(url: String) {
        viewModelScope.launch {
            _snapshot.value = repo.snapshot(url)
        }
    }
}
