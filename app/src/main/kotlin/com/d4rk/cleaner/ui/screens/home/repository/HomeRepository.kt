package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.os.Environment
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.FileScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Repository class for handling home screen data and operations.
 *
 * @param dataStore The DataStore instance for accessing user preferences.
 * @param application The application instance for accessing resources and external files directory.
 * @author Mihai-Cristian Condrea
 */
class HomeRepository(
    dataStore : DataStore , application : Application ,
) : HomeRepositoryImplementation(application , dataStore) {
    private val fileScanner = FileScanner(dataStore , application)

    /**
     * Retrieves storage information.
     * @param onSuccess Callback function to be invoked with the storage information.
     */
    suspend fun getStorageInfo(onSuccess : (UiHomeModel) -> Unit) {
        withContext(Dispatchers.IO) {
            val storageInfo : UiHomeModel = getStorageInfo()
            withContext(Dispatchers.Main) {
                onSuccess(storageInfo)
            }
        }
    }

    suspend fun getLastScanInfo(onSuccess: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            dataStore.lastScanTimestamp.firstOrNull()?.let { timestamp ->
                val daysFromLastScan = calculateDaysSince(timestamp)
                withContext(Dispatchers.Main) {
                    onSuccess(daysFromLastScan)
                }
            }
        }
    }

    /**
     * Retrieves file types data from resources.
     * @param onSuccess Callback function to be invoked with the file types data.
     */
    suspend fun getFileTypesData(onSuccess : (FileTypesData) -> Unit) {
        withContext(Dispatchers.IO) {
            val fileTypesData = getFileTypesDataFromResources()
            withContext(Dispatchers.Main) {
                onSuccess(fileTypesData)
            }
        }
    }

    /**
     * Analyzes files and retrieves filtered files and empty folders.
     * @param onSuccess Callback function to be invoked with the filtered files and empty folders.
     */
    suspend fun analyzeFiles(onSuccess : (Pair<List<File> , List<File>>) -> Unit) {
        withContext(Dispatchers.IO) {
            fileScanner.startScanning()
            val filteredFiles = fileScanner.getFilteredFiles()
            val emptyFolders = fileScanner.getAllFiles().second.ifEmpty {
                emptyList()
            }
            println("Cleaner for Android -> analyzeFiles() received filteredFiles size: ${filteredFiles.size}, emptyFolders size: ${emptyFolders.size}")
            withContext(Dispatchers.Main) {
                onSuccess(Pair(filteredFiles , emptyFolders))
            }
        }
    }

    /**
     * Rescans files and retrieves filtered files.
     * @param onSuccess Callback to receive the result of filtered files.
     */
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

    /**
     * Retrieves files from the trash directory.
     * @return A list of files in the trash directory.
     */
    suspend fun getTrashFiles() : List<File> {
        return withContext(Dispatchers.IO) {
            val trashDir = File(
                application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash"
            )

            if (trashDir.exists()) {
                return@withContext trashDir.listFiles()?.toList() ?: emptyList()
            }
            else {
                return@withContext emptyList()
            }
        }
    }

    /**
     * Deletes the specified files.
     * @param filesToDelete The set of files to delete.
     * @param onSuccess Callback function to be invoked after successful deletion.
     */
    suspend fun deleteFiles(filesToDelete : Set<File> , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            deleteFiles(filesToDelete)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    /**
     * Moves the specified files to the trash directory.
     * @param filesToMove The list of files to move to trash.
     * @param onSuccess Callback function to be invoked after successful move.
     */
    suspend fun moveToTrash(filesToMove : List<File> , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            moveToTrash(filesToMove)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    suspend fun addTrashSize(size: Long) {
        dataStore.addTrashSize(size)
    }

    /**
     * Restores the specified files from the trash directory.
     * @param filesToRestore The set of files to restore from trash.
     * @param onSuccess Callback function to be invoked after successful restore.
     */
    suspend fun restoreFromTrash(filesToRestore : Set<File> , onSuccess : () -> Unit) {
        withContext(Dispatchers.IO) {
            restoreFromTrash(filesToRestore)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}