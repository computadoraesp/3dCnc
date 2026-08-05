package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.models.MeshPoint
import com.example.cnc3d.domain.repositories.MeshRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MeshViewModel(
    private val repo: MeshRepository
) : ViewModel() {

    private val _mesh = MutableStateFlow<Mesh?>(null)
    val mesh: StateFlow<Mesh?> = _mesh

    private val _lastProbe = MutableStateFlow<MeshPoint?>(null)
    val lastProbe: StateFlow<MeshPoint?> = _lastProbe

    fun probe() {
        viewModelScope.launch {
            _lastProbe.value = repo.probePoint()
        }
    }

    fun loadMesh() {
        viewModelScope.launch {
            _mesh.value = repo.getMesh()
        }
    }
}
