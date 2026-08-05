package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.BedMesh
import com.example.cnc3d.domain.usecases.ApplyBedMeshToGcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BedMeshCorrectionViewModel(
    private val applyUseCase: ApplyBedMeshToGcodeUseCase
) : ViewModel() {

    private val _corrected = MutableStateFlow<List<String>>(emptyList())
    val corrected: StateFlow<List<String>> = _corrected

    fun apply(mesh: BedMesh, gcode: List<String>) {
        viewModelScope.launch {
            _corrected.value = applyUseCase.apply(mesh, gcode)
        }
    }
}
