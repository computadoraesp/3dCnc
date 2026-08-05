package com.example.cnc3d.data.repositories

import com.example.cnc3d.datastore.EventLogStore
import com.example.cnc3d.domain.models.MachineEvent
import com.example.cnc3d.domain.repositories.EventRepository
import kotlinx.coroutines.flow.first

class EventRepositoryImpl(
    private val store: EventLogStore
) : EventRepository {

    override suspend fun log(event: MachineEvent) {
        val list = store.events.first()
        store.save(list + event)
    }

    override suspend fun getAll(): List<MachineEvent> {
        return store.events.first()
    }
}

