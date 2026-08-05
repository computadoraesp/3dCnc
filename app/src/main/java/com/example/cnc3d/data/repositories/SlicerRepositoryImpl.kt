package com.example.cnc3d.data.repositories

import com.example.cnc3d.core.api.SlicerApi
import com.example.cnc3d.datastore.SlicerStore
import com.example.cnc3d.domain.models.SlicerProfile
import com.example.cnc3d.domain.models.SlicerType
import com.example.cnc3d.domain.repositories.SlicerRepository
import kotlinx.coroutines.flow.first

class SlicerRepositoryImpl(
    private val store: SlicerStore,
    private val api: SlicerApi
) : SlicerRepository {

    override suspend fun getAll(): List<SlicerProfile> {
        return store.slicers.first()
    }

    override suspend fun saveAll(list: List<SlicerProfile>) {
        store.save(list)
    }

    override suspend fun slice(stlBytes: ByteArray, profile: SlicerProfile): ByteArray? {
        return when (profile.type) {

            SlicerType.CURA ->
                api.sliceCura(profile.endpoint, stlBytes)

            SlicerType.PRUSASLICER ->
                api.slicePrusa(profile.endpoint, stlBytes)

            SlicerType.ORCA ->
                api.sliceOrca(profile.endpoint, stlBytes)

            SlicerType.SUPERSLICER ->
                api.sliceSuper(profile.endpoint, stlBytes)

            SlicerType.CNC_INTERNAL ->
                generateCncGcode(stlBytes)

        }
    }

    private fun generateCncGcode(stlBytes: ByteArray): ByteArray? {
        // TODO: Implement CNC G-code generation from STL bytes
        return null
    }
}
