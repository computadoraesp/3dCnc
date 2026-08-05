package com.example.cnc3d.domain.repositories

import com.example.cnc3d.domain.models.SlicerProfile

interface SlicerRepository {
    suspend fun getAll(): List<SlicerProfile>
    suspend fun saveAll(list: List<SlicerProfile>)
    suspend fun slice(stlBytes: ByteArray, profile: SlicerProfile): ByteArray?
}
