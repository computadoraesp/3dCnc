package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.usecases.ApplyMeshToGcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MeshCorrectionViewModel(
    private val applyUseCase: ApplyMeshToGcodeUseCase
) : ViewModel() {

    private val _corrected = MutableStateFlow<List<String>>(emptyList())
    val corrected: StateFlow<List<String>> = _corrected

    fun apply(mesh: Mesh, gcode: List<String>) {
        viewModelScope.launch {
            _corrected.value = applyUseCase.apply(mesh, gcode)
        }
    }
}
