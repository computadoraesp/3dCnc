package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository
import javax.inject.Inject

class ObserveMachineStatusUseCase @Inject constructor(private val repo: MachineRepository) {
    operator fun invoke() = repo.machineStatus
}
