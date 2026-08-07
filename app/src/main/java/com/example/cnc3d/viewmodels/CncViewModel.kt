package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.cnc.CncStatus
import com.example.cnc3d.domain.usecases.CancelJobUseCase
import com.example.cnc3d.domain.usecases.ObserveMachineStatusUseCase
import com.example.cnc3d.domain.usecases.SendCommandUseCase
import com.example.cnc3d.domain.usecases.StartJobUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CncViewModel @Inject constructor(
    private val observeMachineStatusUseCase: ObserveMachineStatusUseCase,
    private val cancelJobUseCase: CancelJobUseCase,
    private val startJobUseCase: StartJobUseCase,
    private val sendCommandUseCase: SendCommandUseCase,
) : ViewModel() {

    private val _status = MutableStateFlow(CncStatus.DISCONNECTED)
    val status: StateFlow<CncStatus> = _status

    private val _gcodePath = MutableStateFlow<GcodePath?>(null)
    val gcodePath: StateFlow<GcodePath?> = _gcodePath

    private val _selectedFile = MutableStateFlow<String?>(null)
    val selectedFile: StateFlow<String?> = _selectedFile

    init {
        viewModelScope.launch {
            observeMachineStatusUseCase().collect { result ->
                if (result is CncStatus) {
                    _status.value = result
                }
            }
        }
    }

    fun start(file: String) {
        viewModelScope.launch {
            startJobUseCase(file)
        }
    }

    fun emergencyStop() {
        viewModelScope.launch {
            cancelJobUseCase()
        }
    }

    fun reset() {
        viewModelScope.launch {
            sendCommandUseCase("\$Bye")
            cancelJobUseCase()
        }
    }

    fun jog(axis: String, distance: Float, feed: Int = 3000) {
        viewModelScope.launch {
            val cmd = "\$J=G91 ${axis.uppercase()}$distance F$feed"
            sendCommandUseCase(cmd)
        }
    }

    fun sendMdi(command: String) {
        viewModelScope.launch {
            sendCommandUseCase(command)
            val current = _status.value
            _status.value = current.copy(mdiHistory = (current.mdiHistory + command).takeLast(100))
        }
    }

    fun selectFile(name: String) {
        _selectedFile.value = name
    }
}
