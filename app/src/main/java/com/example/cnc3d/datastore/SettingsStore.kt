package com.example.cnc3d.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.text.get

private val Context.dataStore by preferencesDataStore("settings")

class SettingsStore(private val context: Context) {

    companion object {
        val LAST_IP = stringPreferencesKey("last_ip")
        val LAST_FW = stringPreferencesKey("last_fw")
        val AUTO_CONNECT = booleanPreferencesKey("auto_connect")
    }

    val settings: Flow<SettingsModel> = context.dataStore.data.map { prefs ->
        SettingsModel(
            lastIp = prefs[LAST_IP] ?: "",
            lastFirmware = prefs[LAST_FW] ?: "",
            autoConnect = prefs[AUTO_CONNECT] ?: false
        )
    }

    suspend fun saveIp(ip: String) {
        context.dataStore.edit { it[LAST_IP] = ip }
    }

    suspend fun saveFirmware(fw: String) {
        context.dataStore.edit { it[LAST_FW] = fw }
    }

    suspend fun setAutoConnect(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_CONNECT] = enabled }
    }
}
