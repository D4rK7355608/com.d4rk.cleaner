package com.d4rk.cleaner.core.utils.constants.datastore

import com.d4rk.android.libs.apptoolkit.core.utils.constants.datastore.DataStoreNamesConstants

object AppDataStoreConstants : DataStoreNamesConstants() {
    const val DATA_STORE_CLEANED_SPACE = "cleaned_space"
    const val DATA_STORE_LAST_SCAN_TIMESTAMP = "last_scan_timestamp"
    const val DATA_STORE_TRASH_FILE_ORIGINAL_PATHS = "trash_file_original_paths"
    const val DATA_STORE_TRASH_SIZE = "trash_size"
    const val DATA_STORE_LAST_CLEANUP_NOTIFICATION_SHOWN = "last_cleanup_notif_shown"
    const val DATA_STORE_LAST_CLEANUP_NOTIFICATION_CLICKED = "last_cleanup_notif_clicked"
    const val DATA_STORE_LAST_CLEANUP_NOTIFICATION_DISMISSED = "last_cleanup_notif_dismissed"
    const val DATA_STORE_CLEANUP_REMINDER_FREQUENCY_DAYS = "cleanup_reminder_frequency_days"
    const val DATA_STORE_PERMISSION_STORAGE_GRANTED = "permission_storage_granted"
    const val DATA_STORE_PERMISSION_USAGE_STATS_GRANTED = "permission_usage_stats_granted"
    const val DATA_STORE_STARTUP_PAGE = "startup_page"
    const val DATA_STORE_SHOW_BOTTOM_BAR_LABELS = "show_bottom_bar_labels"
    const val DATA_STORE_GENERIC_FILTER = "generic_filter"
    const val DATA_STORE_DELETE_EMPTY_FOLDERS = "delete_empty_folders"
    const val DATA_STORE_DELETE_ARCHIVES = "delete_archives"
    const val DATA_STORE_DELETE_INVALID_MEDIA = "delete_invalid_media"
    const val DATA_STORE_DELETE_CORPSE_FILES = "delete_corpse_files"
    const val DATA_STORE_DELETE_APK_FILES = "delete_apk_files"
    const val DATA_STORE_DELETE_AUDIO_FILES = "delete_audio_files"
    const val DATA_STORE_DELETE_VIDEO_FILES = "delete_video_files"
    const val DATA_STORE_DELETE_IMAGE_FILES = "delete_image_files"
    const val DATA_STORE_DELETE_DUPLICATE_FILES = "delete_duplicate_files"
    const val DATA_STORE_DELETE_OFFICE_FILES = "delete_office_files"
    const val DATA_STORE_DELETE_WINDOWS_FILES = "delete_windows_files"
    const val DATA_STORE_DELETE_FONT_FILES = "delete_font_files"
    const val DATA_STORE_OTHER_EXTENSIONS = "other_extensions"
    const val DATA_STORE_CLIPBOARD_CLEAN = "clipboard_clean"
}
