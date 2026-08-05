package com.example.cnc3d.core.mesh

import com.example.cnc3d.domain.models.Mesh
import kotlin.math.abs

class BilinearInterpolator(private val mesh: Mesh) {

    fun zAt(x: Float, y: Float): Float {

        val p = mesh.points

        // Encuentra los 4 puntos más cercanos
        val sorted = p.sortedBy { abs(it.x - x) + abs(it.y - y) }

        if (sorted.size < 4) return 0f

        val p1 = sorted[0]
        val p2 = sorted[1]
        val p3 = sorted[2]
        val p4 = sorted[3]

        val x1 = p1.x; val y1 = p1.y; val z1 = p1.z
        val x2 = p2.x; val y2 = p2.y; val z2 = p2.z
        val x3 = p3.x; val y3 = p3.y; val z3 = p3.z
        val x4 = p4.x; val y4 = p4.y; val z4 = p4.z

        val denom = (x2 - x1) * (y3 - y1)
        if (denom == 0f) return z1

        val tx = (x - x1) / (x2 - x1)
        val ty = (y - y1) / (y3 - y1)

        val zTop = z1 + tx * (z2 - z1)
        val zBottom = z3 + tx * (z4 - z3)

        return zTop + ty * (zBottom - zTop)
    }
}
