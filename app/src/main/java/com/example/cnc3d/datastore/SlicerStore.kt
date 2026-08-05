package com.example.cnc3d.datastore


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.cnc3d.domain.models.SlicerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

private val Context.slicerStore by preferencesDataStore("slicers")

class SlicerStore(private val context: Context) {

    companion object {
        val SLICERS = stringPreferencesKey("slicers_json")
    }

    val slicers: Flow<List<SlicerProfile>> = context.slicerStore.data.map { prefs ->
        val raw = prefs[SLICERS] ?: "[]"
        Json.decodeFromString(raw)
    }

    suspend fun save(list: List<SlicerProfile>) {
        val json = Json.encodeToString(list)
        context.slicerStore.edit { it[SLICERS] = json }
    }
}

