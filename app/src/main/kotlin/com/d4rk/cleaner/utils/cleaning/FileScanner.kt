package com.d4rk.cleaner.utils.cleaning

import android.app.Application
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.constants.cleaning.ExtensionsConstants
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.flow.first
import java.io.File

class FileScanner(private val dataStore : DataStore , private val application : Application) {

    private var preferences : Map<String , Boolean> = emptyMap()
    private var filteredFiles : List<File> = emptyList()
    private var emptyFolders : List<File> = emptyList()

    suspend fun startScanning() {
        loadPreferences()
        val (files : List<File> , folders : List<File>) = getAllFiles()
        filteredFiles = files
        emptyFolders = folders
    }

    private suspend fun loadPreferences() {
        preferences = with(ExtensionsConstants) {
            mapOf(
                GENERIC_EXTENSIONS to dataStore.genericFilter.first() ,
                ARCHIVE_EXTENSIONS to dataStore.deleteArchives.first() ,
                APK_EXTENSIONS to dataStore.deleteApkFiles.first() ,
                IMAGE_EXTENSIONS to dataStore.deleteImageFiles.first() ,
                AUDIO_EXTENSIONS to dataStore.deleteAudioFiles.first() ,
                VIDEO_EXTENSIONS to dataStore.deleteVideoFiles.first() ,
                OFFICE_EXTENSIONS to dataStore.deleteOfficeFiles.first() ,
                WINDOWS_EXTENSIONS to dataStore.deleteWindowsFiles.first() ,
                FONT_EXTENSIONS to dataStore.deleteFontFiles.first() ,
                OTHER_EXTENSIONS to dataStore.deleteOtherFiles.first() ,
                EMPTY_FOLDERS to dataStore.deleteEmptyFolders.first()
            )
        }
    }

    fun getAllFiles() : Pair<List<File> , List<File>> {
        val files : MutableList<File> = mutableListOf()
        val emptyFolders : MutableList<File> = mutableListOf()
        val stack : ArrayDeque<File> = ArrayDeque()
        val root : File = Environment.getExternalStorageDirectory()
        stack.addFirst(element = root)

        val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

        val archiveExtensions : Set<String> = application.resources.getStringArray(R.array.archive_extensions).toSet()
        val apkExtensions : Set<String> = application.resources.getStringArray(R.array.apk_extensions).toSet()
        val windowsExtensions : Set<String> = application.resources.getStringArray(R.array.windows_extensions).toSet()
        val microsoftOfficeExtensions : Set<String> = application.resources.getStringArray(R.array.microsoft_office_extensions).toSet()
        val fontExtensions : Set<String> = application.resources.getStringArray(R.array.font_extensions).toSet()
        val imageExtensions : Set<String> = application.resources.getStringArray(R.array.image_extensions).toSet()
        val videoExtensions : Set<String> = application.resources.getStringArray(R.array.video_extensions).toSet()
        val audioExtensions : Set<String> = application.resources.getStringArray(R.array.audio_extensions).toSet()
        val genericExtensions : Set<String> = application.resources.getStringArray(R.array.generic_extensions).toSet()

        while (stack.isNotEmpty()) {
            val currentFile : File = stack.removeFirst()

            if (currentFile.isDirectory) {
                if (! currentFile.absolutePath.startsWith(trashDir.absolutePath)) {
                    currentFile.listFiles()?.let { children ->
                        if (children.isEmpty() && preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) {
                            emptyFolders.add(element = currentFile)
                        }
                        else {
                            children.forEach { child ->
                                if (shouldKeepFile(
                                        file = child ,
                                        archiveExtensions = archiveExtensions ,
                                        apkExtensions = apkExtensions ,
                                        imageExtensions = imageExtensions ,
                                        videoExtensions = videoExtensions ,
                                        audioExtensions = audioExtensions ,
                                        genericExtensions = genericExtensions ,
                                        microsoftOfficeExtensions = microsoftOfficeExtensions ,
                                        windowsExtensions = windowsExtensions ,
                                        fontExtensions = fontExtensions
                                    )
                                ) {
                                    if (child.isDirectory) {
                                        stack.addLast(element = child)
                                    }
                                    else {
                                        files.add(element = child)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                if (shouldKeepFile(
                        file = currentFile ,
                        archiveExtensions = archiveExtensions ,
                        apkExtensions = apkExtensions ,
                        imageExtensions = imageExtensions ,
                        videoExtensions = videoExtensions ,
                        audioExtensions = audioExtensions ,
                        genericExtensions = genericExtensions ,
                        microsoftOfficeExtensions = microsoftOfficeExtensions ,
                        windowsExtensions = windowsExtensions ,
                        fontExtensions = fontExtensions
                    )
                ) {
                    files.add(element = currentFile)
                }
            }
        }
        return Pair(first = files , second = emptyFolders)
    }

    private fun shouldKeepFile(
        file : File ,
        archiveExtensions : Set<String> ,
        apkExtensions : Set<String> ,
        imageExtensions : Set<String> ,
        videoExtensions : Set<String> ,
        audioExtensions : Set<String> ,
        genericExtensions : Set<String> ,
        microsoftOfficeExtensions : Set<String> ,
        windowsExtensions : Set<String> ,
        fontExtensions : Set<String>
    ) : Boolean {
        val extension : String = file.extension.lowercase()
        return when (extension) {
            in archiveExtensions -> preferences[ExtensionsConstants.ARCHIVE_EXTENSIONS] == true
            in apkExtensions -> preferences[ExtensionsConstants.APK_EXTENSIONS] == true
            in imageExtensions -> preferences[ExtensionsConstants.IMAGE_EXTENSIONS] == true
            in videoExtensions -> preferences[ExtensionsConstants.VIDEO_EXTENSIONS] == true
            in audioExtensions -> preferences[ExtensionsConstants.AUDIO_EXTENSIONS] == true
            in genericExtensions -> preferences[ExtensionsConstants.GENERIC_EXTENSIONS] == true
            in microsoftOfficeExtensions -> preferences[ExtensionsConstants.OFFICE_EXTENSIONS] == true
            in windowsExtensions -> preferences[ExtensionsConstants.WINDOWS_EXTENSIONS] == true
            in fontExtensions -> preferences[ExtensionsConstants.FONT_EXTENSIONS] == true
            else -> preferences[ExtensionsConstants.OTHER_EXTENSIONS] == true
        }
    }

    fun getFilteredFiles() : List<File> {
        return filteredFiles
    }

    fun reset() {
        filteredFiles = emptyList()
    }
}