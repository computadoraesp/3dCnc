package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.*
import com.example.cnc3d.domain.models.cnc.CNCPosition
import com.example.cnc3d.domain.usecases.SubscribeEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RealtimeDashboardViewModel(
    private val subscribeEventsUseCase: SubscribeEventsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RealtimeDashboardState())
    val state: StateFlow<RealtimeDashboardState> = _state

    fun start() {
        viewModelScope.launch {
            subscribeEventsUseCase().collect { event ->
                when (event.type) {

                    "POSITION" -> {
                        val pos = parsePosition(event.payload)
                        _state.value = _state.value.copy(cncPosition = pos)
                    }

                    "TEMPERATURE" -> {
                        val temps = parseTemps(event.payload)
                        _state.value = _state.value.copy(printerTemps = temps)
                    }

                    "STATE" -> {
                        _state.value = _state.value.copy(machineState = event.payload)
                    }

                    "PROGRESS" -> {
                        val p = event.payload.toFloatOrNull() ?: 0f
                        _state.value = _state.value.copy(jobProgress = p)
                    }
                }
            }
        }
    }

    private fun parsePosition(raw: String): CNCPosition {
        val x = raw.substringAfter("X:").substringBefore(" ").toFloatOrNull() ?: 0f
        val y = raw.substringAfter("Y:").substringBefore(" ").toFloatOrNull() ?: 0f
        val z = raw.substringAfter("Z:").substringBefore(" ").toFloatOrNull() ?: 0f
        val rpm = raw.substringAfter("RPM:").substringBefore(" ").toIntOrNull() ?: 0
        val feed = raw.substringAfter("F:").substringBefore(" ").toIntOrNull() ?: 0

        return CNCPosition(x, y, z, rpm, feed)
    }

    private fun parseTemps(raw: String): PrinterTemps {
        val hotend = raw.substringAfter("T:").substringBefore(" ").toFloatOrNull() ?: 0f
        val bed = raw.substringAfter("B:").substringBefore(" ").toFloatOrNull() ?: 0f

        return PrinterTemps(hotend, bed)
    }
}
