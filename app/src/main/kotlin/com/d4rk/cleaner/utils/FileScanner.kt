package com.d4rk.cleaner.utils

import android.content.res.Resources
import com.d4rk.cleaner.data.store.DataStore
import kotlinx.coroutines.flow.first
import java.io.File
import android.os.Environment
import android.util.Log
import com.d4rk.cleaner.R

/**
 * Utility class for scanning and filtering files based on specified preferences.
 *
 * This class scans and filters files based on preferences such as aggressive filtering, generic filtering,
 * and other criteria defined by the provided resources.
 *
 * @property dataStore DataStore instance for accessing preferences.
 * @property resources Resources instance for accessing string arrays.
 */
class FileScanner(private val dataStore : DataStore , private val resources : Resources) {

    private var preferences : Map<String , Boolean> = emptyMap()
    private var filteredFiles : List<File> = emptyList()

    /**
     * Initiates file scanning process.
     *
     * This function loads preferences, retrieves all files, applies filters, and logs the result.
     */
    suspend fun startScanning() {
        loadPreferences()
        val allFiles = getAllFiles()
        filteredFiles = filterFiles(allFiles)
        Log.d("FileScanner" , "Scanning completed. Found ${filteredFiles.size} files.")
    }

    /**
     * Loads preferences from the data store into the [preferences] map.
     */
    private suspend fun loadPreferences() {
        preferences = mapOf(
            "generic_filter" to dataStore.genericFilter.first() ,
            "aggressive_filter" to dataStore.aggressiveFilter.first() ,
            "delete_empty_folders" to dataStore.deleteEmptyFolders.first() ,
            "delete_archives" to dataStore.deleteArchives.first() ,
            "delete_invalid_media" to dataStore.deleteInvalidMedia.first() ,
            "delete_corpse_files" to dataStore.deleteCorpseFiles.first() ,
            "delete_apk_files" to dataStore.deleteApkFiles.first() ,
            "double_checker" to dataStore.doubleChecker.first() ,
            "clipboard_clean" to dataStore.clipboardClean.first() ,
            "auto_whitelist" to dataStore.autoWhitelist.first() ,
            "one_click_clean" to dataStore.oneClickClean.first() ,
            "daily_clean" to dataStore.dailyCleaner.first()
        )
    }

    /**
     * Retrieves all files from the external storage directory recursively.
     *
     * @return List of all files found in the external storage.
     */
    private fun getAllFiles() : List<File> {
        val files = mutableListOf<File>()
        val stack = ArrayDeque<File>()
        val root = Environment.getExternalStorageDirectory()
        stack.addFirst(root)

        while (stack.isNotEmpty()) {
            val file = stack.removeFirst()
            if (file.isDirectory) {
                file.listFiles()?.let { stack.addAll(it) }
            }
            else {
                files.add(file)
            }
        }

        Log.d("FileScanner" , "Found ${files.size} files")

        return files
    }

    /**
     * Filters files based on defined preferences.
     *
     * @param allFiles List of all files to filter.
     * @return List of files filtered based on preferences.
     */
    private fun filterFiles(allFiles : List<File>) : List<File> {
        return allFiles.filter { file ->
            preferences.any { (key , value) ->
                val result = when (key) {
                    "aggressive_filter" -> {
                        val extensions = resources.getStringArray(R.array.aggressive_filter_files)
                        Log.d("FileScanner", "Aggressive filter extensions: ${extensions.joinToString()}")
                        value && extensions.map { it.removePrefix(".") }.contains(file.extension)
                    }
                    "generic_filter" -> {
                        val extensions = resources.getStringArray(R.array.generic_filter_files)
                        Log.d("FileScanner", "Generic filter extensions: ${extensions.joinToString()}")
                        value && extensions.map { it.removePrefix(".") }.contains(file.extension)
                    }
                    "archive_filter_files" -> {
                        val extensions = resources.getStringArray(R.array.archive_filter_files)
                        Log.d("FileScanner", "Archive filter extensions: ${extensions.joinToString()}")
                        value && extensions.map { it.removePrefix(".") }.contains(file.extension)
                    }
                    else -> false
                }
                if (result) {
                    Log.d("FileScanner", "File: ${file.name}, Key: $key, Value: $value")
                }
                result
            }
        }
    }


    fun getFilteredFiles(): List<File> {
        Log.d("FileScanner", "Filtered Files: $filteredFiles")
        return filteredFiles
    }

}