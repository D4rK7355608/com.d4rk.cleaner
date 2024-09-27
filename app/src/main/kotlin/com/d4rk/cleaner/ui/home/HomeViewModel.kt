package com.d4rk.cleaner.ui.home

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.home.repository.HomeRepository
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(application : Application) : BaseViewModel(application) {
    private val repository = HomeRepository(DataStore(application) , application)
    private val _uiState = MutableStateFlow(UiHomeModel())
    val uiState : StateFlow<UiHomeModel> = _uiState

    init {
        updateStorageInfo()
    }

    private fun updateStorageInfo() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getStorageInfo { uiHomeModel ->
                _uiState.value = uiHomeModel
            }
        }
    }

    fun analyze() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)
            repository.analyzeFiles { filteredFiles ->
                _uiState.value = _uiState.value.copy(
                    scannedFiles = filteredFiles ,
                    isAnalyzing = false ,
                    showCleaningComposable = true
                )
            }
        }
    }

    fun getVideoThumbnail(filePath : String , context : Context , callback : (File?) -> Unit) {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            repository.getVideoThumbnail(filePath , context) { thumbnailFile ->
                callback(thumbnailFile)
            }
        }
    }

    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val updatedFileSelectionStates =
                    _uiState.value.fileSelectionStates + (file to isChecked)
            val newSelectedCount = updatedFileSelectionStates.count { it.value }

            _uiState.value = _uiState.value.copy(
                fileSelectionStates = updatedFileSelectionStates ,
                selectedFileCount = newSelectedCount ,
                allFilesSelected = when {
                    newSelectedCount == _uiState.value.scannedFiles.size && newSelectedCount > 0 -> {
                        true
                    }

                    newSelectedCount == 0 -> {
                        false
                    }

                    isChecked -> {
                        _uiState.value.allFilesSelected
                    }

                    else -> {
                        false
                    }
                }
            )
        }
    }

    fun toggleSelectAllFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val newState = ! _uiState.value.allFilesSelected
            _uiState.value = _uiState.value.copy(allFilesSelected = newState ,
                                                 fileSelectionStates = if (newState) {
                                                     _uiState.value.scannedFiles.associateWith { true }
                                                 }
                                                 else {
                                                     emptyMap()
                                                 } ,
                                                 selectedFileCount = if (newState) {
                                                     _uiState.value.scannedFiles.size
                                                 }
                                                 else {
                                                     0
                                                 })
        }
    }

    fun clean(activity : Activity) {
        if (! PermissionsUtils.hasStoragePermissions(getApplication())) {
            PermissionsUtils.requestStoragePermissions(activity)
            return
        }

        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToDelete = _uiState.value.fileSelectionStates.filter { it.value }.keys
            repository.deleteFiles(filesToDelete)

            _uiState.value = _uiState.value.copy(
                scannedFiles = uiState.value.scannedFiles.filterNot { filesToDelete.contains(it) } ,
                selectedFileCount = 0 ,
                allFilesSelected = false ,
                fileSelectionStates = emptyMap()
            )
            updateStorageInfo()
        }
    }

    fun rescan() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.value =
                    _uiState.value.copy(showRescanDialog = false , scannedFiles = emptyList())
            analyze()
        }
    }
}