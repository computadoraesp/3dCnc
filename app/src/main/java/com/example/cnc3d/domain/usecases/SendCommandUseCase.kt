package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository
import javax.inject.Inject

class SendCommandUseCase @Inject constructor(
    private val repo: MachineRepository
) {
    suspend operator fun invoke(command: String): Boolean {
        return repo.sendCommand(command)
    }
}
