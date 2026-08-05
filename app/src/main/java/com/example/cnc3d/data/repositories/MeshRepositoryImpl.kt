package com.example.cnc3d.data.repositories

import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.data.datasources.FluidncDataSource
import com.example.cnc3d.data.datasources.MoonrakerDataSource
import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.models.MeshPoint
import com.example.cnc3d.domain.repositories.MeshRepository

class MeshRepositoryImpl(
    private val firmware: FirmwareType,
    private val fluidnc: FluidncDataSource?,
    private val moonraker: MoonrakerDataSource?
) : MeshRepository {

    override suspend fun probePoint(): MeshPoint? {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.probe()
            else -> null
        }
    }

    override suspend fun getMesh(): Mesh {
        return when (firmware) {
            FirmwareType.MOONRAKER -> moonraker!!.getBedMesh()
            else -> Mesh(emptyList())
        }
    }
}
