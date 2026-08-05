package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.models.ToolData
import com.example.cnc3d.domain.models.cnc.CncStatus
import com.example.cnc3d.domain.usecases.CancelJobUseCase
import com.example.cnc3d.domain.usecases.GetStatusUseCase
import com.example.cnc3d.domain.usecases.StartJobUseCase
import com.example.cnc3d.domain.usecases.SendCommandUseCase
import com.example.cnc3d.domain.usecases.ListFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CncViewModel @Inject constructor(
    private val getStatusUseCase: GetStatusUseCase,
    private val cancelJobUseCase: CancelJobUseCase,
    private val startJobUseCase: StartJobUseCase,
    private val sendCommandUseCase: SendCommandUseCase,
    private val listFilesUseCase: ListFilesUseCase,
) : ViewModel() {

    private val _status = MutableStateFlow(
        CncStatus("Disconnected", Triple(0f, 0f, 0f), 0, 0)
    )
    val status: StateFlow<CncStatus> = _status

    private val _gcodePath = MutableStateFlow<GcodePath?>(null)
    val gcodePath: StateFlow<GcodePath?> = _gcodePath

    private val _mesh = MutableStateFlow<Mesh?>(null)
    val mesh: StateFlow<Mesh?> = _mesh

    private val _toolLibrary = MutableStateFlow(listOf(
        ToolData(1, "End Mill 6mm", 32.5f, 6.0f),
        ToolData(2, "Ball Mill 4mm", 45.0f, 4.0f)
    ))
    val toolLibrary: StateFlow<List<ToolData>> = _toolLibrary

    private val _availableFiles = MutableStateFlow<List<String>>(emptyList())
    val availableFiles: StateFlow<List<String>> = _availableFiles

    private val _selectedFile = MutableStateFlow<String?>(null)
    val selectedFile: StateFlow<String?> = _selectedFile

    fun refresh() {
        viewModelScope.launch {
            val result = getStatusUseCase()
            (result as? CncStatus)?.let {
                _status.value = it
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
            // In FluidNC, this would be a real reset or hard stop
            cancelJobUseCase()
        }
    }

    fun reset() {
        viewModelScope.launch {
            // Soft reset command for FluidNC
            sendMdi("\$Bye") 
            cancelJobUseCase()
        }
    }

    fun jog(axis: String, distance: Float, feed: Int = 3000) {
        viewModelScope.launch {
            val cmd = "\$J=G91 ${axis.uppercase()}$distance F$feed"
            sendMdi(cmd)
        }
    }

    fun sendMdi(command: String) {
        viewModelScope.launch {
            // Logic to send command to repo
            sendCommandUseCase(command)
            val current = _status.value
            _status.value = current.copy(mdiHistory = (current.mdiHistory + command).takeLast(100))
        }
    }

    fun zeroAxis(axis: String) {
        sendMdi("G10 L20 P1 ${axis.uppercase()}0")
    }

    fun goToZero() {
        sendMdi("G0 X0 Y0 Z0")
    }

    fun setWorkOffset(offset: String) {
        sendMdi(offset.uppercase())
        _status.value = _status.value.copy(activeOffset = offset.uppercase())
    }

    fun selectFile(name: String) {
        _selectedFile.value = name
    }

    fun addTool(name: String, length: Float, diameter: Float) {
        val nextId = (_toolLibrary.value.maxOfOrNull { it.id } ?: 0) + 1
        _toolLibrary.value += ToolData(nextId, name, length, diameter)
    }

    fun deleteTool(id: Int) {
        _toolLibrary.value = _toolLibrary.value.filter { it.id != id }
    }

    fun saveOffsets() {
        sendMdi("G10 L2") // Generic save offsets command for some controllers
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
