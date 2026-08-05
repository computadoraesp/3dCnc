package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.datastore.LayoutStore
import com.example.cnc3d.ui.layout.LayoutConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LayoutViewModel(
    private val store: LayoutStore
) : ViewModel() {

    private val _layout = MutableStateFlow<LayoutConfig?>(null)
    val layout: StateFlow<LayoutConfig?> = _layout

    fun load() {
        viewModelScope.launch {
            store.layout.collect { _layout.value = it }
        }
    }

    fun update(config: LayoutConfig) {
        viewModelScope.launch {
            store.save(config)
            _layout.value = config
        }
    }
}
