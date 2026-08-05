package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.repositories.MachineRepository

class SubscribeEventsUseCase(private val repo: MachineRepository) {
    operator fun invoke() = repo.subscribeEvents()
}
