package com.example.cnc3d.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.ToolpathWarning
import com.example.cnc3d.domain.usecases.AnalyzeGcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ToolpathAnalyzerViewModel(
    private val analyzer: AnalyzeGcodeUseCase
) : ViewModel() {

    private val _warnings = MutableStateFlow<List<ToolpathWarning>>(emptyList())
    val warnings: StateFlow<List<ToolpathWarning>> = _warnings

    fun analyze(lines: List<String>) {
        viewModelScope.launch {
            _warnings.value = analyzer.analyze(lines)
        }
    }
}
