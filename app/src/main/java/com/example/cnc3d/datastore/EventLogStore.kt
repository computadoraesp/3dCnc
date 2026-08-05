package com.example.cnc3d.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.cnc3d.domain.models.MachineEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.text.get

private val Context.eventStore by preferencesDataStore("events")

class EventLogStore(private val context: Context) {

    companion object {
        val EVENTS = stringPreferencesKey("events_json")
    }

    val events: Flow<List<MachineEvent>> = context.eventStore.data.map { prefs ->
        val raw = prefs[EVENTS] ?: "[]"
        Json.decodeFromString(raw)
    }

    suspend fun save(list: List<MachineEvent>) {
        val json = Json.encodeToString(list)
        context.eventStore.edit { it[EVENTS] = json }
    }
}
