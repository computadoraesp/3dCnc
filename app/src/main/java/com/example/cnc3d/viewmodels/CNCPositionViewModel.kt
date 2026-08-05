package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.cnc.CNCPosition
import com.example.cnc3d.domain.usecases.SubscribeEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CNCPositionViewModel(
    private val subscribeEventsUseCase: SubscribeEventsUseCase
) : ViewModel() {

    private val _position = MutableStateFlow<CNCPosition?>(null)
    val position: StateFlow<CNCPosition?> = _position

    fun start() {
        viewModelScope.launch {
            subscribeEventsUseCase().collect { event ->
                if (event.type == "POSITION") {
                    parsePosition(event.payload)
                }
            }
        }
    }

    private fun parsePosition(raw: String) {
        // Ejemplo de payload:
        // pos X:10.0 Y:20.0 Z:5.0 RPM:12000 F:800

        val x = raw.substringAfter("X:").substringBefore(" ").toFloatOrNull() ?: 0f
        val y = raw.substringAfter("Y:").substringBefore(" ").toFloatOrNull() ?: 0f
        val z = raw.substringAfter("Z:").substringBefore(" ").toFloatOrNull() ?: 0f
        val rpm = raw.substringAfter("RPM:").substringBefore(" ").toIntOrNull() ?: 0
        val feed = raw.substringAfter("F:").substringBefore(" ").toIntOrNull() ?: 0

        _position.value = CNCPosition(x, y, z, rpm, feed)
    }
}
