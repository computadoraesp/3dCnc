package com.example.cnc3d.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.cnc3d.domain.models.CloudProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.text.get

private val Context.cloudStore by preferencesDataStore("cloud")

class CloudStore(private val context: Context) {

    companion object {
        val CLOUD = stringPreferencesKey("cloud_json")
    }

    val profile: Flow<CloudProfile?> = context.cloudStore.data.map { prefs ->
        prefs[CLOUD]?.let { Json.decodeFromString(it) }
    }

    suspend fun save(profile: CloudProfile) {
        val json = Json.encodeToString(profile)
        context.cloudStore.edit { it[CLOUD] = json }
    }
}
