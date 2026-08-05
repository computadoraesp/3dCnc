package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.SlicerProfile
import com.example.cnc3d.domain.repositories.SlicerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SlicerViewModel(
    private val repo: SlicerRepository
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<SlicerProfile>>(emptyList())
    val profiles: StateFlow<List<SlicerProfile>> = _profiles

    private val _gcode = MutableStateFlow<ByteArray?>(null)
    val gcode: StateFlow<ByteArray?> = _gcode

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    fun loadProfiles() {
        viewModelScope.launch {
            _profiles.value = repo.getAll()
        }
    }

    fun slice(stl: ByteArray, profile: SlicerProfile) {
        viewModelScope.launch {
            _status.value = "Slicing..."
            val result = repo.slice(stl, profile)
            _gcode.value = result
            _status.value = if (result != null) "OK" else "ERROR"
        }
    }

    fun clearResult() {
        _gcode.value = null
        _status.value = ""
    }
}
