package com.example.cnc3d.domain.repositories

import com.example.cnc3d.domain.models.MachineEvent

interface EventRepository {
    suspend fun log(event: MachineEvent)
    suspend fun getAll(): List<MachineEvent>
}
