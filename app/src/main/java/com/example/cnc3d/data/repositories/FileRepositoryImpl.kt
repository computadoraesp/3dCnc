package com.example.cnc3d.data.repositories

import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.data.datasources.FluidncDataSource
import com.example.cnc3d.data.datasources.MoonrakerDataSource
import com.example.cnc3d.domain.repositories.FileRepository

class FileRepositoryImpl(
    private val firmware: FirmwareType,
    private val fluidnc: FluidncDataSource?,
    private val moonraker: MoonrakerDataSource?
) : FileRepository {

    override suspend fun upload(name: String, bytes: ByteArray): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.uploadFile(name, bytes)
            FirmwareType.MOONRAKER -> moonraker!!.uploadFile(name, bytes)
            else -> false
        }
    }

        override suspend fun listFiles(): List<String> {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.listFiles()
            FirmwareType.MOONRAKER -> moonraker!!.listFiles()
            else -> emptyList()
        }
    }

    override suspend fun delete(name: String): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.delete(name)
            FirmwareType.MOONRAKER -> moonraker!!.delete(name)
            else -> false
        }
    }

}