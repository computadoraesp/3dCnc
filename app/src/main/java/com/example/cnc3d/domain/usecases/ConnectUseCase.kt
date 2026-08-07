package com.example.cnc3d.domain.usecases

import com.example.cnc3d.core.network.ConnectionType
import com.example.cnc3d.domain.repositories.MachineRepository
import javax.inject.Inject

class ConnectUseCase @Inject constructor(private val repo: MachineRepository) {
    suspend operator fun invoke(address: String, type: ConnectionType) = repo.connect(address, type)
}
