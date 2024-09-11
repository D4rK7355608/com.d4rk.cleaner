package com.d4rk.cleaner.ui.home

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.R
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
            STORAGE_PERMISSION -> _uiState.value = _uiState.value.copy(
                showErrorDialog = true,
                errorMessage = getApplication<Application>().getString(R.string.storage_permission_error)
            )
            ErrorType.CLEANING_ERROR -> _uiState.value = _uiState.value.copy(
                showErrorDialog = true,
                errorMessage = exception.message
                    ?: getApplication<Application>().getString(R.string.cleaning_error)
            )
            ErrorType.UNKNOWN_ERROR -> _uiState.value = _uiState.value.copy(
                showErrorDialog = true,
                errorMessage = getApplication<Application>().getString(R.string.unknown_error)
            )
        }
    }

    private suspend fun updateStorageInfo() {
        _uiState.value = repository.getStorageInfo(getApplication())
    }

    fun onFileSelectionChange(file: File, isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(
            fileSelectionStates = _uiState.value.fileSelectionStates + (file to isChecked),
            selectedFileCount = _uiState.value.fileSelectionStates.count { it.value } + if (isChecked) 1 else 0, // Update count based on isChecked
            allFilesSelected = (_uiState.value.fileSelectionStates + (file to isChecked)).all { it.value } // Update allFilesSelected
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

    fun getVideoThumbnail(filePath: String, callback: (Bitmap?) -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val bitmap = repository.getVideoThumbnail(filePath)
            withContext(Dispatchers.Main) {
                callback(bitmap)
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

    fun dismissErrorDialog() {
        _uiState.value = _uiState.value.copy(showErrorDialog = false)
    }
}