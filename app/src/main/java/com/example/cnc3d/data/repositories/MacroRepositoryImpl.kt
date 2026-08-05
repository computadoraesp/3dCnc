package com.example.cnc3d.data.repositories


import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.data.datasources.FluidncDataSource
import com.example.cnc3d.data.datasources.MoonrakerDataSource
import com.example.cnc3d.datastore.MacroStore
import com.example.cnc3d.domain.models.Macro
import com.example.cnc3d.domain.repositories.MacroRepository
import kotlinx.coroutines.flow.first

class MacroRepositoryImpl(
    private val store: MacroStore,
    private val firmware: FirmwareType,
    private val fluidnc: FluidncDataSource?,
    private val moonraker: MoonrakerDataSource?
) : MacroRepository {

    override suspend fun getMacros(): List<Macro> {
        return store.macros.first()
    }

    override suspend fun saveMacros(list: List<Macro>) {
        store.save(list)
    }

    override suspend fun execute(macro: Macro): Boolean {
        return when (firmware) {

            FirmwareType.FLUIDNC -> {
                macro.commands.all { cmd ->
                    fluidnc!!.sendCommand(cmd).contains("ok", ignoreCase = true)
                }
            }

            FirmwareType.MOONRAKER -> {
                macro.commands.all { cmd ->
                    moonraker!!.sendCommand("printer.gcode.script", cmd).isNotEmpty()
                }
            }

            else -> false
        }
    }
}
