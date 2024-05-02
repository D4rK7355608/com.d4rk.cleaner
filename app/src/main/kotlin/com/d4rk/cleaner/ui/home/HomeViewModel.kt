package com.d4rk.cleaner.ui.home

import android.app.Application
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.storage.StorageManager
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.store.DataStore
import com.d4rk.cleaner.utils.FileScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val progress = MutableLiveData(0f)
    val storageUsed = MutableLiveData<String>()
    val storageTotal = MutableLiveData<String>()
    var fileScanner: FileScanner

    val scannedFiles = MutableLiveData<List<File>>()
    val allFilesSelected = mutableStateOf(false)
    val fileSelectionStates = mutableStateMapOf<File, Boolean>()
    private val dataStoreInstance: DataStore = DataStore(application)

    init {
        updateStorageInfo()
        val resourcesInstance = application.resources
        fileScanner = FileScanner(dataStoreInstance, resourcesInstance)
    }

    /**
     * Updates storage information asynchronously.
     *
     * This function retrieves and updates storage-related information such as total storage size, used storage size,
     * and storage usage progress.
     * It utilizes the Android StorageManager and StorageStatsManager to fetch storage statistics.
     * The updated storage information is then posted to corresponding LiveData objects for observation.
     *
     * @see android.content.Context.STORAGE_SERVICE
     * @see android.content.Context.STORAGE_STATS_SERVICE
     * @see android.os.storage.StorageManager
     * @see android.os.storage.StorageStatsManager
     * @see android.os.storage.StorageVolume
     * @param viewModelScope The coroutine scope associated with the ViewModel for launching asynchronous tasks.
     */
    private fun updateStorageInfo() {
        viewModelScope.launch {
            val storageManager =
                getApplication<Application>().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageStatsManager =
                getApplication<Application>().getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageVolume = storageManager.primaryStorageVolume
            val totalSize: Long
            val usedSize: Long
            val uuidStr = storageVolume.uuid
            val uuid: UUID =
                if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)
            totalSize = storageStatsManager.getTotalBytes(uuid)
            usedSize = totalSize - storageStatsManager.getFreeBytes(uuid)
            storageUsed.postValue((usedSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString())
            storageTotal.postValue((totalSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString())
            progress.postValue(usedSize.toFloat() / totalSize.toFloat())
        }
    }

    /**
     * Initiates the file analysis process by invoking the `FileScanner` to scan for files and filter them based on predefined preferences.
     *
     * This function triggers the file scanning process asynchronously using a coroutine in the IO context.
     * It calls the `startScanning()` method of the `FileScanner` class to begin analyzing files.
     * Once scanning is complete, the function retrieves the filtered list of files using `getFilteredFiles()`
     * from the `FileScanner` instance and updates the `scannedFiles` live data with the result.
     * @see FileScanner
     */
    fun analyze() {
        CoroutineScope(Dispatchers.IO).launch {
            fileScanner.startScanning()
            scannedFiles.postValue(fileScanner.getFilteredFiles())
        }
    }

    /**
     * This function is used to select or deselect all files in the scannedFiles list.
     *
     * @param selectAll A boolean value indicating whether to select all files (if true) or deselect all files (if false).
     * When this function is called with true, it will mark all files in the scannedFiles list as selected by adding them to the fileSelectionStates map with a value of true.
     * If called with false, it will mark all files as not selected by adding them to the fileSelectionStates map with a value of false.
     * The function also updates the allFilesSelected LiveData object with the value of the selectAll parameter.
     *
     * Usage:
     * selectAllFiles(true)  // Selects all files
     * selectAllFiles(false) // Deselects all files
     */
    fun selectAllFiles(selectAll: Boolean) {
        scannedFiles.value?.forEach { file ->
            fileSelectionStates[file] = selectAll
        }
        allFilesSelected.value = selectAll
    }

    fun clean() {

    }
}