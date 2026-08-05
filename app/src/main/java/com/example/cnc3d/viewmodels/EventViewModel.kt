package com.example.cnc3d.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cnc3d.domain.models.Event
import com.example.cnc3d.domain.models.MachineEvent
import com.example.cnc3d.domain.repositories.EventRepository
import com.example.cnc3d.domain.usecases.SubscribeEventsUseCase

import com.example.cnc3d.notifications.Notifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    private val subscribeEventsUseCase: SubscribeEventsUseCase,
    private val eventRepo: EventRepository,
    private val notifier: Notifier
) : ViewModel() {

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event

    private val _history = MutableStateFlow<List<MachineEvent>>(emptyList())
    val history: StateFlow<List<MachineEvent>> = _history

    fun start() {
        viewModelScope.launch {
            subscribeEventsUseCase().collect { ev ->
                _event.value = ev
                handleEvent(ev)
            }
        }
    }

    private fun handleEvent(ev: Event) {
        val machineEvent = MachineEvent(
            type = ev.type,
            message = ev.payload
        )

        viewModelScope.launch {
            eventRepo.log(machineEvent)
            notifier.notify(ev.type, ev.payload)
            _history.value = eventRepo.getAll()
        }
    }
    private fun processEvent(ev: Event) {
        val machineEvent = MachineEvent(
            type = ev.type,
            message = ev.payload
        )

        viewModelScope.launch {
            eventRepo.log(machineEvent)
            notifier.notify(ev.type, ev.payload)
            _history.value = eventRepo.getAll()
        }
    }
}
