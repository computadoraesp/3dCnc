package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.domain.models.MachineProfile
import com.example.cnc3d.domain.repositories.MachineProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachineSelectorViewModel @Inject constructor(
    private val repo: MachineProfileRepository
) : ViewModel() {

    private val _machines = MutableStateFlow<List<MachineProfile>>(emptyList())
    val machines: StateFlow<List<MachineProfile>> = _machines

    private val _selected = MutableStateFlow<MachineProfile?>(null)
    val selected: StateFlow<MachineProfile?> = _selected

    fun load() {
        viewModelScope.launch {
            _machines.value = repo.getAll()

            val last = repo.getLast()
            _selected.value = _machines.value.find { it.id == last }
        }
    }

    fun select(profile: MachineProfile) {
        viewModelScope.launch {
            _selected.value = profile
            repo.setLast(profile.id)
        }
    }

    fun add(name: String, address: String, firmware: FirmwareType, connectionType: ConnectionType = ConnectionType.WIFI) {
        viewModelScope.launch {
            val new = MachineProfile(
                id = System.currentTimeMillis().toString(),
                name = name,
                address = address,
                connectionType = connectionType,
                firmware = firmware
            )

            val updated = _machines.value + new
            repo.saveAll(updated)
            _machines.value = updated
        }
    }
}
