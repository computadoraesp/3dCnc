package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.SafetyStatus
import com.example.cnc3d.domain.usecases.SubscribeEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SafetyViewModel(
    private val subscribeEventsUseCase: SubscribeEventsUseCase
) : ViewModel() {

    private val _status = MutableStateFlow(SafetyStatus())
    val status: StateFlow<SafetyStatus> = _status

    fun start() {
        viewModelScope.launch {
            subscribeEventsUseCase().collect { event ->
                when (event.type) {

                    "ALARM" -> {
                        _status.value = _status.value.copy(
                            alarm = event.payload,
                            isSafe = false
                        )
                    }

                    "LIMIT" -> {
                        val axis = event.payload.substringAfter("limit ").substringBefore(" ")
                        val active = event.payload.contains("triggered", ignoreCase = true)

                        val updated = _status.value.limits.toMutableMap()
                        updated[axis] = active

                        _status.value = _status.value.copy(
                            limits = updated,
                            isSafe = !active
                        )
                    }
                }
            }
        }
    }

    fun resetAlarm() {
        _status.value = SafetyStatus()
    }
}
