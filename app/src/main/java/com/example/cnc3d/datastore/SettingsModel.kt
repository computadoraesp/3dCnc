package com.example.cnc3d.datastore

data class SettingsModel(
    val lastIp: String = "",
    val lastFirmware: String = "",
    val autoConnect: Boolean = false
)
