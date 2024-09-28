package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(val application: Application) {

    suspend fun getStorageInfo(): UiHomeModel {
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

    fun deleteFiles(filesToDelete: Set<File>) {
        filesToDelete.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }
    }

    fun getVideoThumbnail(filePath: String, context: Context): File? {
        val bitmap = getVideoThumbnailFromPath(filePath)
        if (bitmap != null) {
            val thumbnailFile = File(context.cacheDir, "thumbnail_${filePath.hashCode()}.png")
            val savedSuccessfully = saveBitmapToFile(bitmap, thumbnailFile)
            if (savedSuccessfully) {
                return thumbnailFile
            }
        }
        return null
    }

    private fun getVideoThumbnailFromPath(filePath: String): Bitmap? {
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

    private fun saveBitmapToFile(bitmap: Bitmap , file: File): Boolean {
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
}