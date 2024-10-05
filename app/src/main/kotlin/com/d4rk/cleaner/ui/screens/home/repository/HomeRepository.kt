package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.os.Environment
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.FileScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class HomeRepository(
    dataStore : DataStore , application : Application ,
) : HomeRepositoryImplementation(application) {
    private val fileScanner = FileScanner(dataStore , application)

    suspend fun getStorageInfo(onSuccess : (UiHomeModel) -> Unit) {
        withContext(Dispatchers.IO) {
            val storageInfo : UiHomeModel = getStorageInfo()
            withContext(Dispatchers.Main) {
                onSuccess(storageInfo)
            }
        }
    }

    suspend fun analyzeFiles(onSuccess : (List<File>) -> Unit) {
        withContext(Dispatchers.IO) {
            fileScanner.startScanning()
            val filteredFiles = fileScanner.getFilteredFiles()
            withContext(Dispatchers.Main) {
                onSuccess(filteredFiles)
            }
        }
    }

    suspend fun rescanFiles(onSuccess : (List<File>) -> Unit) {
        withContext(Dispatchers.IO) {
            fileScanner.reset()
            fileScanner.startScanning()
            val filteredFiles = fileScanner.getFilteredFiles()
            withContext(Dispatchers.Main) {
                onSuccess(filteredFiles)
            }
        }
    }

    suspend fun getTrashFiles() : List<File> {
        return withContext(Dispatchers.IO) {
            val trashDir = File(
                application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash"
            )

            if (trashDir.exists()) {
                trashDir.listFiles()?.toList() ?: emptyList()
            }
            else {
                return@withContext emptyList()
            }
        }
    }

    suspend fun deleteFiles(filesToDelete : Set<File> , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            deleteFiles(filesToDelete)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun moveToTrash(filesToMove : List<File> , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            moveToTrash(filesToMove)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun restoreFromTrash(filesToRestore : Set<File> , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            restoreFromTrash(filesToRestore)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}