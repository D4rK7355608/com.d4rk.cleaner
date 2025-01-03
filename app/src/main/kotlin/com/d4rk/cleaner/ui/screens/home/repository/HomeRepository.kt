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

/**
 * Repository class for handling home screen data and operations.
 *
 * @param dataStore The DataStore instance for accessing user preferences.
 * @param application The application instance for accessing resources and external files directory.
 * @author Mihai-Cristian Condrea
 */
class HomeRepository(dataStore : DataStore , application : Application) :
    HomeRepositoryImplementation(application , dataStore) {
    private val fileScanner : FileScanner = FileScanner(dataStore , application)

    /**
     * Retrieves storage information.
     * @param onSuccess Callback function to be invoked with the storage information.
     */
    suspend fun getStorageInfoRepository(onSuccess : (UiHomeModel) -> Unit) {
        withContext(context = Dispatchers.IO) {
            val storageInfo : UiHomeModel = getStorageInfoImplementation()
            withContext(context = Dispatchers.Main) {
                onSuccess(storageInfo)
            }
        }
    }

    /**
     * Retrieves file types data from resources.
     * @param onSuccess Callback function to be invoked with the file types data.
     */
    suspend fun getFileTypesRepository(onSuccess : (FileTypesData) -> Unit) {
        withContext(context = Dispatchers.IO) {
            val fileTypesData : FileTypesData = getFileTypesImplementation()
            withContext(context = Dispatchers.Main) {
                onSuccess(fileTypesData)
            }
        }
    }

    /**
     * Analyzes files and retrieves filtered files and empty folders.
     * @param onSuccess Callback function to be invoked with the filtered files and empty folders.
     */
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

    /**
     * Rescans files and retrieves filtered files.
     * @param onSuccess Callback to receive the result of filtered files.
     */
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

    /**
     * Retrieves files from the trash directory.
     * @return A list of files in the trash directory.
     */
    suspend fun getTrashFiles() : List<File> {
        return withContext(context = Dispatchers.IO) {
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
    suspend fun deleteFilesRepository(filesToDelete : Set<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            deleteFilesImplementation(filesToDelete = filesToDelete)
            withContext(context = Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    /**
     * Moves the specified files to the trash directory.
     * @param filesToMove The list of files to move to trash.
     * @param onSuccess Callback function to be invoked after successful move.
     */
    suspend fun moveToTrashRepository(filesToMove : List<File> , onSuccess : () -> Unit) {
        withContext(context = Dispatchers.IO) {
            moveToTrashImplementation(filesToMove = filesToMove)
            withContext(context = Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    /**
     * Restores the specified files from the trash directory.
     * @param filesToRestore The set of files to restore from trash.
     * @param onSuccess Callback function to be invoked after successful restore.
     */
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