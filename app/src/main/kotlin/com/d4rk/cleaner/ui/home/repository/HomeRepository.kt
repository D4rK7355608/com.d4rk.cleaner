package com.d4rk.cleaner.ui.home.repository

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.FileScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.roundToInt

class HomeRepository(
    dataStore: DataStore,
    resources: Resources
) {
    private val fileScanner = FileScanner(dataStore, resources)

    suspend fun getStorageInfo(context: Context): UiHomeModel {
        return withContext(Dispatchers.IO) {
            val storageManager: StorageManager =
                context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageStatsManager: StorageStatsManager =
                context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageVolume: StorageVolume = storageManager.primaryStorageVolume
            val totalSize: Long
            val usedSize: Long
            val uuidStr: String? = storageVolume.uuid
            val uuid: UUID =
                if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)
            totalSize = storageStatsManager.getTotalBytes(uuid)
            usedSize = totalSize - storageStatsManager.getFreeBytes(uuid)
            val usedFormatted: String =
                (usedSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString()
            val totalFormatted: String =
                (totalSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString()
            val usageProgress: Float = usedSize.toFloat() / totalSize.toFloat()

            UiHomeModel(
                progress = usageProgress,
                storageUsed = usedFormatted,
                storageTotal = totalFormatted
            )
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