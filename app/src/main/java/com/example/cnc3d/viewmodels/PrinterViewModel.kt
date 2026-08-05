package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.PrinterStatus
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterViewModel @Inject constructor(
    private val getStatusUseCase: GetStatusUseCase,
    private val pauseJobUseCase: PauseJobUseCase,
    private val resumeJobUseCase: ResumeJobUseCase,
    private val cancelJobUseCase: CancelJobUseCase,
    private val startJobUseCase: StartJobUseCase,
    private val sendCommandUseCase: SendCommandUseCase,
    private val listFilesUseCase: ListFilesUseCase,
) : ViewModel() {

    private val _status = MutableStateFlow(
        PrinterStatus("Disconnected", 0f, 0f, 0f)
    )
    val status: StateFlow<PrinterStatus> = _status

    private val _mdiHistory = MutableStateFlow<List<String>>(emptyList())
    val mdiHistory: StateFlow<List<String>> = _mdiHistory

    private val _zOffset = MutableStateFlow(0f)
    val zOffset: StateFlow<Float> = _zOffset

    private val _gcodePath = MutableStateFlow<GcodePath?>(null)
    val gcodePath: StateFlow<GcodePath?> = _gcodePath

    private val _mesh = MutableStateFlow<Mesh?>(null)
    val mesh: StateFlow<Mesh?> = _mesh

    private val _availableFiles = MutableStateFlow<List<String>>(emptyList())
    val availableFiles: StateFlow<List<String>> = _availableFiles

    private val _selectedFile = MutableStateFlow<String?>(null)
    val selectedFile: StateFlow<String?> = _selectedFile

    fun refresh() {
        viewModelScope.launch {
            val result = getStatusUseCase()
            (result as? PrinterStatus)?.let {
                _status.value = it
            }
        }
    }

    fun start(file: String) {
        viewModelScope.launch {
            startJobUseCase(file)
        }
    }

    fun pause() {
        viewModelScope.launch {
            pauseJobUseCase()
        }
    }

    fun resume() {
        viewModelScope.launch {
            resumeJobUseCase()
        }
    }

    fun cancel() {
        viewModelScope.launch {
            cancelJobUseCase()
        }
    }

    fun emergencyStop() {
        viewModelScope.launch {
            // In a real Klipper app, this would be M112
            cancelJobUseCase()
        }
    }

    fun sendMdi(command: String) {
        viewModelScope.launch {
            _mdiHistory.value = (_mdiHistory.value + command).takeLast(100)
            sendCommandUseCase(command)
        }
    }

    fun adjustZOffset(delta: Float) {
        _zOffset.value += delta
        sendMdi("SET_GCODE_OFFSET Z_ADJUST=$delta MOVE=1")
    }

    fun saveZOffset() {
        viewModelScope.launch {
            sendMdi("SAVE_CONFIG")
        }
    }

    fun jog(axis: String, distance: Float) {
        viewModelScope.launch {
            val cmd = when (axis.uppercase()) {
                "X" -> "G91\nG1 X$distance F3000\nG90"
                "Y" -> "G91\nG1 Y$distance F3000\nG90"
                "Z" -> "G91\nG1 Z$distance F600\nG90"
                else -> ""
            }
            if (cmd.isNotBlank()) sendMdi(cmd)
        }
    }

    fun probeTest() {
        sendMdi("PROBE")
    }

    fun setTargetTemp(heater: String, temp: Float) {
        val cmd = when (heater.lowercase()) {
            "extruder" -> "M104 S$temp"
            "heater_bed" -> "M140 S$temp"
            else -> ""
        }
        if (cmd.isNotBlank()) sendMdi(cmd)
    }

    fun extrude(amount: Float, speed: Float) {
        viewModelScope.launch {
            sendMdi("G91\nG1 E$amount F${speed * 60}\nG90")
        }
    }

    fun retract(amount: Float, speed: Float) {
        extrude(-amount, speed)
    }

    fun selectFile(name: String) {
        _selectedFile.value = name
    }

    fun loadFiles() {
        viewModelScope.launch {
            try {
                _availableFiles.value = listFilesUseCase()
            } catch (_: Exception) {
                _availableFiles.value = emptyList()
            }
        }
    }
}
