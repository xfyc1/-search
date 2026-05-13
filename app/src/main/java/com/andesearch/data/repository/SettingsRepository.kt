package com.andesearch.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val darkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DARK_THEME] ?: false
    }

    val excludedDirs: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[EXCLUDED_DIRS] ?: emptySet()
    }

    val autoStartIndex: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AUTO_START_INDEX] ?: true
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[DARK_THEME] = enabled }
    }

    suspend fun addExcludedDir(dir: String) {
        dataStore.edit { prefs ->
            val current = prefs[EXCLUDED_DIRS] ?: emptySet()
            prefs[EXCLUDED_DIRS] = current + dir
        }
    }

    suspend fun removeExcludedDir(dir: String) {
        dataStore.edit { prefs ->
            val current = prefs[EXCLUDED_DIRS] ?: emptySet()
            prefs[EXCLUDED_DIRS] = current - dir
        }
    }

    companion object {
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val EXCLUDED_DIRS = stringSetPreferencesKey("excluded_dirs")
        private val AUTO_START_INDEX = booleanPreferencesKey("auto_start_index")
    }
}
