package com.example.cnc3d.domain.usecases

import com.example.cnc3d.core.mesh.BilinearInterpolator
import com.example.cnc3d.domain.models.BedMesh

class ApplyBedMeshToGcodeUseCase {

    fun apply(mesh: BedMesh, lines: List<String>): List<String> {

        val interp = BilinearInterpolator(
            com.example.cnc3d.domain.models.Mesh(
                mesh.points.map {
                    com.example.cnc3d.domain.models.MeshPoint(it.x, it.y, it.z)
                }
            )
        )

        val out = mutableListOf<String>()

        var lastX = 0f
        var lastY = 0f

        lines.forEach { raw ->
            val line = raw.trim()
            var newLine = line

            if (line.startsWith("G1") || line.startsWith("G0")) {

                if ("X" in line)
                    lastX = line.substringAfter("X").substringBefore(" ").toFloatOrNull() ?: lastX

                if ("Y" in line)
                    lastY = line.substringAfter("Y").substringBefore(" ").toFloatOrNull() ?: lastY

                if ("Z" in line) {
                    val originalZ = line.substringAfter("Z").substringBefore(" ").toFloatOrNull()
                    if (originalZ != null) {
                        val correction = interp.zAt(lastX, lastY)
                        val newZ = originalZ + correction
                        newLine = line.replace("Z$originalZ", "Z$newZ")
                    }
                }
            }

            out.add(newLine)
        }

        return out
    }
}
