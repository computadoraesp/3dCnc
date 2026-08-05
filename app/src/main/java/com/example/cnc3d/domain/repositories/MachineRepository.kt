package com.example.cnc3d.domain.repositories

import com.example.cnc3d.domain.models.Event
import com.example.cnc3d.domain.models.*
import com.example.cnc3d.core.network.ConnectionType
import kotlinx.coroutines.flow.Flow

interface MachineRepository {

    suspend fun connect(address: String, type: ConnectionType = ConnectionType.WIFI): Boolean

    suspend fun detectFirmware(address: String): String

    suspend fun getStatus(): Any

    suspend fun startJob(fileName: String): Boolean

    suspend fun pauseJob(): Boolean

    suspend fun resumeJob(): Boolean

    suspend fun cancelJob(): Boolean

    suspend fun uploadFile(name: String, bytes: ByteArray): Boolean

    suspend fun listFiles(): List<String>

    suspend fun sendCommand(command: String): Boolean

    fun subscribeEvents(): Flow<Event>

}
