package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository
import javax.inject.Inject

class PauseJobUseCase @Inject constructor(private val repo: MachineRepository) {
    suspend operator fun invoke() = repo.pauseJob()
}
