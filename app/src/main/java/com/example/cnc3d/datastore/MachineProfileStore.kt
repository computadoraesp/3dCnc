package com.example.cnc3d.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.cnc3d.domain.models.MachineProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.text.get

private val Context.machineStore by preferencesDataStore("machines")

class MachineProfileStore(private val context: Context) {

    companion object {
        val MACHINES = stringPreferencesKey("machines_json")
        val LAST_MACHINE = stringPreferencesKey("last_machine_id")
    }

    val machines: Flow<List<MachineProfile>> = context.machineStore.data.map { prefs ->
        val raw = prefs[MACHINES] ?: "[]"
        Json.decodeFromString(raw)
    }

    val lastMachineId: Flow<String?> = context.machineStore.data.map { prefs ->
        prefs[LAST_MACHINE]
    }

    suspend fun saveMachines(list: List<MachineProfile>) {
        val json = Json.encodeToString(list)
        context.machineStore.edit { it[MACHINES] = json }
    }

    suspend fun setLastMachine(id: String) {
        context.machineStore.edit { it[LAST_MACHINE] = id }
    }
}
