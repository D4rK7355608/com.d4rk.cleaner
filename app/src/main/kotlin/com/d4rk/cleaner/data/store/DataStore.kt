package com.d4rk.cleaner.data.store

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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

    // Cleaning
    private val genericFilterKey = booleanPreferencesKey("generic_filter")
    val genericFilter: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[genericFilterKey] ?: false
    }
    suspend fun saveGenericFilter(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[genericFilterKey] = isChecked
        }
    }
    private val aggressiveFilterKey = booleanPreferencesKey("aggressive_filter")
    val aggressiveFilter: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[aggressiveFilterKey] ?: false
    }
    suspend fun saveAggressiveFilter(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[aggressiveFilterKey] = isChecked
        }
    }
    private val deleteEmptyFoldersKey = booleanPreferencesKey("delete_empty_folders")
    val deleteEmptyFolders: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteEmptyFoldersKey] ?: true
    }
    suspend fun saveDeleteEmptyFolders(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteEmptyFoldersKey] = isChecked
        }
    }
    private val deleteArchivesKey = booleanPreferencesKey("delete_archives")
    val deleteArchives: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteArchivesKey] ?: false
    }
    suspend fun saveDeleteArchives(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteArchivesKey] = isChecked
        }
    }
    private val deleteInvalidMediaKey = booleanPreferencesKey("delete_invalid_media")
    val deleteInvalidMedia: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteInvalidMediaKey] ?: false
    }
    suspend fun saveDeleteInvalidMedia(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteInvalidMediaKey] = isChecked
        }
    }
    private val deleteCorpseFilesKey = booleanPreferencesKey("delete_corpse_files")
    val deleteCorpseFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteCorpseFilesKey] ?: false
    }
    suspend fun saveDeleteCorpseFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteCorpseFilesKey] = isChecked
        }
    }
    private val deleteApkFilesKey = booleanPreferencesKey("delete_apk_files")
    val deleteApkFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteApkFilesKey] ?: true
    }
    suspend fun saveDeleteApkFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteApkFilesKey] = isChecked
        }
    }
    private val doubleCheckerKey = booleanPreferencesKey("double_checker")
    val doubleChecker: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[doubleCheckerKey] ?: false
    }
    suspend fun saveDoubleChecker(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[doubleCheckerKey] = isChecked
        }
    }
    private val clipboardCleanKey = booleanPreferencesKey("clipboard_clean")
    val clipboardClean: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[clipboardCleanKey] ?: false
    }
    suspend fun saveClipboardClean(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[clipboardCleanKey] = isChecked
        }
    }
    private val autoWhitelistKey = booleanPreferencesKey("auto_whitelist")
    val autoWhitelist: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[autoWhitelistKey] ?: true
    }
    suspend fun saveAutoWhitelist(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[autoWhitelistKey] = isChecked
        }
    }
    private val oneClickCleanKey = booleanPreferencesKey("one_click_clean")
    val oneClickClean: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[oneClickCleanKey] ?: false
    }
    suspend fun saveOneClickClean(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[oneClickCleanKey] = isChecked
        }
    }





    private val dailyCleanerKey = booleanPreferencesKey("daily_clean")
    val dailyCleaner: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[dailyCleanerKey] ?: false
    }
    suspend fun saveDailyCleaner(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[dailyCleanerKey] = isChecked
        }
    }

    // Theme mode
    val themeModeState = mutableStateOf("follow_system")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[themeModeKey] ?: "follow_system"
    }
    suspend fun saveThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[themeModeKey] = mode
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

    // AMOLED mode
    private val amoledModeKey = booleanPreferencesKey("amoled_mode")
    val amoledMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[amoledModeKey] ?: false
    }
    suspend fun saveAmoledMode(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[amoledModeKey] = isChecked
        }
    }

    // Dynamic colors
    private val dynamicColorsKey = booleanPreferencesKey("dynamic_colors")
    val dynamicColors: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[dynamicColorsKey] ?: true
    }
    suspend fun saveDynamicColors(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[dynamicColorsKey] = isChecked
        }
    }
}