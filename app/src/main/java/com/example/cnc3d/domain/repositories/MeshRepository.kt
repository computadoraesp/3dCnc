package com.example.cnc3d.domain.repositories

import com.example.cnc3d.domain.models.Mesh
import com.example.cnc3d.domain.models.MeshPoint

interface MeshRepository {
    suspend fun probePoint(): MeshPoint?
    suspend fun getMesh(): Mesh
}
