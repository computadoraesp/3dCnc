package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.SafetyStatus
import com.example.cnc3d.domain.repositories.MachineControlRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MachineControlViewModel(
    private val repo: MachineControlRepository,
    safetyStatus: StateFlow<SafetyStatus>
) : ViewModel() {

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    private val _safetyStatus = safetyStatus
    val safetyStatus: StateFlow<SafetyStatus> = _safetyStatus

    private fun update(ok: Boolean) {
        _status.value = if (ok) "OK" else "ERROR"
    }

    fun jog(axis: String, amount: Float) {
        if (!safeCheck()) return
        viewModelScope.launch { update(repo.jog(axis, amount)) }
    }

    fun home() {
        viewModelScope.launch { update(repo.home()) }
    }

    fun spindle(on: Boolean, rpm: Int = 0) {
        viewModelScope.launch { update(repo.spindle(on, rpm)) }
    }

    fun feedOverride(percent: Int) {
        viewModelScope.launch { update(repo.feedOverride(percent)) }
    }

    fun extrude(amount: Float, speed: Int) {
        viewModelScope.launch { update(repo.extrude(amount, speed)) }
    }

    fun setHotendTemp(temp: Int) {
        viewModelScope.launch { update(repo.setHotendTemp(temp)) }
    }

    fun setBedTemp(temp: Int) {
        viewModelScope.launch { update(repo.setBedTemp(temp)) }
    }
    fun safeCheck(): Boolean {
        return safetyStatus.value.isSafe
    }

}
