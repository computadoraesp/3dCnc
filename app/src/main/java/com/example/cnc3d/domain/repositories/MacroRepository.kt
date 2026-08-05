package com.example.cnc3d.domain.repositories

import com.example.cnc3d.domain.models.Macro

interface MacroRepository {
    suspend fun getMacros(): List<Macro>
    suspend fun saveMacros(list: List<Macro>)
    suspend fun execute(macro: Macro): Boolean
}
