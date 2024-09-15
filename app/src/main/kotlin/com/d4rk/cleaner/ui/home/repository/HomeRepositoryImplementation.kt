package com.d4rk.cleaner.ui.home.repository

import android.app.Application
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(val application: Application) {

    suspend fun getStorageInfo() : UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(application) { used, total, usageProgress ->
                continuation.resume(
                    UiHomeModel(
                        progress = usageProgress,
                        storageUsed = used,
                        storageTotal = total
                    )
                )
            }
        }
    }


    fun analyzeFiles(): List<File> {
        // Add your file analysis logic here
        return emptyList() // Replace with actual implementation
    }

    fun deleteFiles(filesToDelete: Set<File>) {
        filesToDelete.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }
    }

    fun saveBitmapToFile(bitmap: Bitmap , file: File): Boolean {
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getVideoThumbnail(filePath: String): Bitmap? {
        return try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(filePath)
            val frame: Bitmap? = mediaMetadataRetriever.getFrameAtTime(0)
            mediaMetadataRetriever.release()
            frame
        } catch (e: Exception) {
            null
        }
    }
}