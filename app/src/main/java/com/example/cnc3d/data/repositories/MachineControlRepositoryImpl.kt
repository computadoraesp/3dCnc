package com.example.cnc3d.data.repositories

import com.example.cnc3d.core.detection.FirmwareType
import com.example.cnc3d.data.datasources.FluidncDataSource
import com.example.cnc3d.data.datasources.MoonrakerDataSource
import com.example.cnc3d.domain.repositories.MachineControlRepository

class MachineControlRepositoryImpl(
    private val firmware: FirmwareType,
    private val fluidnc: FluidncDataSource?,
    private val moonraker: MoonrakerDataSource?
) : MachineControlRepository {

    override suspend fun jog(axis: String, amount: Float): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC ->
                fluidnc!!.sendCommand("G91") != "" &&
                        fluidnc.sendCommand("G0 $axis$amount") != "" &&
                        fluidnc.sendCommand("G90").contains("ok")

            FirmwareType.MOONRAKER ->
                moonraker!!.sendCommand("printer.gcode.script", "G91") != "" &&
                        moonraker.sendCommand("printer.gcode.script", "G0 $axis$amount") != "" &&
                        moonraker.sendCommand("printer.gcode.script", "G90") != ""

            else -> false
        }
    }

    override suspend fun home(): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("G28").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.sendCommand("printer.gcode.script", "G28") != ""
            else -> false
        }
    }

    override suspend fun spindle(on: Boolean, rpm: Int): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC ->
                if (on) fluidnc!!.sendCommand("M3 S$rpm").contains("ok")
                else fluidnc!!.sendCommand("M5").contains("ok")

            else -> false
        }
    }

    override suspend fun feedOverride(percent: Int): Boolean {
        return when (firmware) {
            FirmwareType.FLUIDNC -> fluidnc!!.sendCommand("M220 S$percent").contains("ok")
            FirmwareType.MOONRAKER -> moonraker!!.sendCommand("printer.gcode.script", "M220 S$percent") != ""
            else -> false
        }
    }

    override suspend fun extrude(amount: Float, speed: Int): Boolean {
        return when (firmware) {
            FirmwareType.MOONRAKER ->
                moonraker!!.sendCommand("printer.gcode.script", "G91") != "" &&
                        moonraker.sendCommand("printer.gcode.script", "G1 E$amount F$speed") != "" &&
                        moonraker.sendCommand("printer.gcode.script", "G90") != ""

            else -> false
        }
    }

    override suspend fun setHotendTemp(temp: Int): Boolean {
        return when (firmware) {
            FirmwareType.MOONRAKER ->
                moonraker!!.sendCommand("printer.gcode.script", "M104 S$temp") != ""

            else -> false
        }
    }

    override suspend fun setBedTemp(temp: Int): Boolean {
        return when (firmware) {
            FirmwareType.MOONRAKER ->
                moonraker!!.sendCommand("printer.gcode.script", "M140 S$temp") != ""

            else -> false
        }
    }

    override suspend fun moveAxis(axis: String, amount: Float): Boolean {
        return jog(axis, amount)
    }

    override suspend fun homePrinter(): Boolean {
        return home()
    }
}
