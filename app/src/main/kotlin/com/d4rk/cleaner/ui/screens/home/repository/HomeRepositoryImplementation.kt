package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.media.MediaScannerConnection
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import kotlinx.coroutines.flow.first
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

    fun calculateDaysSince(timestamp : Long) : Int {
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

    suspend fun moveToTrash(filesToMove : List<File>) {
        if (! trashDir.exists()) {
            trashDir.mkdirs()
        }

        filesToMove.forEach { file ->
            if (file.exists()) {
                val originalPath = file.absolutePath
                val destination = File(trashDir , file.name)

                println("Cleaner for Android -> Moving file: ${file.absolutePath} to ${destination.absolutePath}")

                if (file.renameTo(destination)) {
                    println("Cleaner for Android -> File moved successfully")
                    dataStore.addTrashFileOriginalPath(originalPath)
                    dataStore.addTrashFilePath(originalPath to destination.absolutePath)


                    MediaScannerConnection.scanFile(
                        application , arrayOf(
                            destination.absolutePath , file.absolutePath
                        ) , null , null
                    )
                }
                else {
                    println("Cleaner for Android -> File move failed")
                }
            }
            else {
                println("Cleaner for Android -> File does not exist: ${file.absolutePath}")
            }
        }
    }

    suspend fun restoreFromTrash(filesToRestore : Set<File>) {
        println("Cleaner for Android -> impl logic called")
        val originalPaths = dataStore.trashFileOriginalPaths.first()
        println("Cleaner for Android -> Original paths from DataStore: $originalPaths")
        val trashToOriginalMap =
                dataStore.trashFilePaths.first().associate { it.second to it.first }
        println("Cleaner for Android -> trashToOriginalMap: $trashToOriginalMap")

        filesToRestore.forEach { file ->
            println("Cleaner for Android -> Attempting to restore: ${file.absolutePath}")

            if (file.exists()) {
                val originalPath = originalPaths.firstOrNull { File(it).name == file.name }
                println("Cleaner for Android -> Original path found: $originalPath")

                if (originalPath != null) {
                    val destinationFile = File(originalPath)
                    val destinationParent = destinationFile.parentFile

                    if (destinationParent?.exists() == false) {
                        destinationParent.mkdirs()
                    }

                    println("Cleaner for Android -> Restoring to: ${destinationFile.absolutePath}")

                    if (file.renameTo(destinationFile)) {
                        println("Cleaner for Android -> File restored successfully")
                        dataStore.removeTrashFileOriginalPath(originalPath)
                        dataStore.removeTrashFilePath(originalPath)
                        MediaScannerConnection.scanFile(
                            application , arrayOf(
                                destinationFile.absolutePath , file.absolutePath
                            ) , null , null
                        )
                    }
                    else {
                        println("Cleaner for Android -> File restore failed. Check if the file already exists or there is a permission issue.") // More informative message
                    }
                }
                else {
                    println("Cleaner for Android -> No original path found for ${file.name}. Restoring to Downloads.")
                    val downloadsDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destinationFile = File(downloadsDir , file.name)

                    if (file.renameTo(destinationFile)) {
                        println("Cleaner for Android -> File restored to Downloads successfully")

                        MediaScannerConnection.scanFile(
                            application ,
                            arrayOf(destinationFile.absolutePath , file.absolutePath) ,
                            null ,
                            null
                        )
                    }
                    else {
                        println("Cleaner for Android -> File restore to Downloads failed")
                    }
                }
            }
            else {
                println("Cleaner for Android -> File does not exist in trash: ${file.absolutePath}")
            }
        }
    }
}