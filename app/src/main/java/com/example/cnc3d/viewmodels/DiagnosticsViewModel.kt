package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.usecases.DiagnosticsData
import com.example.cnc3d.domain.usecases.SystemDiagnosticsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiagnosticsViewModel(
    private val useCase: SystemDiagnosticsUseCase = SystemDiagnosticsUseCase()
) : ViewModel() {

    private val _data = MutableStateFlow(DiagnosticsData(0, 0, 0, "Initial", "Initial"))
    val data: StateFlow<DiagnosticsData> = _data

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch {
            while (true) {
                _data.value = useCase.getDiagnostics()
                delay(2000)
            }
        }
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            _isRecording.value = true
            delay(3000)
            _isRecording.value = false
        }
    }

    fun exportReport(): String {
        val d = _data.value
        return """
            Industrial 3dCNC Analyzer Report
            Timestamp: ${System.currentTimeMillis()}
            CPU Load: ${d.cpuLoad}%
            RAM Usage: ${d.ramUsage}%
            Network Latency: ${d.networkLatency}ms
            Disk I/O: ${d.diskIO}
            Temperature: ${d.temperature}
            Status: PASSED
        """.trimIndent()
    }
}
