package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.ToolData
import com.example.cnc3d.domain.repositories.MachineProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CncToolViewModel @Inject constructor(
    private val profileRepo: MachineProfileRepository
) : ViewModel() {

    private val _toolLibrary = MutableStateFlow(
        listOf(
            ToolData(1, "End Mill 6mm", 32.5f, 6.0f),
            ToolData(2, "Ball Mill 4mm", 45.0f, 4.0f)
        )
    )
    val toolLibrary: StateFlow<List<ToolData>> = _toolLibrary

    private val _units = MutableStateFlow("mm")
    val units: StateFlow<String> = _units

    init {
        loadUnits()
    }

    private fun loadUnits() {
        viewModelScope.launch {
            val lastId = profileRepo.getLast()
            val profiles = profileRepo.getAll()
            val profile = profiles.find { it.id == lastId } ?: profiles.firstOrNull()
            _units.value = profile?.config?.units ?: "mm"
        }
    }

    fun addTool(name: String, length: Float, diameter: Float) {
        val nextId = (_toolLibrary.value.maxOfOrNull { it.id } ?: 0) + 1
        _toolLibrary.value += ToolData(nextId, name, length, diameter)
    }

    fun deleteTool(id: Int) {
        _toolLibrary.value = _toolLibrary.value.filter { it.id != id }
    }

    fun updateTool(id: Int, name: String, length: Float, diameter: Float) {
        _toolLibrary.value = _toolLibrary.value.map {
            if (it.id == id) it.copy(name = name, length = length, diameter = diameter) else it
        }
    }
}
