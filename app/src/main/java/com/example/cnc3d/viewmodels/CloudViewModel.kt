package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.usecases.CloudSyncUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CloudViewModel(
    private val syncUseCase: CloudSyncUseCase
) : ViewModel() {

    private val _status = MutableStateFlow("Idle")
    val status: StateFlow<String> = _status

    private val _lastSync = MutableStateFlow("Never")
    val lastSync: StateFlow<String> = _lastSync

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount

    fun sync() {
        viewModelScope.launch {
            _status.value = "Syncing..."
            val ok = syncUseCase.syncAll()
            if (ok) {
                _status.value = "Success"
                _lastSync.value = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                _pendingCount.value = 0
            } else {
                _status.value = "Error"
            }
        }
    }

    fun uploadFiles() {
        viewModelScope.launch {
            _status.value = "Uploading..."
            kotlinx.coroutines.delay(1000)
            _status.value = "Upload Complete"
        }
    }

    fun downloadFiles() {
        viewModelScope.launch {
            _status.value = "Downloading..."
            kotlinx.coroutines.delay(1000)
            _status.value = "Download Complete"
        }
    }

    fun clearCache() {
        _status.value = "Cache Cleared"
        _pendingCount.value = 0
    }
}
