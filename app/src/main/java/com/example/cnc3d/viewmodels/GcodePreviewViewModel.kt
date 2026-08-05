package com.example.cnc3d.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.usecases.ParseGcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GcodePreviewViewModel(
    private val parser: ParseGcodeUseCase
) : ViewModel() {

    private val _path = MutableStateFlow<GcodePath?>(null)
    val path: StateFlow<GcodePath?> = _path

    fun load(lines: List<String>) {
        viewModelScope.launch {
            _path.value = parser.parse(lines)
        }
    }
}

