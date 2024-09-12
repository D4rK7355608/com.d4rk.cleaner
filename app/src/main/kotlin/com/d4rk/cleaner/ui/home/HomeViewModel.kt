package com.d4rk.cleaner.ui.home

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.constants.error.ErrorType
import com.d4rk.cleaner.constants.error.ErrorType.STORAGE_PERMISSION
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.home.repository.HomeRepository
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.error.ErrorHandler
import com.d4rk.cleaner.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val repository = HomeRepository(DataStore(application), application.resources)
    val _uiState = MutableStateFlow(UiHomeModel())
    val uiState: StateFlow<UiHomeModel> = _uiState

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            updateStorageInfo()
        }
    }

    override fun handleError(errorType: ErrorType, exception: Throwable) {
        when (errorType) {
            ErrorType.ANALYSIS_ERROR -> _uiState.value = _uiState.value.copy(isAnalyzing = false)
            ErrorType.STORAGE_PERMISSION -> TODO()
            else -> super.handleError(errorType, exception)
        }
    }

    private suspend fun updateStorageInfo() {
        _uiState.value = repository.getStorageInfo(getApplication())
    }

    fun onFileSelectionChange(file: File, isChecked: Boolean) {
        val updatedFileSelectionStates = _uiState.value.fileSelectionStates + (file to isChecked)
        _uiState.value = _uiState.value.copy(
            fileSelectionStates = updatedFileSelectionStates,
            selectedFileCount = updatedFileSelectionStates.count { it.value },
            allFilesSelected = updatedFileSelectionStates.all { it.value } && updatedFileSelectionStates.isNotEmpty()
        )
    }

    fun selectAllFiles(selectAll: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val newFileSelectionStates = _uiState.value.scannedFiles.associateWith { selectAll }
            _uiState.value = _uiState.value.copy(
                allFilesSelected = selectAll,
                fileSelectionStates = newFileSelectionStates,
                selectedFileCount = if (selectAll) newFileSelectionStates.size else 0
            )
        }
    }

    fun analyze() {
        if (!PermissionsUtils.hasStoragePermissions(getApplication())) {
            ErrorHandler.handleError(getApplication(), STORAGE_PERMISSION)
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, showCleaningComposable = true)
            val filteredFiles = repository.analyzeFiles()

            _uiState.value = _uiState.value.copy(
                scannedFiles = filteredFiles,
                isAnalyzing = false,
            )
        }
    }

    fun clean(activity: Activity) {
        if (!PermissionsUtils.hasStoragePermissions(getApplication())) {
            PermissionsUtils.requestStoragePermissions(activity)
            return
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            val filesToDelete = _uiState.value.fileSelectionStates.filter { it.value }.keys
            repository.deleteFiles(filesToDelete)

            _uiState.value = _uiState.value.copy(
                scannedFiles = uiState.value.scannedFiles.filterNot { filesToDelete.contains(it) },
                selectedFileCount = 0,
                allFilesSelected = false,
                fileSelectionStates = emptyMap()
            )
            updateStorageInfo()
        }
    }

    fun getVideoThumbnail(filePath: String, context: Context, callback: (File?) -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val bitmap = repository.getVideoThumbnail(filePath)
            if (bitmap != null) {
                val thumbnailFile = File(context.cacheDir, "thumbnail_${filePath.hashCode()}.png")
                val savedSuccessfully = repository.saveBitmapToFile(bitmap, thumbnailFile)
                withContext(Dispatchers.Main) {
                    if (savedSuccessfully) {
                        callback(thumbnailFile)
                    } else {
                        callback(null)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

    fun rescan() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.value =
                _uiState.value.copy(showRescanDialog = false, scannedFiles = emptyList())
            analyze()
        }
    }
}