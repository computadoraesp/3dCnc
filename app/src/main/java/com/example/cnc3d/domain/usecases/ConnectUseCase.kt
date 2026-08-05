package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository

class ConnectUseCase(private val repo: MachineRepository) {
    suspend operator fun invoke(ip: String) = repo.connect(ip)
}
