package com.example.cnc3d.domain.usecases

import com.example.cnc3d.domain.models.PrinterWarning
import com.example.cnc3d.domain.models.PrinterWarningType

class AnalyzePrinterGcodeUseCase {

    fun analyze(lines: List<String>): List<PrinterWarning> {

        val warnings = mutableListOf<PrinterWarning>()

        var lastZ = 0f
        var extruderTemp = 0f
        var fanOn = false

        lines.forEachIndexed { index, raw ->
            val line = raw.trim().uppercase()

            // Detect temperature commands
            if (line.startsWith("M104") || line.startsWith("M109")) {
                val t = line.substringAfter("S").toFloatOrNull()
                if (t != null) extruderTemp = t

                if (t != null && t < 180) {
                    warnings.add(
                        PrinterWarning(
                            PrinterWarningType.TEMP_TOO_LOW,
                            "Temperatura demasiado baja: $t°C",
                            index + 1
                        )
                    )
                }

                if (t != null && t > 260) {
                    warnings.add(
                        PrinterWarning(
                            PrinterWarningType.TEMP_TOO_HIGH,
                            "Temperatura demasiado alta: $t°C",
                            index + 1
                        )
                    )
                }
            }

            // Detect fan
            if (line.startsWith("M106")) fanOn = true
            if (line.startsWith("M107")) fanOn = false

            // Detect Z moves
            if ("Z" in line) {
                val z = line.substringAfter("Z").substringBefore(" ").toFloatOrNull()
                if (z != null) {

                    if (z - lastZ > 0.5f) {
                        warnings.add(
                            PrinterWarning(
                                PrinterWarningType.LAYER_JUMP,
                                "Salto de capa sospechoso: Z=$z",
                                index + 1
                            )
                        )
                    }

                    lastZ = z
                }
            }

            // Detect extrusion
            if ("E" in line && line.startsWith("G1")) {
                val e = line.substringAfter("E").substringBefore(" ").toFloatOrNull()
                if (e != null && e > 5f) {
                    warnings.add(
                        PrinterWarning(
                            PrinterWarningType.EXTRUSION_TOO_HIGH,
                            "Extrusión excesiva: E=$e",
                            index + 1
                        )
                    )
                }
            }

            // Detect retraction
            if ("E" in line && line.startsWith("G1") && "-" in line) {
                val e = line.substringAfter("E").substringBefore(" ").toFloatOrNull()
                if (e != null && e < -3f) {
                    warnings.add(
                        PrinterWarning(
                            PrinterWarningType.RETRACTION_TOO_LONG,
                            "Retracción demasiado larga: E=$e",
                            index + 1
                        )
                    )
                }
            }

            // Move without heat
            if (line.startsWith("G1") && extruderTemp < 180) {
                warnings.add(
                    PrinterWarning(
                        PrinterWarningType.MOVE_WITHOUT_HEAT,
                        "Movimiento sin extrusor caliente",
                        index + 1
                    )
                )
            }

            // Fan off on high layers
            if (lastZ > 5 && !fanOn) {
                warnings.add(
                    PrinterWarning(
                        PrinterWarningType.FAN_OFF_HIGH_LAYER,
                        "Fan apagado en capas superiores",
                        index + 1
                    )
                )
            }
        }

        return warnings
    }
}

