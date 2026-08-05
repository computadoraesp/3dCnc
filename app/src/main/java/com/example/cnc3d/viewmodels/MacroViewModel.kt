package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.Macro
import com.example.cnc3d.domain.repositories.MacroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MacroViewModel(
    private val repo: MacroRepository
) : ViewModel() {

    private val _macros = MutableStateFlow<List<Macro>>(emptyList())
    val macros: StateFlow<List<Macro>> = _macros

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    fun load() {
        viewModelScope.launch {
            _macros.value = repo.getMacros()
        }
    }

    fun addMacro(name: String, commands: List<String>) {
        viewModelScope.launch {
            val updated = _macros.value + Macro(name, commands)
            repo.saveMacros(updated)
            _macros.value = updated
        }
    }

    fun execute(macro: Macro) {
        viewModelScope.launch {
            val ok = repo.execute(macro)
            _status.value = if (ok) "Executed" else "Error"
        }
    }
}
