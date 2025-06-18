package com.d4rk.cleaner.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.cleaner.app.main.utils.constants.NavigationRoutes
import com.d4rk.cleaner.core.utils.constants.datastore.AppDataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStore(val context : Context) : CommonDataStore(context = context) {

    fun getStartupPage() : Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(name = AppDataStoreConstants.DATA_STORE_STARTUP_PAGE)] ?: NavigationRoutes.ROUTE_HOME
        }
    }

    suspend fun saveStartupPage(startupPage : String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(name = AppDataStoreConstants.DATA_STORE_STARTUP_PAGE)] = startupPage
        }
    }

    fun getShowBottomBarLabels() : Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_SHOW_BOTTOM_BAR_LABELS)] != false
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

    suspend fun saveLastScanTimestamp(timestamp : Long) {
        dataStore.edit { preferences ->
            preferences[lastScanTimestampKey] = timestamp
            println("Cleaner for Android -> Saved timestamp: $timestamp")
        }
    }

    private val trashFileOriginalPathsKey = stringSetPreferencesKey("trash_file_original_paths")

    val trashFileOriginalPaths : Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[trashFileOriginalPathsKey] ?: emptySet()
    }

    suspend fun addTrashFileOriginalPath(originalPath : String) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFileOriginalPathsKey] ?: emptySet()
            settings[trashFileOriginalPathsKey] = currentPaths + originalPath
        }
    }

    suspend fun removeTrashFileOriginalPath(originalPath : String) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFileOriginalPathsKey] ?: emptySet()
            settings[trashFileOriginalPathsKey] = currentPaths - originalPath
        }
    }

    private val trashFilePathsKey = stringSetPreferencesKey("trash_file_paths")

    suspend fun addTrashFilePath(pathPair : Pair<String , String>) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFilePathsKey] ?: emptySet()
            settings[trashFilePathsKey] = currentPaths + "${pathPair.first}||${pathPair.second}"
        }
    }

    suspend fun removeTrashFilePath(originalPath : String) {
        dataStore.edit { settings ->
            val currentPaths = settings[trashFilePathsKey] ?: emptySet()
            val updatedPaths = currentPaths.filterNot { it.startsWith("$originalPath||") }.toSet()
            settings[trashFilePathsKey] = updatedPaths
        }
    }

    private val trashSizeKey = longPreferencesKey(name = "trash_size")
    val trashSize : Flow<Long> = dataStore.data.map { preferences ->
        preferences[trashSizeKey] ?: 0L
    }

    suspend fun addTrashSize(size : Long) {
        dataStore.edit { settings ->
            val currentSize = settings[trashSizeKey] ?: 0L
            settings[trashSizeKey] = currentSize + size
        }
    }

    suspend fun subtractTrashSize(size : Long) {
        dataStore.edit { settings ->
            val currentSize = settings[trashSizeKey] ?: 0L
            settings[trashSizeKey] = (currentSize - size).coerceAtLeast(0L)
        }
    }

    private val genericFilterKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_GENERIC_FILTER)
    val genericFilter : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[genericFilterKey] == true
    }

    suspend fun saveGenericFilter(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[genericFilterKey] = isChecked
        }
    }

    private val deleteEmptyFoldersKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_EMPTY_FOLDERS)
    val deleteEmptyFolders : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteEmptyFoldersKey] != false
    }

    suspend fun saveDeleteEmptyFolders(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteEmptyFoldersKey] = isChecked
        }
    }

    private val deleteArchivesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_ARCHIVES)
    val deleteArchives : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteArchivesKey] == true
    }

    suspend fun saveDeleteArchives(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteArchivesKey] = isChecked
        }
    }

    private val deleteInvalidMediaKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_INVALID_MEDIA)
    val deleteInvalidMedia : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteInvalidMediaKey] == true
    }

    suspend fun saveDeleteInvalidMedia(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteInvalidMediaKey] = isChecked
        }
    }

    private val deleteCorpseFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_CORPSE_FILES)
    val deleteCorpseFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteCorpseFilesKey] == true
    }

    suspend fun saveDeleteCorpseFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteCorpseFilesKey] = isChecked
        }
    }

    private val deleteApkFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_APK_FILES)
    val deleteApkFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteApkFilesKey] != false
    }

    suspend fun saveDeleteApkFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteApkFilesKey] = isChecked
        }
    }

    private val deleteAudioFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_AUDIO_FILES)
    val deleteAudioFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteAudioFilesKey] != false
    }

    suspend fun saveDeleteAudioFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteAudioFilesKey] = isChecked
        }
    }

    private val deleteVideoFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_VIDEO_FILES)
    val deleteVideoFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteVideoFilesKey] != false
    }

    suspend fun saveDeleteVideoFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteVideoFilesKey] = isChecked
        }
    }

    private val deleteOfficeFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_OFFICE_FILES)
    val deleteOfficeFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteOfficeFilesKey] != false
    }

    suspend fun saveDeleteOfficeFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteOfficeFilesKey] = isChecked
        }
    }

    private val deleteWindowsFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_WINDOWS_FILES)
    val deleteWindowsFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteWindowsFilesKey] != false
    }

    suspend fun saveDeleteWindowsFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteWindowsFilesKey] = isChecked
        }
    }

    private val deleteFontFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_FONT_FILES)
    val deleteFontFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteFontFilesKey] != false
    }

    suspend fun saveDeleteFontFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteFontFilesKey] = isChecked
        }
    }

    private val deleteOtherFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_OTHER_EXTENSIONS)
    val deleteOtherFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteOtherFilesKey] != false
    }

    suspend fun saveDeleteOtherFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteOtherFilesKey] = isChecked
        }
    }

    private val deleteImageFilesKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_IMAGE_FILES)
    val deleteImageFiles : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteImageFilesKey] != false
    }

    suspend fun saveDeleteImageFiles(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteImageFilesKey] = isChecked
        }
    }

    private val clipboardCleanKey = booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_CLIPBOARD_CLEAN)
    val clipboardClean : Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[clipboardCleanKey] == true
    }

    suspend fun saveClipboardClean(isChecked : Boolean) {
        dataStore.edit { preferences ->
            preferences[clipboardCleanKey] = isChecked
        }
    }

    private val storagePermissionGrantedKey = booleanPreferencesKey("permission_storage_granted")
    val storagePermissionGranted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[storagePermissionGrantedKey] == true
    }

    suspend fun saveStoragePermissionGranted(granted: Boolean) {
        dataStore.edit { prefs ->
            prefs[storagePermissionGrantedKey] = granted
        }
    }

    private val notificationsPermissionGrantedKey = booleanPreferencesKey("permission_notifications_granted")
    val notificationsPermissionGranted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[notificationsPermissionGrantedKey] == true
    }

    suspend fun saveNotificationsPermissionGranted(granted: Boolean) {
        dataStore.edit { prefs ->
            prefs[notificationsPermissionGrantedKey] = granted
        }
    }

    private val usagePermissionGrantedKey = booleanPreferencesKey("permission_usage_stats_granted")
    val usagePermissionGranted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[usagePermissionGrantedKey] == true
    }

    suspend fun saveUsagePermissionGranted(granted: Boolean) {
        dataStore.edit { prefs ->
            prefs[usagePermissionGrantedKey] = granted
        }
    }

    private val documentTreePermissionGrantedKey = booleanPreferencesKey("permission_document_tree_granted")
    val documentTreePermissionGranted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[documentTreePermissionGrantedKey] == true
    }

    private val documentTreeUriKey = stringPreferencesKey("document_tree_uri")
    val documentTreeUri: Flow<String?> = dataStore.data.map { prefs ->
        prefs[documentTreeUriKey]
    }

    suspend fun saveDocumentTreePermissionGranted(granted: Boolean) {
        dataStore.edit { prefs ->
            prefs[documentTreePermissionGrantedKey] = granted
        }
    }

    suspend fun saveDocumentTreeUri(uri: String) {
        dataStore.edit { prefs ->
            prefs[documentTreeUriKey] = uri
        }
    }
}