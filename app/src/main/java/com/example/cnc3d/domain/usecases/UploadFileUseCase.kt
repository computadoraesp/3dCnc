package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository

class UploadFileUseCase(private val repo: MachineRepository) {
    suspend operator fun invoke(name: String, bytes: ByteArray) =
        repo.uploadFile(name, bytes)
}
