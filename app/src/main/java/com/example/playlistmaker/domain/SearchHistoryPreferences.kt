package com.example.playlistmaker.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val preferencesKey = stringPreferencesKey("search_history_key")

    fun addEntry(query: String) {
        if (query.isBlank()) return
        coroutineScope.launch {
            dataStore.edit { prefs ->
                val history = prefs[preferencesKey]?.split(",")?.toMutableList() ?: mutableListOf()
                history.remove(query)
                history.add(0, query)
                prefs[preferencesKey] = history.take(10).joinToString(",")
            }
        }
    }

    suspend fun getEntries(): List<String> {
        val prefs = dataStore.data.first()
        return prefs[preferencesKey]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }
}