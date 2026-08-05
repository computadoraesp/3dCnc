package com.example.cnc3d.data.repositories

import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.data.datasources.FluidncDataSource
import com.example.cnc3d.data.datasources.MoonrakerDataSource
import com.example.cnc3d.domain.models.CameraInfo
import com.example.cnc3d.domain.repositories.CameraRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes

class CameraRepositoryImpl(
    private val firmware: FirmwareType,
    private val moonraker: MoonrakerDataSource?,
    private val fluidnc: FluidncDataSource?,
    private val http: HttpClient
) : CameraRepository {

    override suspend fun getCameras(): List<CameraInfo> {
        return when (firmware) {
            FirmwareType.MOONRAKER -> moonraker!!.getCameras()
            FirmwareType.FLUIDNC -> listOf(fluidnc!!.getIpCamera(fluidnc.ip))
            else -> emptyList()
        }
    }

    override suspend fun snapshot(url: String): ByteArray? {
        return http.get(url).bodyAsBytes()
    }
}
