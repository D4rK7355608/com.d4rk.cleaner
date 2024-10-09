package com.d4rk.cleaner.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.screens.home.repository.HomeRepository
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
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
            showLoading()
            repository.analyzeFiles { result ->
                val filteredFiles = result.first
                val emptyFolders = result.second

                _uiState.value = _uiState.value.copy(
                    scannedFiles = filteredFiles ,
                    emptyFolders = emptyFolders ,
                    showCleaningComposable = true ,
                    noFilesFound = filteredFiles.isEmpty() && emptyFolders.isEmpty() ,
                )
            }
            hideLoading()
        }
    }

    fun onCloseAnalyzeComposable() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.value = _uiState.value.copy(showCleaningComposable = false)
        }
    }

    fun rescanFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            _uiState.value = _uiState.value.copy(scannedFiles = emptyList())
            repository.rescanFiles { filteredFiles ->
                _uiState.value = _uiState.value.copy(
                    scannedFiles = filteredFiles , showCleaningComposable = true
                )
            }
            hideLoading()
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

    fun clean() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToDelete = _uiState.value.fileSelectionStates.filter { it.value }.keys
            showLoading()
            repository.deleteFiles(filesToDelete) {
                _uiState.value =
                        _uiState.value.copy(scannedFiles = uiState.value.scannedFiles.filterNot {
                            filesToDelete.contains(it)
                        } ,
                                            selectedFileCount = 0 ,
                                            allFilesSelected = false ,
                                            fileSelectionStates = emptyMap())
                updateStorageInfo()
            }
            hideLoading()
        }
    }

    fun moveToTrash() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToMove = _uiState.value.fileSelectionStates.filter { it.value }.keys.toList()
            showLoading()
            repository.moveToTrash(filesToMove) {
                _uiState.value =
                        _uiState.value.copy(scannedFiles = uiState.value.scannedFiles.filterNot { existingFile ->
                            filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                        } ,
                                            selectedFileCount = 0 ,
                                            allFilesSelected = false ,
                                            fileSelectionStates = emptyMap())
                updateStorageInfo()
            }
            hideLoading()
        }
    }
}