package com.example.cnc3d.data.repositories


import com.example.cnc3d.datastore.MachineProfileStore
import com.example.cnc3d.domain.models.MachineProfile
import com.example.cnc3d.domain.repositories.MachineProfileRepository
import kotlinx.coroutines.flow.first

class MachineProfileRepositoryImpl(
    private val store: MachineProfileStore
) : MachineProfileRepository {

    override suspend fun getAll(): List<MachineProfile> {
        return store.machines.first()
    }

    override suspend fun saveAll(list: List<MachineProfile>) {
        store.saveMachines(list)
    }

    override suspend fun setLast(id: String) {
        store.setLastMachine(id)
    }

    override suspend fun getLast(): String? {
        return store.lastMachineId.first()
    }
}
