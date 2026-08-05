package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository
import javax.inject.Inject

class ListFilesUseCase @Inject constructor(
    private val repo: MachineRepository
) {
    suspend operator fun invoke(): List<String> {
        return repo.listFiles()
    }
}
