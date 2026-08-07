package com.example.cnc3d.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.usecases.ListFilesUseCase
import com.example.cnc3d.domain.usecases.UploadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CncFileViewModel @Inject constructor(
    private val listFilesUseCase: ListFilesUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _availableFiles = MutableStateFlow<List<String>>(emptyList())
    val availableFiles: StateFlow<List<String>> = _availableFiles

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    init {
        loadFiles()
    }

    fun loadFiles() {
        viewModelScope.launch {
            try {
                _availableFiles.value = listFilesUseCase()
            } catch (e: Exception) {
                _availableFiles.value = emptyList()
            }
        }
    }

    fun uploadFile(uri: Uri) {
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

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
    }
}
