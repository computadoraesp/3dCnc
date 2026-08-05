package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.CloudRepository
import com.example.cnc3d.domain.repositories.EventRepository
import com.example.cnc3d.domain.repositories.MachineProfileRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CloudSyncUseCase(
    private val cloudRepo: CloudRepository,
    private val machineRepo: MachineProfileRepository,
    private val eventRepo: EventRepository
) {

    suspend fun syncAll(): Boolean {
        val machines = machineRepo.getAll()
        val events = eventRepo.getAll()

        val machinesJson = Json.encodeToString(machines)
        val eventsJson = Json.encodeToString(events)

        val ok1 = cloudRepo.upload("machines.json", machinesJson.toByteArray())
        val ok2 = cloudRepo.upload("events.json", eventsJson.toByteArray())

        return ok1 && ok2
    }
}
