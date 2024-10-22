package com.d4rk.cleaner.data.datastore

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.constants.datastore.DataStoreNamesConstants
import com.d4rk.cleaner.constants.ui.bottombar.BottomBarRoutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = DataStoreNamesConstants.DATA_STORE_SETTINGS)

class DataStore(context : Context) {
    private val dataStore = context.dataStore

    companion object {
        @Volatile
        private var instance : DataStore? = null

        fun getInstance(context : Context) : DataStore {
            return instance ?: synchronized(lock = this) {
                instance ?: DataStore(context).also { instance = it }
            }
        }
    }

    // Last used app notifications
    private val lastUsedKey =
            longPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_LAST_USED)
    val lastUsed : Flow<Long> = dataStore.data.map { preferences ->
        preferences[lastUsedKey] ?: 0
    }

    suspend fun saveLastUsed(timestamp : Long) {
        dataStore.edit { preferences ->
            preferences[lastUsedKey] = timestamp
        }
    }

    // Startup
    private val startupKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_STARTUP)
    val startup : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[startupKey] != false
    }

    suspend fun saveStartup(isFirstTime : Boolean) {
        dataStore.edit { preferences ->
            preferences[startupKey] = isFirstTime
        }
    }

    // Display
    val themeModeState = mutableStateOf(value = "follow_system")
    private val themeModeKey =
            stringPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_THEME_MODE)
    val themeMode : Flow<String> = dataStore.data.map { preferences ->
        preferences[themeModeKey] ?: "follow_system"
    }

    suspend fun saveThemeMode(mode : String) {
        dataStore.edit { preferences ->
            preferences[themeModeKey] = mode
        }
    }

    private val amoledModeKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_AMOLED_MODE)
    val amoledMode : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[amoledModeKey] == true
    }

    suspend fun saveAmoledMode(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[amoledModeKey] = isChecked
        }
    }

    private val dynamicColorsKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DYNAMIC_COLORS)
    val dynamicColors : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[dynamicColorsKey] != false
    }

    suspend fun saveDynamicColors(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[dynamicColorsKey] = isChecked
        }
    }

    private val bouncyButtonsKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_BOUNCY_BUTTONS)
    val bouncyButtons : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[bouncyButtonsKey] != false
    }

    suspend fun saveBouncyButtons(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[bouncyButtonsKey] = isChecked
        }
    }

    fun getStartupPage() : Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_STARTUP_PAGE)]
                ?: BottomBarRoutes.HOME
        }
    }

    suspend fun saveStartupPage(startupPage : String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_STARTUP_PAGE)] =
                    startupPage
        }
    }

    fun getShowBottomBarLabels() : Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_SHOW_BOTTOM_BAR_LABELS)] != false
        }
    }

    private val languageKey =
            stringPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_LANGUAGE)

    fun getLanguage() : Flow<String> = dataStore.data.map { preferences ->
        preferences[languageKey] ?: "en"
    }

    suspend fun saveLanguage(language : String) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }

    // Cleaning
    private val cleanedSpaceKey = longPreferencesKey(name = "cleaned_space")
    val cleanedSpace : Flow<Long> = dataStore.data.map { preferences ->
        preferences[cleanedSpaceKey] ?: 0L
    }

    suspend fun addCleanedSpace(space : Long) {
        dataStore.edit { preferences ->
            preferences[cleanedSpaceKey] = (preferences[cleanedSpaceKey] ?: 0L) + space
        }
    }

    private val lastScanTimestampKey = longPreferencesKey(name = "last_scan_timestamp")
    val lastScanTimestamp : Flow<Long> = dataStore.data.map { preferences ->
        preferences[lastScanTimestampKey] ?: 0L
    }

    suspend fun saveLastScanTimestamp(timestamp : Long) {
        dataStore.edit { preferences ->
            preferences[lastScanTimestampKey] = timestamp
        }
    }

    private val trashFileOriginalPathsKey = stringSetPreferencesKey("trash_file_original_paths")

    val trashFileOriginalPaths: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[trashFileOriginalPathsKey] ?: emptySet()
    }

    suspend fun addTrashFileOriginalPath(originalPath: String) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFileOriginalPathsKey] ?: emptySet()
            settings[trashFileOriginalPathsKey] = currentPaths + originalPath
        }
    }

    suspend fun removeTrashFileOriginalPath(originalPath: String) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFileOriginalPathsKey] ?: emptySet()
            settings[trashFileOriginalPathsKey] = currentPaths - originalPath
        }
    }

    private val trashFilePathsKey = stringSetPreferencesKey("trash_file_paths")

    val trashFilePaths: Flow<Set<Pair<String, String>>> = dataStore.data.map { preferences ->
        preferences[trashFilePathsKey]?.mapNotNull { entry ->
            val parts = entry.split("||")
            if (parts.size == 2) {
                Pair(parts[0] , parts[1])
            }
            else {
                println("Cleaner for Android -> Invalid entry in trashFilePaths: $entry. It should contain the '||' delimiter.")
                null
            }
        }?.toSet() ?: emptySet()

    }

    suspend fun addTrashFilePath(pathPair: Pair<String, String>) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFilePathsKey] ?: emptySet()
            settings[trashFilePathsKey] = currentPaths + "${pathPair.first}||${pathPair.second}"
        }
    }

    suspend fun removeTrashFilePath(originalPath: String) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFilePathsKey] ?: emptySet()
            val updatedPaths = currentPaths.filterNot { it.startsWith("$originalPath||") }.toSet()
            settings[trashFilePathsKey] = updatedPaths
        }
    }

    private val trashSizeKey = longPreferencesKey(name = "trash_size")
    val trashSize: Flow<Long> = dataStore.data.map { preferences ->
        preferences[trashSizeKey] ?: 0L
    }

    suspend fun addTrashSize(size : Long) {
        dataStore.edit { settings ->
            val currentSize = settings[trashSizeKey] ?: 0L
            settings[trashSizeKey] = currentSize + size
        }
    }

    suspend fun subtractTrashSize(size: Long) {
        dataStore.edit { settings ->
            val currentSize = settings[trashSizeKey] ?: 0L
            settings[trashSizeKey] = (currentSize - size).coerceAtLeast(0L)
        }
    }

    private val genericFilterKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_GENERIC_FILTER)
    val genericFilter : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[genericFilterKey] == true
    }

    suspend fun saveGenericFilter(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[genericFilterKey] = isChecked
        }
    }

    private val deleteEmptyFoldersKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_EMPTY_FOLDERS)
    val deleteEmptyFolders : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteEmptyFoldersKey] != false
    }

    suspend fun saveDeleteEmptyFolders(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteEmptyFoldersKey] = isChecked
        }
    }

    private val deleteArchivesKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_ARCHIVES)
    val deleteArchives : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteArchivesKey] == true
    }

    suspend fun saveDeleteArchives(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteArchivesKey] = isChecked
        }
    }

    private val deleteInvalidMediaKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_INVALID_MEDIA)
    val deleteInvalidMedia : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteInvalidMediaKey] == true
    }

    suspend fun saveDeleteInvalidMedia(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteInvalidMediaKey] = isChecked
        }
    }

    private val deleteCorpseFilesKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_CORPSE_FILES)
    val deleteCorpseFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteCorpseFilesKey] == true
    }

    suspend fun saveDeleteCorpseFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteCorpseFilesKey] = isChecked
        }
    }

    private val deleteApkFilesKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_APK_FILES)
    val deleteApkFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteApkFilesKey] != false
    }

    suspend fun saveDeleteApkFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteApkFilesKey] = isChecked
        }
    }

    private val deleteAudioFilesKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_AUDIO_FILES)
    val deleteAudioFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteAudioFilesKey] != false
    }

    suspend fun saveDeleteAudioFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteAudioFilesKey] = isChecked
        }
    }

    private val deleteVideoFilesKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_VIDEO_FILES)
    val deleteVideoFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteVideoFilesKey] != false
    }

    suspend fun saveDeleteVideoFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteVideoFilesKey] = isChecked
        }
    }

    private val deleteImageFilesKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_DELETE_IMAGE_FILES)
    val deleteImageFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteImageFilesKey] != false
    }

    suspend fun saveDeleteImageFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteImageFilesKey] = isChecked
        }
    }

    private val clipboardCleanKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_CLIPBOARD_CLEAN)
    val clipboardClean : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[clipboardCleanKey] == true
    }

    suspend fun saveClipboardClean(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[clipboardCleanKey] = isChecked
        }
    }

    // Usage and Diagnostics
    private val usageAndDiagnosticsKey =
            booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_USAGE_AND_DIAGNOSTICS)
    val usageAndDiagnostics : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[usageAndDiagnosticsKey] ?: ! BuildConfig.DEBUG
    }

    suspend fun saveUsageAndDiagnostics(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[usageAndDiagnosticsKey] = isChecked
        }
    }

    // Ads
    private val adsKey = booleanPreferencesKey(name = DataStoreNamesConstants.DATA_STORE_ADS)
    val ads : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[adsKey] ?: ! BuildConfig.DEBUG
    }

    suspend fun saveAds(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[adsKey] = isChecked
        }
    }
}