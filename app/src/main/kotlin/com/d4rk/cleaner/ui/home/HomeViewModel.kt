package com.d4rk.cleaner.ui.home

import android.app.Activity
import android.app.Application
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.cleaning.FileScanner
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

class HomeViewModel(application : Application) : AndroidViewModel(application) {
    val progress = MutableLiveData(0f)
    val storageUsed = MutableLiveData<String>()
    val storageTotal = MutableLiveData<String>()
    var fileScanner : FileScanner
    val scannedFiles = MutableLiveData<List<File>>()
    val allFilesSelected = mutableStateOf(value = false)
    val fileSelectionStates = mutableStateMapOf<File , Boolean>()
    private val dataStoreInstance : DataStore = DataStore(application)
    val showCleaningComposable = MutableLiveData(false)
    val isAnalyzing = MutableLiveData(false)
    var showRescanDialog = mutableStateOf(value = false)
    private var hasScanned = mutableStateOf(value = false)
    private var isUserConfirmedRescan = mutableStateOf(value = false)
    val _selectedFileCount = MutableStateFlow(value = 0)
    val selectedFileCount : StateFlow<Int> = _selectedFileCount.asStateFlow()

    init {
        updateStorageInfo()
        fileScanner = FileScanner(dataStoreInstance , application.resources)
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
            StorageUtils.getStorageInfo(getApplication()) { used , total , usageProgress ->
                storageUsed.postValue(used)
                storageTotal.postValue(total)
                progress.postValue(usageProgress)
            }
        }
    }

    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        fileSelectionStates[file] = isChecked
        _selectedFileCount.value = fileSelectionStates.count { it.value }
        allFilesSelected.value = fileSelectionStates.all { it.value }
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
    fun selectAllFiles(selectAll : Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                scannedFiles.value?.forEach { file ->
                    fileSelectionStates[file] = selectAll
                }
            }
            allFilesSelected.value = selectAll
            _selectedFileCount.value = if (selectAll) fileSelectionStates.size else 0
        }
    }

    /**
     * Initiates the file analysis process.
     *
     * This function checks for necessary permissions and, if granted, triggers the file* scanning process asynchronously.
     *
     * @param activity The Activity instance required to request permissions.
     */
    fun analyze(activity : Activity) {
        if (! PermissionsUtils.hasStoragePermissions(getApplication())) {
            PermissionsUtils.requestStoragePermissions(activity)
            return
        }

        if (hasScanned.value && ! isUserConfirmedRescan.value) {
            showRescanDialog.value = true
            return
        }

        isUserConfirmedRescan.value = false
        isAnalyzing.value = true
        showCleaningComposable.value = true
        viewModelScope.launch {
            val filteredFiles : List<File> = withContext(Dispatchers.IO) {
                fileScanner.startScanning()
                fileScanner.getFilteredFiles()
            }

            withContext(Dispatchers.Main) {
                fileSelectionStates.clear()
                scannedFiles.value = filteredFiles
                isAnalyzing.value = false
                hasScanned.value = true
            }
        }
    }

    fun rescan(activity : Activity) {
        showRescanDialog.value = false
        scannedFiles.value = emptyList()
        analyze(activity)
    }

    /**
     * Initiates the cleaning process if the required permissions are granted.
     *
     * @param activity The Activity instance required to request permissions.
     */
    fun clean(activity : Activity) {
        if (! PermissionsUtils.hasStoragePermissions(getApplication())) {
            PermissionsUtils.requestStoragePermissions(activity)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val filesToDelete : Set<File> = fileSelectionStates.filter { it.value }.keys
            filesToDelete.forEach { file ->
                if (file.exists()) {
                    file.deleteRecursively()
                }
            }

            withContext(Dispatchers.Main) {
                scannedFiles.value = scannedFiles.value?.filterNot { filesToDelete.contains(it) }
                fileSelectionStates.clear()
                selectAllFiles(selectAll = false)
                _selectedFileCount.value = 0
                updateStorageInfo()
            }
        }
    }
}