package com.example.cnc3d.data.repositories

import android.content.Context
import com.example.cnc3d.core.detection.FirmwareDetector
import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.core.network.BluetoothTransport
import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.core.network.ConnectionTransport
import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.core.network.NetworkTransport
import com.example.cnc3d.core.network.UsbTransport
import com.example.cnc3d.core.websocket.EventStream
import com.example.cnc3d.data.datasources.FluidncDataSource
import com.example.cnc3d.data.datasources.MoonrakerDataSource
import com.example.cnc3d.domain.models.Event
import com.example.cnc3d.domain.models.cnc.CncStatus
import com.example.cnc3d.domain.repositories.MachineRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class MachineRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scope: CoroutineScope,
    private val fluidnc: FluidncDataSource?,
    private val moonraker: MoonrakerDataSource?,
    private val eventStream: EventStream,
    private val detector: FirmwareDetector,
    private val connectionManager: ConnectionManager
) : MachineRepository {

    private var firmware: FirmwareType = FirmwareType.UNKNOWN
    private var currentType: ConnectionType? = null

    override suspend fun connect(address: String, type: ConnectionType): Boolean {
        currentType = type
        val transport: ConnectionTransport = when (type) {
            ConnectionType.WIFI -> {
                val baseUrl = if (address.startsWith("http")) address else "http://$address"
                val wsUrl = baseUrl.replace("http", "ws") + "/ws"
                connectionManager.configure(baseUrl)
                NetworkTransport(connectionManager.httpClient!!, scope, wsUrl)
            }
            ConnectionType.BLUETOOTH -> BluetoothTransport(context, scope, address)
            ConnectionType.USB -> UsbTransport(context, scope)
        }

        connectionManager.setTransport(transport)
        connectionManager.connect()

        // After connecting, we try to detect firmware
        val result = detector.detect()
        firmware = result.firmware
        
        // If it's serial/BT and detection failed, we might assume it's GRBL (FluidNC base)
        if (firmware == FirmwareType.UNKNOWN && type != ConnectionType.WIFI) {
            firmware = FirmwareType.FLUIDNC 
        }

        return firmware != FirmwareType.UNKNOWN
    }

    override suspend fun detectFirmware(address: String): String {
        val result = detector.detect()
        firmware = result.firmware
        return firmware.name
    }

    override suspend fun getStatus(): Any {
        val status = when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.getStatus()
            FirmwareType.MOONRAKER -> moonraker!!.getStatus()
            else -> return "Unknown firmware"
        }

        return if (status is CncStatus) {
            status.copy(connectionType = currentType)
        } else {
            status
        }
    }

    override suspend fun startJob(fileName: String): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("start $fileName").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.startPrint(fileName)
            else -> false
        }
    }

    override suspend fun pauseJob(): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("hold").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.pause()
            else -> false
        }
    }

    override suspend fun resumeJob(): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("resume").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.resume()
            else -> false
        }
    }

    override suspend fun cancelJob(): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("reset").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.cancel()
            else -> false
        }
    }

    override suspend fun uploadFile(name: String, bytes: ByteArray): Boolean {
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

    override suspend fun sendCommand(command: String): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> {
                fluidnc!!.sendCommand(command)
                true
            }
            FirmwareType.MOONRAKER -> {
                moonraker!!.sendCommand(command)
                true
            }
            else -> false
        }
    }

    override fun subscribeEvents(): Flow<Event> {
        return when (firmware) {
            FirmwareType.FLUIDNC, FirmwareType.MOONRAKER -> eventStream.stream()
            else -> emptyFlow()
        }
    }
}
