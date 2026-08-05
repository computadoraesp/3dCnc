package com.example.cnc3d.data.repositories

import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.data.datasources.FluidncDataSource
import com.example.cnc3d.data.datasources.MoonrakerDataSource
import com.example.cnc3d.domain.repositories.JobExecutionRepository

class JobExecutionRepositoryImpl(
    private val firmware: FirmwareType,
    private val fluidnc: FluidncDataSource?,
    private val moonraker: MoonrakerDataSource?
) : JobExecutionRepository {

    override suspend fun startJob(fileName: String): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("start $fileName").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.startPrint(fileName)
            else -> false
        }
    }

    override suspend fun stopJob(): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("stop").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.sendCommand("printer.print.cancel").contains("ok")
            else -> false
        }
    }

    override suspend fun getProgress(): Float {
        return when (firmware) {
            FirmwareType.FLUIDNC -> 0f // FluidNC no expone progreso por HTTP
            FirmwareType.MOONRAKER -> moonraker!!.getStatus().progress
            else -> 0f
        }
    }
}
