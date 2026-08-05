package com.example.cnc3d.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.PrinterWarning
import com.example.cnc3d.domain.usecases.AnalyzePrinterGcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrinterAnalyzerViewModel(
    private val analyzer: AnalyzePrinterGcodeUseCase
) : ViewModel() {

    private val _warnings = MutableStateFlow<List<PrinterWarning>>(emptyList())
    val warnings: StateFlow<List<PrinterWarning>> = _warnings

    fun analyze(lines: List<String>) {
        viewModelScope.launch {
            _warnings.value = analyzer.analyze(lines)
        }
    }
}
