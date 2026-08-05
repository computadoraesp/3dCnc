package com.example.cnc3d.core.websocket

import com.example.cnc3d.domain.models.Event

/**
 * EventParser
 * Convierte texto crudo recibido por WebSocket en un Event tipado.
 *
 * Este parser es EXTENSIBLE y seguro:
 * - No inventa formatos
 * - No asume estructuras
 * - Detecta eventos por patrones reales
 */
object EventParser {

    fun parse(raw: String): Event {

        val type = when {

            // --- CNC POSITION ---
            raw.contains("pos", ignoreCase = true) ||
                    raw.contains("X:", ignoreCase = true) ||
                    raw.contains("Y:", ignoreCase = true) ||
                    raw.contains("Z:", ignoreCase = true) ->
                "POSITION"

            // --- TEMPERATURES ---
            raw.contains("temp", ignoreCase = true) ||
                    raw.contains("T:", ignoreCase = true) ||
                    raw.contains("B:", ignoreCase = true) ->
                "TEMPERATURE"

            // --- ALARMS ---
            raw.contains("alarm", ignoreCase = true) ->
                "ALARM"

            // --- LIMIT SWITCHES ---
            raw.contains("limit", ignoreCase = true) ||
                    raw.contains("endstop", ignoreCase = true) ->
                "LIMIT"

            // --- MACHINE STATE ---
            raw.contains("state", ignoreCase = true) ||
                    raw.contains("idle", ignoreCase = true) ||
                    raw.contains("running", ignoreCase = true) ||
                    raw.contains("paused", ignoreCase = true) ->
                "STATE"

            // --- JOB PROGRESS ---
            raw.contains("progress", ignoreCase = true) ||
                    raw.matches(Regex("^\\d+(\\.\\d+)?%$")) ->
                "PROGRESS"

            // --- UNKNOWN ---
            else -> "UNKNOWN"
        }

        return Event(type, raw)
    }
}
