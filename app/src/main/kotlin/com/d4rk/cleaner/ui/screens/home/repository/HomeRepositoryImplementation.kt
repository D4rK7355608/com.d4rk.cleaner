package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(
    val application : Application ,
    val dataStore : DataStore ,
) {

    private val trashDir =
            File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

    private val sharedPrefs =
            application.getSharedPreferences("TrashMetadata" , Context.MODE_PRIVATE)

    suspend fun getStorageInfo() : UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(application) { used , total , usageProgress ->
                continuation.resume(
                    UiHomeModel(
                        storageUsageProgress = usageProgress , usedStorageFormatted = used , totalStorageFormatted = total
                    )
                )
            }
        }
    }

    suspend fun getFileTypesDataFromResources(): FileTypesData {
        return suspendCoroutine { continuation ->
            val apkExtensions = application.resources.getStringArray(R.array.apk_extensions).toList()
            val imageExtensions = application.resources.getStringArray(R.array.image_extensions).toList()
            val videoExtensions = application.resources.getStringArray(R.array.video_extensions).toList()
            val audioExtensions = application.resources.getStringArray(R.array.audio_extensions).toList()
            val archiveExtensions = application.resources.getStringArray(R.array.archive_extensions).toList()
            val fileTypesTitles = application.resources.getStringArray(R.array.file_types_titles).toList()
            val fileTypesData = FileTypesData(
                apkExtensions = apkExtensions ,
                imageExtensions = imageExtensions ,
                videoExtensions = videoExtensions ,
                audioExtensions = audioExtensions , // FIXME: Type mismatch: inferred type is Array<(out) String!> but IntArray was expected
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
                        application ,
                        arrayOf(
                            destination.absolutePath ,
                            file.absolutePath
                        ) ,
                        null ,
                        null
                    )
                }
            }
        }
    }

    fun restoreFromTrash(filesToRestore : Set<File>) {
        filesToRestore.forEach { file ->
            if (file.exists()) {
                val originalPath = sharedPrefs.getString(file.absolutePath , null)
                    ?: file.absolutePath.replace(Regex("_\\d+$") , "")

                val destinationFile = File(originalPath)
                val destinationParent = destinationFile.parentFile

                if (destinationParent?.exists() == false) {
                    destinationParent.mkdirs()
                }

                if (file.renameTo(destinationFile)) {
                    MediaScannerConnection.scanFile(
                        application ,
                        arrayOf(
                            destinationFile.absolutePath ,
                            file.absolutePath
                        ) ,
                        null ,
                        null
                    )
                    sharedPrefs.edit().remove(file.absolutePath).apply()
                }
            }
        }
    }
}