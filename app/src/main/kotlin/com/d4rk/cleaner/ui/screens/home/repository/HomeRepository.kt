package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.os.Environment
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.FileScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class HomeRepository(dataStore : DataStore , application : Application) : HomeRepositoryImplementation(application , dataStore) {
    private val fileScanner : FileScanner = FileScanner(dataStore , application)

    suspend fun getStorageInfoRepository(onSuccess : (UiHomeModel) -> Unit) {
        withContext(context = Dispatchers.IO) {
            val storageInfo : UiHomeModel = getStorageInfoImplementation()
            withContext(context = Dispatchers.Main) {
                onSuccess(storageInfo)
            }
        }
    }

    suspend fun getFileTypesRepository(onSuccess : (FileTypesData) -> Unit) {
        withContext(context = Dispatchers.IO) {
            val fileTypesData : FileTypesData = getFileTypesImplementation()
            withContext(context = Dispatchers.Main) {
                onSuccess(fileTypesData)
            }
        }
    }

    suspend fun analyzeFiles(onSuccess : (Pair<List<File> , List<File>>) -> Unit) {
        withContext(context = Dispatchers.IO) {
            fileScanner.startScanning()
            val filteredFiles : List<File> = fileScanner.getFilteredFiles()
            val emptyFolders : List<File> = fileScanner.getAllFiles().second.ifEmpty {
                emptyList()
            }
            withContext(context = Dispatchers.Main) {
                onSuccess(Pair(filteredFiles , emptyFolders))
            }
        }
    }

    suspend fun rescanFiles(onSuccess : (List<File>) -> Unit) {
        withContext(context = Dispatchers.IO) {
            fileScanner.reset()
            fileScanner.startScanning()
            val filteredFiles : List<File> = fileScanner.getFilteredFiles()
            withContext(Dispatchers.Main) {
                onSuccess(filteredFiles)
            }
        }
    }

    suspend fun getTrashFiles() : List<File> {
        return withContext(context = Dispatchers.IO) {
            val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

            if (trashDir.exists()) {
                return@withContext trashDir.listFiles()?.toList() ?: emptyList()
            }
            else {
                return@withContext emptyList()
            }
        }
    }

    suspend fun deleteFilesRepository(filesToDelete : Set<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            deleteFilesImplementation(filesToDelete = filesToDelete)
            withContext(context = Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun moveToTrashRepository(filesToMove : List<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            moveToTrashImplementation(filesToMove = filesToMove)
            withContext(context = Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun restoreFromTrashRepository(filesToRestore : Set<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            restoreFromTrashImplementation(filesToRestore = filesToRestore)
            withContext(context = Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun addTrashSize(size : Long) {
        dataStore.addTrashSize(size = size)
    }

    suspend fun subtractTrashSize(size : Long) {
        dataStore.subtractTrashSize(size = size)
    }
}