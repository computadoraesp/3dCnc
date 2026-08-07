package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.cnc.CncStatus
import com.example.cnc3d.domain.usecases.ObserveMachineStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CncDiagViewModel @Inject constructor(
    private val observeMachineStatusUseCase: ObserveMachineStatusUseCase
) : ViewModel() {

    private val _status = MutableStateFlow(CncStatus.DISCONNECTED)
    val status: StateFlow<CncStatus> = _status

    init {
        viewModelScope.launch {
            observeMachineStatusUseCase().collect { result ->
                if (result is CncStatus) {
                    _status.value = result
                }
            }
        }
    }
}
