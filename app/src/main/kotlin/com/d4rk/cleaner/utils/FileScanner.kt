package com.d4rk.cleaner.utils

import android.content.res.Resources
import com.d4rk.cleaner.data.store.DataStore
import kotlinx.coroutines.flow.first
import java.io.File
import android.os.Environment
import android.util.Log
import com.d4rk.cleaner.R

class FileScanner(private val dataStore : DataStore , private val resources : Resources) {

    private var preferences : Map<String , Boolean> = emptyMap()
    private var filteredFiles : List<File> = emptyList()

    suspend fun startScanning() {
        loadPreferences()
        val allFiles = getAllFiles()
        filteredFiles = filterFiles(allFiles)
        Log.d("FileScanner" , "Scanning completed. Found ${filteredFiles.size} files.")
    }

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

        Log.d("FileScanner", "Found ${files.size} files")

        return files
    }

    private fun filterFiles(allFiles : List<File>) : List<File> {
        return allFiles.filter { file ->
            preferences.any { (key , value) ->
                when (key) {
                    "aggressive_filter" -> value && resources
                            .getStringArray(R.array.aggressive_filter_files)
                            .contains(file.extension)

                    "generic_filter" -> value && resources
                            .getStringArray(R.array.generic_filter_files).contains(file.extension)

                    "archive_filter_files" -> value && resources
                            .getStringArray(R.array.archive_filter_files).contains(file.extension)

                    else -> false
                }
            }
        }
    }
}