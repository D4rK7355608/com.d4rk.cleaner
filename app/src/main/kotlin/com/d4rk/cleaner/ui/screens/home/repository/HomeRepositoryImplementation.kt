package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.core.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import kotlinx.coroutines.flow.first
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(val application : Application , val dataStore : DataStore) {
    private val trashDir : File = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

    suspend fun getStorageInfoImplementation() : com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(context = application) { _ , _ , _ , usageProgress , freeSpacePercentage ->
                continuation.resume(
                    com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel(storageInfo = com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo(storageUsageProgress = usageProgress , freeSpacePercentage = freeSpacePercentage))
                )
            }
        }
    }

    fun getAllFilesImplementation() : Pair<List<File> , List<File>> {
        val files : MutableList<File> = mutableListOf()
        val emptyFolders : MutableList<File> = mutableListOf()
        val stack : ArrayDeque<File> = ArrayDeque()
        val root : File = Environment.getExternalStorageDirectory()
        stack.addFirst(element = root)

        val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

        while (stack.isNotEmpty()) {
            val currentFile : File = stack.removeFirst()
            if (currentFile.isDirectory) {
                if (! currentFile.absolutePath.startsWith(trashDir.absolutePath)) {
                    currentFile.listFiles()?.let { children ->
                        if (children.isEmpty()) {
                            emptyFolders.add(currentFile)
                        }
                        else {
                            children.forEach { child ->
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
            }
            else {
                files.add(currentFile)
            }
        }
        return Pair(files , emptyFolders)
    }

    suspend fun getFileTypesImplementation() : com.d4rk.cleaner.core.data.model.ui.screens.FileTypesData {
        return suspendCoroutine { continuation ->
            val apkExtensions : List<String> = application.resources.getStringArray(R.array.apk_extensions).toList()
            val imageExtensions : List<String> = application.resources.getStringArray(R.array.image_extensions).toList()
            val videoExtensions : List<String> = application.resources.getStringArray(R.array.video_extensions).toList()
            val audioExtensions : List<String> = application.resources.getStringArray(R.array.audio_extensions).toList()
            val fontExtensions : List<String> = application.resources.getStringArray(R.array.font_extensions).toList()
            val windowsExtensions : List<String> = application.resources.getStringArray(R.array.windows_extensions).toList()
            val archiveExtensions : List<String> = application.resources.getStringArray(R.array.archive_extensions).toList()
            val officeExtensions : List<String> = application.resources.getStringArray(R.array.microsoft_office_extensions).toList()
            val genericExtensions : List<String> = application.resources.getStringArray(R.array.generic_extensions).toList()
            val fileTypesTitles : List<String> = application.resources.getStringArray(R.array.file_types_titles).toList()

            val knownExtensions = mutableSetOf<String>().apply {
                addAll(elements = apkExtensions.map { it.lowercase() })
                addAll(elements = imageExtensions.map { it.lowercase() })
                addAll(elements = videoExtensions.map { it.lowercase() })
                addAll(elements = audioExtensions.map { it.lowercase() })
                addAll(elements = fontExtensions.map { it.lowercase() })
                addAll(elements = windowsExtensions.map { it.lowercase() })
                addAll(elements = archiveExtensions.map { it.lowercase() })
                addAll(elements = officeExtensions.map { it.lowercase() })
                addAll(elements = genericExtensions.map { it.lowercase() })
            }

            val allFoundExtensions : MutableSet<String> = mutableSetOf()
            fun scanDir(dir : File) {
                dir.listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        scanDir(file)
                    }
                    else {
                        val ext : String = file.extension.lowercase()
                        if (ext.isNotEmpty()) {
                            allFoundExtensions.add(element = ext)
                        }
                    }
                }
            }
            scanDir(Environment.getExternalStorageDirectory())

            val otherExtensions : List<String> = (allFoundExtensions - knownExtensions).toList().sorted()

            val fileTypesData = com.d4rk.cleaner.core.data.model.ui.screens.FileTypesData(
                apkExtensions = apkExtensions ,
                imageExtensions = imageExtensions ,
                videoExtensions = videoExtensions ,
                audioExtensions = audioExtensions ,
                archiveExtensions = archiveExtensions ,
                fileTypesTitles = fileTypesTitles ,
                fontExtensions = fontExtensions ,
                windowsExtensions = windowsExtensions ,
                officeExtensions = officeExtensions ,
                otherExtensions = otherExtensions
            )
            continuation.resume(value = fileTypesData)
        }
    }

    suspend fun deleteFilesImplementation(filesToDelete : Set<File>) {
        val shouldClearClipboard : Boolean = dataStore.clipboardClean.first()

        filesToDelete.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }

        if (shouldClearClipboard) {
            clearClipboardImplementation()
        }
    }

    private fun clearClipboardImplementation() {
        val clipboardManager : ClipboardManager = application.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                clipboardManager.clearPrimaryClip()
            }
            else {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("" , ""))
            }
        }
    }

    suspend fun moveToTrashImplementation(filesToMove : List<File>) {
        if (! trashDir.exists()) {
            trashDir.mkdirs()
        }

        filesToMove.forEach { file ->
            if (file.exists()) {
                val originalPath : String = file.absolutePath
                val destination = File(trashDir , file.name)

                if (file.renameTo(destination)) {
                    dataStore.addTrashFileOriginalPath(originalPath = originalPath)
                    dataStore.addTrashFilePath(pathPair = originalPath to destination.absolutePath)
                    MediaScannerConnection.scanFile(
                        application , arrayOf(destination.absolutePath , file.absolutePath) , null , null
                    )
                }
            }
        }
    }

    suspend fun restoreFromTrashImplementation(filesToRestore : Set<File>) {
        val originalPaths : Set<String> = dataStore.trashFileOriginalPaths.first()
        filesToRestore.forEach { file ->
            if (file.exists()) {
                val originalPath : String? = originalPaths.firstOrNull { File(it).name == file.name }
                if (originalPath != null) {
                    val destinationFile = File(originalPath)
                    val destinationParent : File? = destinationFile.parentFile

                    if (destinationParent?.exists() == false) {
                        destinationParent.mkdirs()
                    }

                    if (file.renameTo(destinationFile)) {
                        dataStore.removeTrashFileOriginalPath(originalPath = originalPath)
                        dataStore.removeTrashFilePath(originalPath = originalPath)
                        MediaScannerConnection.scanFile(
                            application , arrayOf(destinationFile.absolutePath , file.absolutePath) , null , null
                        )
                    }
                }
                else {
                    val downloadsDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destinationFile = File(downloadsDir , file.name)

                    if (file.renameTo(destinationFile)) {
                        MediaScannerConnection.scanFile(
                            application , arrayOf(destinationFile.absolutePath , file.absolutePath) , null , null
                        )
                    }
                }
            }
        }
    }
}