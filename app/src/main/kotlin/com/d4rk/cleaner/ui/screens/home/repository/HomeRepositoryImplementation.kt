package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.media.MediaScannerConnection
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(
    val application : Application ,
    val dataStore : DataStore ,
) {

    private val trashDir =
            File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

    suspend fun getStorageInfo() : UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(application) { used , total , usageProgress ->
                continuation.resume(
                    UiHomeModel(
                        storageUsageProgress = usageProgress ,
                        usedStorageFormatted = used ,
                        totalStorageFormatted = total ,
                    )
                )
            }
        }
    }

    fun calculateDaysSince(timestamp: Long): Int {
        if (timestamp == 0L) return 0

        val currentTime = System.currentTimeMillis()
        val differenceInMillis = currentTime - timestamp
        val days = TimeUnit.MILLISECONDS.toDays(differenceInMillis).toInt()

        return days
    }

    suspend fun getFileTypesDataFromResources() : FileTypesData {
        return suspendCoroutine { continuation ->
            val apkExtensions =
                    application.resources.getStringArray(R.array.apk_extensions).toList()
            val imageExtensions =
                    application.resources.getStringArray(R.array.image_extensions).toList()
            val videoExtensions =
                    application.resources.getStringArray(R.array.video_extensions).toList()
            val audioExtensions =
                    application.resources.getStringArray(R.array.audio_extensions).toList()
            val archiveExtensions =
                    application.resources.getStringArray(R.array.archive_extensions).toList()
            val fileTypesTitles =
                    application.resources.getStringArray(R.array.file_types_titles).toList()
            val fileTypesData = FileTypesData(
                apkExtensions = apkExtensions ,
                imageExtensions = imageExtensions ,
                videoExtensions = videoExtensions ,
                audioExtensions = audioExtensions ,
                archiveExtensions = archiveExtensions ,
                fileTypesTitles = fileTypesTitles
            )
            continuation.resume(fileTypesData)
        }
    }

    fun deleteFiles(filesToDelete : Set<File>) {
        filesToDelete.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }
    }

    /**
     * Moves the given files to the trash directory.
     *
     * @param filesToMove The list of files to move to the trash.
     */
    suspend fun moveToTrash(filesToMove : List<File>) {
        if (! trashDir.exists()) {
            trashDir.mkdirs()
        }

        filesToMove.forEach { file ->
            if (file.exists()) {
                val originalPath = file.absolutePath
                val destination = File(trashDir , "${file.name}_${file.lastModified()}")

                if (file.renameTo(destination)) {
                    dataStore.addTrashFilePath(originalPath)

                    MediaScannerConnection.scanFile(
                        application , arrayOf(
                            destination.absolutePath , file.absolutePath
                        ) , null , null
                    )
                }
            }
        }
    }

    suspend fun restoreFromTrash(filesToRestore : Set<File>) {
        filesToRestore.forEach { file ->
            if (file.exists()) {
                dataStore.trashFilePaths.collect { paths ->
                    val originalPath =
                            paths.find { it == file.absolutePath.replace(Regex("_\\d+$") , "") }
                    if (originalPath != null) {
                        val destinationFile = File(originalPath)
                        val destinationParent = destinationFile.parentFile

                        if (destinationParent?.exists() == false) {
                            destinationParent.mkdirs()
                        }

                        if (file.renameTo(destinationFile)) {
                            MediaScannerConnection.scanFile(
                                application , arrayOf(
                                    destinationFile.absolutePath , file.absolutePath
                                ) , null , null
                            )
                            dataStore.removeTrashFilePath(originalPath)
                        }
                    }
                }
            }
        }
    }
}