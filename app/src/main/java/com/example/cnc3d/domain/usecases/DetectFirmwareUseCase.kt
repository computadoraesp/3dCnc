package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository

class DetectFirmwareUseCase(private val repo: MachineRepository) {
    suspend operator fun invoke(ip: String) = repo.detectFirmware(ip)
}
