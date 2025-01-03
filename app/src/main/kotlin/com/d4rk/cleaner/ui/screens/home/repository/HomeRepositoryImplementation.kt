package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.media.MediaScannerConnection
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import kotlinx.coroutines.flow.first
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(
    val application : Application ,
    val dataStore : DataStore ,
) {

    private val trashDir : File =
            File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

    suspend fun getStorageInfoImplementation() : UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(context = application) { _ , _ , _ , usageProgress , freeSpacePercentage ->
                continuation.resume(
                    UiHomeModel(
                        storageInfo = StorageInfo(
                            storageUsageProgress = usageProgress ,
                            freeSpacePercentage = freeSpacePercentage
                        )
                    )
                )
            }
        }
    }

    suspend fun getFileTypesImplementation() : FileTypesData {
        return suspendCoroutine { continuation ->
            val apkExtensions : List<String> =
                    application.resources.getStringArray(R.array.apk_extensions).toList()
            val imageExtensions : List<String> =
                    application.resources.getStringArray(R.array.image_extensions).toList()
            val videoExtensions : List<String> =
                    application.resources.getStringArray(R.array.video_extensions).toList()
            val audioExtensions : List<String> =
                    application.resources.getStringArray(R.array.audio_extensions).toList()
            val archiveExtensions : List<String> =
                    application.resources.getStringArray(R.array.archive_extensions).toList()
            val fileTypesTitles : List<String> =
                    application.resources.getStringArray(R.array.file_types_titles).toList()
            val fileTypesData = FileTypesData(
                apkExtensions = apkExtensions ,
                imageExtensions = imageExtensions ,
                videoExtensions = videoExtensions ,
                audioExtensions = audioExtensions ,
                archiveExtensions = archiveExtensions ,
                fileTypesTitles = fileTypesTitles
            )
            continuation.resume(value = fileTypesData)
        }
    }

    fun deleteFilesImplementation(filesToDelete : Set<File>) {
        filesToDelete.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
            else {
                // TODO: add a dialog if no file to delete found
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
                        application , arrayOf(
                            destination.absolutePath , file.absolutePath
                        ) , null , null
                    )
                }
                else {
                    // TODO: Add a dialog for failed files at the end for moving to trash
                }
            }
            else {
                // TODO: Add a dialog if the file does not exist for moving to trash
            }
        }
    }

    suspend fun restoreFromTrashImplementation(filesToRestore : Set<File>) {
        val originalPaths : Set<String> = dataStore.trashFileOriginalPaths.first()
        filesToRestore.forEach { file ->
            if (file.exists()) {
                val originalPath : String? =
                        originalPaths.firstOrNull { File(it).name == file.name }
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
                            application , arrayOf(
                                destinationFile.absolutePath , file.absolutePath
                            ) , null , null
                        )
                    }
                    else {
                        // TODO: Add a dialog for failed files at the end
                    }
                }
                else {
                    val downloadsDir : File =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destinationFile = File(downloadsDir , file.name)

                    if (file.renameTo(destinationFile)) {
                        MediaScannerConnection.scanFile(
                            application ,
                            arrayOf(destinationFile.absolutePath , file.absolutePath) ,
                            null ,
                            null
                        )
                    }
                    else {
                        // TODO: Add a dialog if the move to download has been failed
                    }
                }
            }
            else {
                // TODO: Add a dialog if the file does not exist
            }
        }
    }
}