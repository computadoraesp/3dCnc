package com.example.cnc3d.domain.repositories

import com.example.cnc3d.domain.models.MachineProfile

interface MachineProfileRepository {
    suspend fun getAll(): List<MachineProfile>
    suspend fun saveAll(list: List<MachineProfile>)
    suspend fun setLast(id: String)
    suspend fun getLast(): String?
}
