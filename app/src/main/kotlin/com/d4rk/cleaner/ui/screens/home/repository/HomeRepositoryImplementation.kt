package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.media.MediaScannerConnection
import android.os.Environment
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(val application : Application) {

    suspend fun getStorageInfo() : UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(application) { used , total , usageProgress ->
                continuation.resume(
                    UiHomeModel(
                        progress = usageProgress , storageUsed = used , storageTotal = total
                    )
                )
            }
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
    fun moveToTrash(filesToMove : List<File>) {
        val trashDir = File(
            application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash"
        )
        if (! trashDir.exists()) {
            trashDir.mkdirs()
        }

        filesToMove.forEach { file ->
            val destination = File(trashDir , file.name)
            if (file.exists()) {
                if (file.renameTo(destination)) {
                    MediaScannerConnection.scanFile(
                        application ,
                        arrayOf(destination.absolutePath , file.absolutePath) ,
                        null ,
                        null
                    )
                }
            }
        }
    }

    fun restoreFromTrash(filesToRestore: Set<File>) {
        filesToRestore.forEach { file ->
            val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Trash")

            val relativePathInTrash = file.relativeTo(trashDir).path
            val originalFile = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), relativePathInTrash)

            val destinationParent = originalFile.parentFile

            if (destinationParent != null && !destinationParent.exists()) {
                destinationParent.mkdirs()
            }

            if (file.exists()) {
                if (file.renameTo(originalFile)) {
                    MediaScannerConnection.scanFile(
                        application,
                        arrayOf(originalFile.absolutePath, file.absolutePath),
                        null,
                        null
                    )
                }
            }
        }
    }
}