package com.d4rk.cleaner.ui.home.repository

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.FileScanner
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeRepository(
    dataStore: DataStore,
    resources: Resources
) {
    private val fileScanner = FileScanner(dataStore, resources)

    suspend fun getStorageInfo(context: Context): UiHomeModel {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                StorageUtils.getStorageInfo(context) { used, total, usageProgress ->
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
    }

    suspend fun analyzeFiles(): List<File> {
        return withContext(Dispatchers.IO) {
            fileScanner.startScanning()
            fileScanner.getFilteredFiles()
        }
    }

    suspend fun deleteFiles(filesToDelete: Set<File>) {
        withContext(Dispatchers.IO) {
            filesToDelete.forEach { file ->
                if (file.exists()) {
                    file.deleteRecursively()
                }
            }
        }
    }

    suspend fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
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

    suspend fun getVideoThumbnail(filePath: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
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
}