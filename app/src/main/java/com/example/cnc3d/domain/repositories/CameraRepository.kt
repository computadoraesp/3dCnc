package com.example.cnc3d.domain.repositories

import com.example.cnc3d.domain.models.CameraInfo

interface CameraRepository {
    suspend fun getCameras(): List<CameraInfo>
    suspend fun snapshot(url: String): ByteArray?
}
