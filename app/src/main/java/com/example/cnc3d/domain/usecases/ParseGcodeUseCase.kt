package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.models.GcodePath
import com.example.cnc3d.domain.models.GcodeSegment

class ParseGcodeUseCase {

    fun parse(lines: List<String>): GcodePath {

        var x = 0f
        var y = 0f
        var z = 0f

        val segments = mutableListOf<GcodeSegment>()

        for (line in lines) {
            val clean = line.trim().uppercase()

            if (clean.startsWith("G0") || clean.startsWith("G1")) {

                val oldX = x
                val oldY = y
                val oldZ = z

                if ("X" in clean) x = clean.substringAfter("X").substringBefore(" ").toFloatOrNull() ?: x
                if ("Y" in clean) y = clean.substringAfter("Y").substringBefore(" ").toFloatOrNull() ?: y
                if ("Z" in clean) z = clean.substringAfter("Z").substringBefore(" ").toFloatOrNull() ?: z

                val rapid = clean.startsWith("G0")
                val extrude = "E" in clean

                segments.add(
                    GcodeSegment(
                        oldX, oldY, oldZ,
                        x, y, z,
                        rapid = rapid,
                        extrude = extrude
                    )
                )
            }
        }

        return GcodePath(segments)
    }
}
