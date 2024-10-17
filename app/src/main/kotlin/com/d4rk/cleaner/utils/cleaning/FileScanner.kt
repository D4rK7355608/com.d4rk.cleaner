package com.d4rk.cleaner.utils.cleaning

import android.app.Application
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.cleaning.ExtensionsConstants
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.flow.first
import java.io.File

/**
 * A utility class for scanning and filtering files based on user-defined preferences.
 * This class scans files in the external storage directory, excluding a designated "Trash" directory.
 * It filters files based on user preferences for various file types (generic, archive, APK, audio, video, image, and empty folders).
 * Preferences are retrieved from a DataStore instance, and file type extensions are defined in string arrays within the application's resources.
 *
 * @param dataStore The DataStore instance for accessing user preferences.
 * @param application The application instance for accessing resources and external files directory.
 * @author Mihai-Cristian Condrea
 */
class FileScanner(private val dataStore : DataStore , private val application : Application) {

    private var preferences : Map<String , Boolean> = emptyMap()
    private var filteredFiles : List<File> = emptyList()

    /**
     * Starts the file scanning process.
     * Loads user preferences, retrieves all files from external storage (excluding the "Trash" directory),
     * and filters the files based on the loaded preferences.
     */
    suspend fun startScanning() {
        println("Cleaner for Android -> startScanning() called")
        loadPreferences()
        val (files , emptyFolders) = getAllFiles()
        filteredFiles = files
        println("Cleaner for Android -> startScanning() completed, filteredFiles size: ${filteredFiles.size}, emptyFolders size: ${emptyFolders.size}")
    }

    /**
     * Loads user preferences from the DataStore.
     * Retrieves user preferences for each file type filter and stores them in a map.
     */
    private suspend fun loadPreferences() {
        println("Cleaner for Android -> loadPreferences() called")
        preferences = with(ExtensionsConstants) {
            mapOf(
                GENERIC_EXTENSIONS to dataStore.genericFilter.first() ,
                ARCHIVE_EXTENSIONS to dataStore.deleteArchives.first() ,
                APK_EXTENSIONS to dataStore.deleteApkFiles.first() ,
                IMAGE_EXTENSIONS to dataStore.deleteImageFiles.first() ,
                AUDIO_EXTENSIONS to dataStore.deleteAudioFiles.first() ,
                VIDEO_EXTENSIONS to dataStore.deleteVideoFiles.first() ,
                EMPTY_FOLDERS to dataStore.deleteEmptyFolders.first()
            )
        }
        println("Cleaner for Android -> loadPreferences() completed, preferences: $preferences")
    }

    /**
     * Retrieves all files from the external storage directory, excluding the "Trash" directory.
     * Filters files and empty folders based on user preferences.
     * @return A Pair containing a list of filtered files and a list of empty folders.
     */
    fun getAllFiles() : Pair<List<File> , List<File>> {
        val files = mutableListOf<File>()
        val emptyFolders = mutableListOf<File>()
        val stack = ArrayDeque<File>()
        val root : File = Environment.getExternalStorageDirectory()
        stack.addFirst(root)

        val trashDir =
                File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

        val archiveExtensions =
                application.resources.getStringArray(R.array.archive_extensions).toSet()
        val apkExtensions = application.resources.getStringArray(R.array.apk_extensions).toSet()
        val imageExtensions = application.resources.getStringArray(R.array.image_extensions).toSet()
        val videoExtensions = application.resources.getStringArray(R.array.video_extensions).toSet()
        val audioExtensions = application.resources.getStringArray(R.array.audio_extensions).toSet()
        val genericExtensions =
                application.resources.getStringArray(R.array.generic_extensions).toSet()

        while (stack.isNotEmpty()) {
            val currentFile = stack.removeFirst()

            if (currentFile.isDirectory) {
                if (! currentFile.absolutePath.startsWith(trashDir.absolutePath)) {
                    val children = currentFile.listFiles()
                    if (children != null) {
                        if (children.isEmpty() && preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) {
                            emptyFolders.add(currentFile)
                        }
                        else {
                            for (child in children) {
                                if (shouldKeepFile(
                                        child ,
                                        archiveExtensions ,
                                        apkExtensions ,
                                        imageExtensions ,
                                        videoExtensions ,
                                        audioExtensions ,
                                        genericExtensions
                                    )
                                ) {
                                    if (child.isDirectory) {
                                        stack.addLast(child)
                                    }
                                    else {
                                        files.add(child)
                                    }
                                }
                            }
                        }
                    }
                    else {
                        println("Cleaner for Android -> listFiles() returned null for directory: ${currentFile.absolutePath}. Possible permission issue.")
                    }

                }
            }
            else {
                if (shouldKeepFile(
                        currentFile ,
                        archiveExtensions ,
                        apkExtensions ,
                        imageExtensions ,
                        videoExtensions ,
                        audioExtensions ,
                        genericExtensions
                    )
                ) {
                    files.add(currentFile)
                }
            }

        }

        return Pair(files , emptyFolders)
    }

    /**
     * Checks if a file should be kept based on user preferences and file extension.
     *
     * @param file The file to check.
     * @param archiveExtensions The set of archive file extensions.
     * @param apkExtensions The set of APK file extensions.
     * @param imageExtensions The set of image file extensions.
     * @param videoExtensions The set of video file extensions.
     * @param audioExtensions The set of audio file extensions.
     * @param genericExtensions The set of generic file extensions.
     * @return True if the file should be kept, false otherwise.
     */
    private fun shouldKeepFile(
        file : File ,
        archiveExtensions : Set<String> ,
        apkExtensions : Set<String> ,
        imageExtensions : Set<String> ,
        videoExtensions : Set<String> ,
        audioExtensions : Set<String> ,
        genericExtensions : Set<String> ,
    ) : Boolean {
        if (preferences.isEmpty() || preferences.all { ! it.value }) return false
        val extension = file.extension.lowercase()

        return when {
            preferences[ExtensionsConstants.ARCHIVE_EXTENSIONS] == true && extension in archiveExtensions -> {
                true
            }

            preferences[ExtensionsConstants.APK_EXTENSIONS] == true && extension in apkExtensions -> {
                true
            }

            preferences[ExtensionsConstants.IMAGE_EXTENSIONS] == true && extension in imageExtensions -> {
                true
            }

            preferences[ExtensionsConstants.VIDEO_EXTENSIONS] == true && extension in videoExtensions -> {
                true
            }

            preferences[ExtensionsConstants.AUDIO_EXTENSIONS] == true && extension in audioExtensions -> {
                true
            }

            preferences[ExtensionsConstants.GENERIC_EXTENSIONS] == true && extension in genericExtensions -> {
                true
            }

            extension.isEmpty() && preferences.any { it.value } -> {
                true
            }

            else -> {
                false
            }
        }
    }

    /**
     * Returns the list of filtered files.
     * @return The list of filtered files.
     */
    fun getFilteredFiles() : List<File> {
        println("getFilteredFiles() called")
        return filteredFiles
    }

    /**
     * Resets the filtered files list to an empty list.
     */
    fun reset() {
        println("reset() called")
        filteredFiles = emptyList()
    }
}