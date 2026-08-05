package com.example.cnc3d.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.cnc3d.domain.models.Macro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.text.get

private val Context.macroStore by preferencesDataStore("macros")

class MacroStore(private val context: Context) {

    companion object {
        val MACROS = stringPreferencesKey("macros_json")
    }

    val macros: Flow<List<Macro>> = context.macroStore.data.map { prefs ->
        val raw = prefs[MACROS] ?: "[]"
        Json.decodeFromString(raw)
    }

    suspend fun save(list: List<Macro>) {
        val json = Json.encodeToString(list)
        context.macroStore.edit { it[MACROS] = json }
    }
}
