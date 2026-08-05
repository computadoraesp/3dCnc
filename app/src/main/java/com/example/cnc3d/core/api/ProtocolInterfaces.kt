package com.example.cnc3d.core.api

interface CncController {
    suspend fun getStatus(): String
    suspend fun sendCommand(cmd: String): String
    suspend fun uploadFile(name: String, bytes: ByteArray): Boolean
}

interface PrinterController {
    suspend fun getPrinterInfo(): String
    suspend fun queryObjects(): String
    suspend fun startPrint(file: String): Boolean
    suspend fun uploadFile(name: String, bytes: ByteArray): Boolean
}
