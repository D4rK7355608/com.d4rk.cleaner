package com.d4rk.cleaner.data.store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

class DataStore(context: Context) {
    private val dataStore = context.dataStore

    // Usage and Diagnostics
    private val usageAndDiagnosticsKey = booleanPreferencesKey("usage_and_diagnostics")
    val usageAndDiagnostics: Flow<Boolean> = dataStore.data.map { preferences -> // FIXME: Property "usageAndDiagnostics" is never used
        preferences[usageAndDiagnosticsKey] ?: true
    }
    suspend fun saveUsageAndDiagnostics(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[usageAndDiagnosticsKey] = isChecked
        }
    }

    // Dark mode
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[darkModeKey] ?: false
    }
    suspend fun saveDarkMode(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[darkModeKey] = isChecked
        }
    }
}