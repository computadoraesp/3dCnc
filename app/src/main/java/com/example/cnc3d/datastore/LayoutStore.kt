package com.example.cnc3d.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cnc3d.ui.layout.LayoutConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.layoutStore: DataStore<Preferences> by preferencesDataStore(name = "layout_prefs")

class LayoutStore(private val context: Context) {

    companion object {
        val LAYOUT = stringPreferencesKey("layout_json")
    }

    val layout: Flow<LayoutConfig?> = context.layoutStore.data.map { prefs ->
        prefs[LAYOUT]?.let { Json.decodeFromString(it) }
    }

    suspend fun save(config: LayoutConfig) {
        val json = Json.encodeToString(config)
        context.layoutStore.edit { it[LAYOUT] = json }
    }
}
