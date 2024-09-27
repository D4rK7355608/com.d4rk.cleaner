package com.d4rk.cleaner.ui.home.repository

import android.app.Application
import android.graphics.Bitmap
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.FileScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class HomeRepository(
    dataStore: DataStore, application: Application
) : HomeRepositoryImplementation(application) {
    private val fileScanner = FileScanner(dataStore, application.resources)

    suspend fun getStorageInfo(onSuccess: (UiHomeModel) -> Unit) {
        withContext(Dispatchers.IO) {
            val storageInfo: UiHomeModel = getStorageInfo()
            withContext(Dispatchers.Main) {
                onSuccess(storageInfo)
            }
        }
    }

    suspend fun analyzeFiles(onSuccess: (List<File>) -> Unit) {
        withContext(Dispatchers.IO) {
            fileScanner.startScanning()
            val filteredFiles = fileScanner.getFilteredFiles()
            withContext(Dispatchers.Main) { // Switch to Main for callback
                onSuccess(filteredFiles)
            }
        }
    }

    suspend fun deleteFiles(filesToDelete: Set<File>, onSuccess: () -> Unit) {
        withContext(Dispatchers.IO) {
            deleteFiles(filesToDelete)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun saveBitmapToFile(bitmap: Bitmap, file: File, onSuccess: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val success: Boolean = saveBitmapToFile(bitmap, file)
            withContext(Dispatchers.Main) {
                onSuccess(success)
            }
        }
    }

    suspend fun getVideoThumbnail(filePath: String, onSuccess: (Bitmap?) -> Unit) {
        withContext(Dispatchers.IO) {
            val thumbnail: Bitmap? = getVideoThumbnail(filePath)
            withContext(Dispatchers.Main) {
                onSuccess(thumbnail)
            }
        }
    }
}