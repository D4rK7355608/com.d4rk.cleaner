package com.d4rk.cleaner.utils

import android.content.res.Resources
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.store.DataStore
import kotlinx.coroutines.flow.first
import java.io.File

/**
 * Utility class for scanning and filtering files based on specified preferences.
 *
 * This class scans and filters files based on preferences such as aggressive filtering, generic filtering,
 * and other criteria defined by the provided resources.
 *
 * @property dataStore DataStore instance for accessing preferences.
 * @property resources Resources instance for accessing string arrays.
 */
class FileScanner(private val dataStore: DataStore, private val resources: Resources) {

    private var preferences: Map<String, Boolean> = emptyMap()
    private var filteredFiles: List<File> = emptyList()

    /**
     * Initiates file scanning process.
     *
     * This function loads preferences, retrieves all files, applies filters, and logs the result.
     */
    suspend fun startScanning() {
        loadPreferences()
        val allFiles = getAllFiles()
        filteredFiles = filterFiles(allFiles).toList()
    }

    /**
     * Loads preferences from the data store into the [preferences] map.
     */
    private suspend fun loadPreferences() {
        preferences = mapOf(
            "generic_extensions" to dataStore.genericFilter.first(),
            // "delete_empty_folders" to dataStore.deleteEmptyFolders.first() ,
            "archive_extensions" to dataStore.deleteArchives.first(),
            // "delete_invalid_media" to dataStore.deleteInvalidMedia.first() ,
            // "delete_corpse_files" to dataStore.deleteCorpseFiles.first() ,
            "apk_extensions" to dataStore.deleteApkFiles.first(),
            "audio_extensions" to dataStore.deleteAudioFiles.first(),
            "video_extensions" to dataStore.deleteVideoFiles.first(),
            //"double_checker" to dataStore.doubleChecker.first() ,
            //"clipboard_clean" to dataStore.clipboardClean.first() ,
            // "auto_whitelist" to dataStore.autoWhitelist.first() ,
            //"one_click_clean" to dataStore.oneClickClean.first() ,
            // "daily_clean" to dataStore.dailyCleaner.first()
        )
    }

    /**
     * Retrieves all files from the external storage directory recursively.
     *
     * @return List of all files found in the external storage.
     */
    private fun getAllFiles(): List<File> {
        val files = mutableListOf<File>()
        val stack = ArrayDeque<File>()
        val root = Environment.getExternalStorageDirectory()
        stack.addFirst(root)

        while (stack.isNotEmpty()) {
            val file = stack.removeFirst()
            if (file.isDirectory) {
                file.listFiles()?.let { stack.addAll(it) }
            } else {
                files.add(file)
            }
        }

        return files
    }

    /**
     * Filters files based on defined preferences.
     *
     * @param allFiles List of all files to filter.
     * @return List of files filtered based on preferences.
     */
    private fun filterFiles(allFiles: List<File>): Sequence<File> {
        return sequence {
            for (file in allFiles) {
                if (preferences.any { (key, value) ->
                        val result = when (key) {
                            "generic_extensions" -> {
                                val extensions =
                                    resources.getStringArray(R.array.generic_extensions)
                                value && extensions.map { it.removePrefix(".") }
                                    .contains(file.extension)
                            }

                            "archive_extensions" -> {
                                val extensions =
                                    resources.getStringArray(R.array.archive_extensions)
                                value && extensions.contains(file.extension)
                            }

                            "apk_extensions" -> {
                                val extensions = resources.getStringArray(R.array.apk_extensions)
                                value && extensions.contains(file.extension)
                            }

                            "audio_extensions" -> {
                                val extensions = resources.getStringArray(R.array.audio_extensions)
                                value && extensions.contains(file.extension)
                            }

                            "video_extensions" -> {
                                val extensions = resources.getStringArray(R.array.video_extensions)
                                value && extensions.contains(file.extension)
                            }

                            else -> false
                        }
                        result
                    }) {
                    yield(file)
                }
            }
        }
    }

    fun getFilteredFiles(): List<File> {
        return filteredFiles
    }
}