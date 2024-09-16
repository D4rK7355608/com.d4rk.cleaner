package com.d4rk.cleaner.data.datastore

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.d4rk.cleaner.constants.ui.bottombar.BottomBarRoutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class DataStore(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        @Volatile
        private var instance: DataStore? = null

        fun getInstance(context: Context): DataStore {
            return instance ?: synchronized(lock = this) {
                instance ?: DataStore(context).also { instance = it }
            }
        }
    }

    // Last used app notifications
    private val lastUsedKey = longPreferencesKey(name = "last_used")
    val lastUsed: Flow<Long> = dataStore.data.map { preferences ->
        preferences[lastUsedKey] ?: 0
    }

    suspend fun saveLastUsed(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[lastUsedKey] = timestamp
        }
    }

    // Startup
    private val startupKey = booleanPreferencesKey(name = "value")
    val startup: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[startupKey] ?: true
    }

    suspend fun saveStartup(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[startupKey] = isFirstTime
        }
    }

    // Display
    val themeModeState = mutableStateOf(value = "follow_system")
    private val themeModeKey = stringPreferencesKey(name = "theme_mode")
    val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[themeModeKey] ?: "follow_system"
    }

    suspend fun saveThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[themeModeKey] = mode
        }
    }

    private val amoledModeKey = booleanPreferencesKey(name = "amoled_mode")
    val amoledMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[amoledModeKey] ?: false
    }

    suspend fun saveAmoledMode(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[amoledModeKey] = isChecked
        }
    }

    private val dynamicColorsKey = booleanPreferencesKey(name = "dynamic_colors")
    val dynamicColors: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[dynamicColorsKey] ?: true
    }

    suspend fun saveDynamicColors(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[dynamicColorsKey] = isChecked
        }
    }

    private val bouncyButtonsKey = booleanPreferencesKey(name = "bouncy_buttons")
    val bouncyButtons: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[bouncyButtonsKey] ?: true
    }

    suspend fun saveBouncyButtons(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[bouncyButtonsKey] = isChecked
        }
    }

    fun getStartupPage(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(name = "startup_page")] ?: BottomBarRoutes.HOME
        }
    }

    suspend fun saveStartupPage(startupPage: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(name = "startup_page")] = startupPage
        }
    }

    fun getShowBottomBarLabels(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(name = "show_bottom_bar_labels")] ?: true
        }
    }

    private val languageKey = stringPreferencesKey(name = "language")

    fun getLanguage(): Flow<String> = dataStore.data.map { preferences ->
        preferences[languageKey] ?: "en"
    }

    suspend fun saveLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }

    // Cleaning
    private val genericFilterKey = booleanPreferencesKey(name = "generic_filter")
    val genericFilter: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[genericFilterKey] ?: false
    }

    suspend fun saveGenericFilter(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[genericFilterKey] = isChecked
        }
    }

    private val deleteEmptyFoldersKey = booleanPreferencesKey(name = "delete_empty_folders")
    val deleteEmptyFolders: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteEmptyFoldersKey] ?: true
    }

    suspend fun saveDeleteEmptyFolders(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteEmptyFoldersKey] = isChecked
        }
    }

    private val deleteArchivesKey = booleanPreferencesKey(name = "delete_archives")
    val deleteArchives: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteArchivesKey] ?: false
    }

    suspend fun saveDeleteArchives(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteArchivesKey] = isChecked
        }
    }

    private val deleteInvalidMediaKey = booleanPreferencesKey(name = "delete_invalid_media")
    val deleteInvalidMedia: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteInvalidMediaKey] ?: false
    }

    suspend fun saveDeleteInvalidMedia(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteInvalidMediaKey] = isChecked
        }
    }

    private val deleteCorpseFilesKey = booleanPreferencesKey(name = "delete_corpse_files")
    val deleteCorpseFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteCorpseFilesKey] ?: false
    }

    suspend fun saveDeleteCorpseFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteCorpseFilesKey] = isChecked
        }
    }

    private val deleteApkFilesKey = booleanPreferencesKey(name = "delete_apk_files")
    val deleteApkFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteApkFilesKey] ?: true
    }

    suspend fun saveDeleteApkFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteApkFilesKey] = isChecked
        }
    }

    private val deleteAudioFilesKey = booleanPreferencesKey(name = "delete_audio_files")
    val deleteAudioFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteAudioFilesKey] ?: true
    }

    suspend fun saveDeleteAudioFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteAudioFilesKey] = isChecked
        }
    }

    private val deleteVideoFilesKey = booleanPreferencesKey(name = "delete_video_files")
    val deleteVideoFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteVideoFilesKey] ?: true
    }

    suspend fun saveDeleteVideoFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteVideoFilesKey] = isChecked
        }
    }

    private val deleteImageFilesKey = booleanPreferencesKey(name = "delete_image_files")
    val deleteImageFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteImageFilesKey] ?: true
    }

    suspend fun saveDeleteImageFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteImageFilesKey] = isChecked
        }
    }

    private val clipboardCleanKey = booleanPreferencesKey(name = "clipboard_clean")
    val clipboardClean: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[clipboardCleanKey] ?: false
    }

    suspend fun saveClipboardClean(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[clipboardCleanKey] = isChecked
        }
    }

    // Usage and Diagnostics
    private val usageAndDiagnosticsKey = booleanPreferencesKey(name = "usage_and_diagnostics")
    val usageAndDiagnostics: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[usageAndDiagnosticsKey] ?: true
    }

    suspend fun saveUsageAndDiagnostics(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[usageAndDiagnosticsKey] = isChecked
        }
    }

    // Ads
    private val adsKey = booleanPreferencesKey(name = "ads")
    val ads: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[adsKey] ?: true
    }

    suspend fun saveAds(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[adsKey] = isChecked
        }
    }
}