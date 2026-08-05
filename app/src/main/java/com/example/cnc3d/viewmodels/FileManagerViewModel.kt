package com.example.cnc3d.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.repositories.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FileManagerViewModel(
    private val repo: FileRepository
) : ViewModel() {

    private val _files = MutableStateFlow<List<String>>(emptyList())
    val files: StateFlow<List<String>> = _files

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    fun refresh() {
        viewModelScope.launch {
            _files.value = repo.listFiles()
        }
    }

    fun delete(name: String) {
        viewModelScope.launch {
            val ok = repo.delete(name)
            _status.value = if (ok) "Deleted" else "Error"
            refresh()
        }
    }
}
