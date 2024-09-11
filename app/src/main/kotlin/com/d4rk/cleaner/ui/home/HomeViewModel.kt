package com.d4rk.cleaner.ui.home

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.cleaning.FileScanner
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress
    private val _storageUsed = MutableStateFlow("")
    val storageUsed: StateFlow<String> = _storageUsed
    private val _storageTotal = MutableStateFlow("")
    val storageTotal: StateFlow<String> = _storageTotal
    private var fileScanner: FileScanner
    private val _scannedFiles = MutableStateFlow<List<File>>(emptyList())
    val scannedFiles: StateFlow<List<File>> = _scannedFiles
    val allFilesSelected = mutableStateOf(value = false)
    val fileSelectionStates = mutableStateMapOf<File, Boolean>()
    private val dataStoreInstance: DataStore = DataStore(application)
    private val _showCleaningComposable = MutableStateFlow(false)
    val showCleaningComposable: StateFlow<Boolean> = _showCleaningComposable
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing
    var showRescanDialog = mutableStateOf(value = false)
    private var hasScanned = mutableStateOf(value = false)
    private var isUserConfirmedRescan = mutableStateOf(value = false)
    private val _selectedFileCount = MutableStateFlow(fileSelectionStates.count { it.value })
    val selectedFileCount: StateFlow<Int> = _selectedFileCount
    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog: StateFlow<Boolean> = _showErrorDialog
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    init {
        updateStorageInfo()
        fileScanner = FileScanner(dataStoreInstance, application.resources)
    }

    fun dismissErrorDialog() {
        _showErrorDialog.value = false
    }

    private fun updateStorageInfo() {
        viewModelScope.launch {
            StorageUtils.getStorageInfo(getApplication()) { used, total, usageProgress ->
                _storageUsed.value = used
                _storageTotal.value = total
                _progress.value = usageProgress
            }
        }
    }

    fun onFileSelectionChange(file: File, isChecked: Boolean) {
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
    fun selectAllFiles(selectAll: Boolean) {
        viewModelScope.launch {
            allFilesSelected.value = selectAll
            withContext(Dispatchers.IO) {
                scannedFiles.value.forEach { file ->
                    fileSelectionStates[file] = selectAll
                }
            }
            _selectedFileCount.value = if (selectAll) fileSelectionStates.size else 0
        }
    }

    fun analyze() {
        if (!PermissionsUtils.hasStoragePermissions(getApplication())) {
            // Handle permission request (e.g., show a dialog or navigate to settings)
            return
        }

        if (hasScanned.value && !isUserConfirmedRescan.value) {
            showRescanDialog.value = true
            return
        }

        isUserConfirmedRescan.value = false
        _isAnalyzing.value = true
        _showCleaningComposable.value = true
        viewModelScope.launch(CoroutineExceptionHandler { _ , exception ->
            _showErrorDialog.value = true
            _errorMessage.value = exception.message ?: "An error occurred during analysis."
            _isAnalyzing.value = false
        }) {
            val filteredFiles: List<File> = withContext(Dispatchers.IO) {
                fileScanner.startScanning()
                fileScanner.getFilteredFiles()
            }

            withContext(Dispatchers.Main) {
                fileSelectionStates.clear()
                _scannedFiles.value = filteredFiles
                _isAnalyzing.value = false
                hasScanned.value = true
            }
        }
    }

    fun rescan() {
        showRescanDialog.value = false
        _scannedFiles.value = emptyList()
        analyze()
    }

    /**
     * Initiates the cleaning process if the required permissions are granted.
     *
     * @param activity The Activity instance required to request permissions.
     */
    fun clean(activity: Activity) {
        if (!PermissionsUtils.hasStoragePermissions(getApplication())) {
            PermissionsUtils.requestStoragePermissions(activity)
            return
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, exception ->
            _showErrorDialog.value = true
            _errorMessage.value = exception.message ?: "An error occurred during cleaning."
        }) {
            val filesToDelete: Set<File> = fileSelectionStates.filter { it.value }.keys
            withContext(Dispatchers.IO) {
                filesToDelete.forEach { file ->
                    if (file.exists()) {
                        file.deleteRecursively()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                _scannedFiles.value = scannedFiles.value.filterNot { filesToDelete.contains(it) }
                fileSelectionStates.clear()
                selectAllFiles(selectAll = false)
                _selectedFileCount.value = 0
                updateStorageInfo()
            }
        }
    }


    fun getVideoThumbnail(filePath: String, callback: (Bitmap?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap: Bitmap? = try {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(filePath)
                val frame : Bitmap? = mediaMetadataRetriever.getFrameAtTime(0)
                mediaMetadataRetriever.release()
                frame
            } catch (e: Exception) {
                null
            }
            withContext(Dispatchers.Main) {
                callback(bitmap)
            }
        }
    }
}