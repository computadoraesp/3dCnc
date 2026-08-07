package com.example.cnc3d.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.models.PrinterStatus
import com.example.cnc3d.domain.usecases.CancelJobUseCase
import com.example.cnc3d.domain.usecases.GetStatusUseCase
import com.example.cnc3d.domain.usecases.ListFilesUseCase
import com.example.cnc3d.domain.usecases.PauseJobUseCase
import com.example.cnc3d.domain.usecases.ResumeJobUseCase
import com.example.cnc3d.domain.usecases.SendCommandUseCase
import com.example.cnc3d.domain.usecases.StartJobUseCase
import com.example.cnc3d.domain.usecases.UploadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val uploadFileUseCase: UploadFileUseCase,
    @ApplicationContext private val context: Context
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

    private val _uiMessage = kotlinx.coroutines.flow.MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

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

    fun uploadFile(uri: android.net.Uri) {
        viewModelScope.launch {
            val fileName = getFileName(uri) ?: "upload.gcode"
            if (!fileName.endsWith(".gcode", true) && !fileName.endsWith(
                    ".nc",
                    true
                ) && !fileName.endsWith(".gc", true)
            ) {
                _uiMessage.emit("Invalid file format. Please select .gcode or .nc")
                return@launch
            }

            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    val success = uploadFileUseCase(fileName, bytes)
                    if (success) {
                        _uiMessage.emit("File uploaded successfully: $fileName")
                        loadFiles()
                    } else {
                        _uiMessage.emit("Upload failed")
                    }
                }
            } catch (e: Exception) {
                _uiMessage.emit("Error: ${e.message}")
            }
        }
    }

    private fun getFileName(uri: android.net.Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
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
