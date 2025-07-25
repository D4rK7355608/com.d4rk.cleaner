package com.d4rk.cleaner.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.cleaner.core.utils.constants.datastore.AppDataStoreConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStore(val context: Context) : CommonDataStore(context = context) {

    // Cleaning
    private val cleanedSpaceKey =
        longPreferencesKey(name = AppDataStoreConstants.DATA_STORE_CLEANED_SPACE)
    val cleanedSpace: Flow<Long> = dataStore.data.map { preferences ->
        preferences[cleanedSpaceKey] ?: 0L
    }

    suspend fun addCleanedSpace(space: Long) {
        dataStore.edit { preferences ->
            preferences[cleanedSpaceKey] = (preferences[cleanedSpaceKey] ?: 0L) + space
        }
    }

    private val lastScanTimestampKey =
        longPreferencesKey(name = AppDataStoreConstants.DATA_STORE_LAST_SCAN_TIMESTAMP)

    val lastScanTimestamp: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[lastScanTimestampKey]
    }

    suspend fun saveLastScanTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[lastScanTimestampKey] = timestamp
        }
    }

    private val lastCleanupNotificationShownKey =
        longPreferencesKey(name = AppDataStoreConstants.DATA_STORE_LAST_CLEANUP_NOTIFICATION_SHOWN)
    val lastCleanupNotificationShown: Flow<Long> = dataStore.data.map { prefs ->
        prefs[lastCleanupNotificationShownKey] ?: 0L
    }

    suspend fun saveLastCleanupNotificationShown(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[lastCleanupNotificationShownKey] = timestamp
        }
    }

    private val lastCleanupNotificationClickedKey =
        longPreferencesKey(name = AppDataStoreConstants.DATA_STORE_LAST_CLEANUP_NOTIFICATION_CLICKED)
    val lastCleanupNotificationClicked: Flow<Long> = dataStore.data.map { prefs ->
        prefs[lastCleanupNotificationClickedKey] ?: 0L
    }

    suspend fun saveLastCleanupNotificationClicked(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[lastCleanupNotificationClickedKey] = timestamp
        }
    }


    private val cleanupNotificationSnoozedUntilKey =
        longPreferencesKey(name = AppDataStoreConstants.DATA_STORE_CLEANUP_NOTIFICATION_SNOOZED_UNTIL)
    val cleanupNotificationSnoozedUntil: Flow<Long> = dataStore.data.map { prefs ->
        prefs[cleanupNotificationSnoozedUntilKey] ?: 0L
    }

    suspend fun saveCleanupNotificationSnoozedUntil(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[cleanupNotificationSnoozedUntilKey] = timestamp
        }
    }

    private val reminderFrequencyKey =
        intPreferencesKey(name = AppDataStoreConstants.DATA_STORE_CLEANUP_REMINDER_FREQUENCY_DAYS)
    val cleanupReminderFrequencyDays: Flow<Int> = dataStore.data.map { prefs ->
        prefs[reminderFrequencyKey] ?: 7
    }


    private val trashFileOriginalPathsKey =
        stringSetPreferencesKey(AppDataStoreConstants.DATA_STORE_TRASH_FILE_ORIGINAL_PATHS)

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


    private val trashSizeKey =
        longPreferencesKey(name = AppDataStoreConstants.DATA_STORE_TRASH_SIZE)
    val trashSize: Flow<Long> = dataStore.data.map { preferences ->
        preferences[trashSizeKey] ?: 0L
    }

    suspend fun addTrashSize(size: Long) {
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
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_GENERIC_FILTER)
    val genericFilter: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[genericFilterKey] == true
    }

    suspend fun saveGenericFilter(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[genericFilterKey] = isChecked
        }
    }

    private val deleteEmptyFoldersKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_EMPTY_FOLDERS)
    val deleteEmptyFolders: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteEmptyFoldersKey] != false
    }

    suspend fun saveDeleteEmptyFolders(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteEmptyFoldersKey] = isChecked
        }
    }

    private val deleteArchivesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_ARCHIVES)
    val deleteArchives: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteArchivesKey] == true
    }

    suspend fun saveDeleteArchives(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteArchivesKey] = isChecked
        }
    }

    private val deleteInvalidMediaKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_INVALID_MEDIA)
    val deleteInvalidMedia: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteInvalidMediaKey] == true
    }

    suspend fun saveDeleteInvalidMedia(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteInvalidMediaKey] = isChecked
        }
    }

    private val deleteCorpseFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_CORPSE_FILES)
    val deleteCorpseFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteCorpseFilesKey] == true
    }

    suspend fun saveDeleteCorpseFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteCorpseFilesKey] = isChecked
        }
    }

    private val deleteApkFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_APK_FILES)
    val deleteApkFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteApkFilesKey] != false
    }

    suspend fun saveDeleteApkFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteApkFilesKey] = isChecked
        }
    }

    private val deleteAudioFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_AUDIO_FILES)
    val deleteAudioFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteAudioFilesKey] != false
    }

    suspend fun saveDeleteAudioFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteAudioFilesKey] = isChecked
        }
    }

    private val deleteVideoFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_VIDEO_FILES)
    val deleteVideoFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteVideoFilesKey] != false
    }

    suspend fun saveDeleteVideoFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteVideoFilesKey] = isChecked
        }
    }

    private val deleteOfficeFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_OFFICE_FILES)
    val deleteOfficeFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteOfficeFilesKey] != false
    }

    suspend fun saveDeleteOfficeFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteOfficeFilesKey] = isChecked
        }
    }

    private val deleteWindowsFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_WINDOWS_FILES)
    val deleteWindowsFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteWindowsFilesKey] != false
    }

    suspend fun saveDeleteWindowsFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteWindowsFilesKey] = isChecked
        }
    }

    private val deleteFontFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_FONT_FILES)
    val deleteFontFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteFontFilesKey] != false
    }

    suspend fun saveDeleteFontFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteFontFilesKey] = isChecked
        }
    }

    private val deleteOtherFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_OTHER_EXTENSIONS)
    val deleteOtherFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteOtherFilesKey] != false
    }

    suspend fun saveDeleteOtherFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteOtherFilesKey] = isChecked
        }
    }

    private val deleteImageFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_IMAGE_FILES)
    val deleteImageFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteImageFilesKey] != false
    }

    suspend fun saveDeleteImageFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteImageFilesKey] = isChecked
        }
    }

    private val deleteDuplicateFilesKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DELETE_DUPLICATE_FILES)
    val deleteDuplicateFiles: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[deleteDuplicateFilesKey] == true
    }

    suspend fun saveDeleteDuplicateFiles(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[deleteDuplicateFilesKey] = isChecked
        }
    }

    private val deepDuplicateSearchKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_DEEP_DUPLICATE_SEARCH)
    val deepDuplicateSearch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[deepDuplicateSearchKey] == true
    }


    private val duplicateScanEnabledKey =
        booleanPreferencesKey(name = AppDataStoreConstants.DATA_STORE_ENABLE_DUPLICATE_SCAN)
    val duplicateScanEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[duplicateScanEnabledKey] ?: true
    }



    private val storagePermissionGrantedKey =
        booleanPreferencesKey(AppDataStoreConstants.DATA_STORE_PERMISSION_STORAGE_GRANTED)
    val storagePermissionGranted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[storagePermissionGrantedKey] == true
    }

    suspend fun saveStoragePermissionGranted(granted: Boolean) {
        dataStore.edit { prefs ->
            prefs[storagePermissionGrantedKey] = granted
        }
    }


    private val usagePermissionGrantedKey =
        booleanPreferencesKey(AppDataStoreConstants.DATA_STORE_PERMISSION_USAGE_STATS_GRANTED)
    val usagePermissionGranted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[usagePermissionGrantedKey] == true
    }

    suspend fun saveUsagePermissionGranted(granted: Boolean) {
        dataStore.edit { prefs ->
            prefs[usagePermissionGrantedKey] = granted
        }
    }

    private val whatsappGridViewKey =
        booleanPreferencesKey(AppDataStoreConstants.DATA_STORE_WHATSAPP_GRID_VIEW)
    val whatsappGridView: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[whatsappGridViewKey] ?: true
    }

    suspend fun saveWhatsAppGridView(isGrid: Boolean) {
        dataStore.edit { prefs ->
            prefs[whatsappGridViewKey] = isGrid
        }
    }

    private val streakCountKey =
        intPreferencesKey(name = AppDataStoreConstants.DATA_STORE_STREAK_COUNT)
    val streakCount: Flow<Int> = dataStore.data.map { prefs ->
        prefs[streakCountKey] ?: 0
    }

    suspend fun saveStreakCount(count: Int) {
        dataStore.edit { prefs ->
            prefs[streakCountKey] = count
        }
    }

    private val lastCleanDayKey =
        longPreferencesKey(name = AppDataStoreConstants.DATA_STORE_LAST_CLEAN_DAY)
    val lastCleanDay: Flow<Long> = dataStore.data.map { prefs ->
        prefs[lastCleanDayKey] ?: 0L
    }

    suspend fun saveLastCleanDay(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[lastCleanDayKey] = timestamp
        }
    }

    private val streakReminderEnabledKey =
        booleanPreferencesKey(AppDataStoreConstants.DATA_STORE_STREAK_REMINDER_ENABLED)
    val streakReminderEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[streakReminderEnabledKey] ?: false
    }

    suspend fun saveStreakReminderEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[streakReminderEnabledKey] = enabled
        }
    }

    suspend fun isStreakReminderInitialized(): Boolean {
        val prefs = dataStore.data.first()
        return prefs.contains(streakReminderEnabledKey)
    }

    private val showStreakCardKey =
        booleanPreferencesKey(AppDataStoreConstants.DATA_STORE_SHOW_STREAK_CARD)
    val showStreakCard: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[showStreakCardKey] ?: true
    }

    suspend fun saveShowStreakCard(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[showStreakCardKey] = show
        }
    }

    private val streakHideUntilKey =
        longPreferencesKey(AppDataStoreConstants.DATA_STORE_STREAK_HIDE_UNTIL)
    val streakHideUntil: Flow<Long> = dataStore.data.map { prefs ->
        prefs[streakHideUntilKey] ?: 0L
    }

    suspend fun saveStreakHideUntil(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[streakHideUntilKey] = timestamp
        }
    }

    private val autoCleanEnabledKey =
        booleanPreferencesKey(AppDataStoreConstants.DATA_STORE_AUTO_CLEAN_ENABLED)
    val autoCleanEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[autoCleanEnabledKey] ?: false
    }

    suspend fun saveAutoCleanEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[autoCleanEnabledKey] = enabled
        }
    }

    private val autoCleanFrequencyDaysKey =
        intPreferencesKey(AppDataStoreConstants.DATA_STORE_AUTO_CLEAN_FREQUENCY_DAYS)
    val autoCleanFrequencyDays: Flow<Int> = dataStore.data.map { prefs ->
        prefs[autoCleanFrequencyDaysKey] ?: 7
    }


}
