package com.example.cnc3d.domain.repositories

interface JobExecutionRepository {
    suspend fun startJob(fileName: String): Boolean
    suspend fun stopJob(): Boolean
    suspend fun getProgress(): Float
}
