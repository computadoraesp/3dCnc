package com.example.cnc3d.core.websocket

import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.domain.models.Event
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class EventStream @Inject constructor(private val connectionManager: ConnectionManager) {

    fun stream(): Flow<Event> {
        return connectionManager.messages()?.map { raw ->
            EventParser.parse(raw)
        } ?: emptyFlow()
    }
}
