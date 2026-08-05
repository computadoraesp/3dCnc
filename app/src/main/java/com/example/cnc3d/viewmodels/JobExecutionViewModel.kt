package com.example.cnc3d.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.repositories.JobExecutionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JobExecutionViewModel(
    private val repo: JobExecutionRepository
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _status = MutableStateFlow("Idle")
    val status: StateFlow<String> = _status

    fun start(fileName: String) {
        viewModelScope.launch {
            val ok = repo.startJob(fileName)
            _status.value = if (ok) "Running" else "Error"
        }
    }

    fun stop() {
        viewModelScope.launch {
            val ok = repo.stopJob()
            _status.value = if (ok) "Stopped" else "Error"
        }
    }

    fun refreshProgress() {
        viewModelScope.launch {
            _progress.value = repo.getProgress()
        }
    }
}
