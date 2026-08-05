package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.models.ToolpathWarning
import com.example.cnc3d.domain.models.WarningType

class AnalyzeGcodeUseCase {

    fun analyze(lines: List<String>): List<ToolpathWarning> {

        val warnings = mutableListOf<ToolpathWarning>()

        var spindleOn = false
        var lastZ = 999f

        lines.forEachIndexed { index, raw ->
            val line = raw.trim().uppercase()

            // Detect spindle ON/OFF
            if (line.startsWith("M3")) spindleOn = true
            if (line.startsWith("M5")) spindleOn = false

            // Detect Z moves
            if ("Z" in line) {
                val z = line.substringAfter("Z").substringBefore(" ").toFloatOrNull()
                if (z != null) {

                    // Z negative unexpected
                    if (z < 0 && lastZ > 0) {
                        warnings.add(
                            ToolpathWarning(
                                WarningType.Z_NEGATIVE,
                                "Z negativo inesperado",
                                index + 1
                            )
                        )
                    }

                    lastZ = z
                }
            }

            // Feedrate too high
            if ("F" in line) {
                val f = line.substringAfter("F").substringBefore(" ").toFloatOrNull()
                if (f != null && f > 3000) {
                    warnings.add(
                        ToolpathWarning(
                            WarningType.FEEDRATE_HIGH,
                            "Feedrate excesivo: $f",
                            index + 1
                        )
                    )
                }
            }

            // Cutting without spindle
            if ((line.startsWith("G1") || line.startsWith("G2") || line.startsWith("G3"))
                && spindleOn == false
                && lastZ < 0
            ) {
                warnings.add(
                    ToolpathWarning(
                        WarningType.SPINDLE_OFF,
                        "Movimiento de corte con spindle apagado",
                        index + 1
                    )
                )
            }
        }

        return warnings
    }
}
