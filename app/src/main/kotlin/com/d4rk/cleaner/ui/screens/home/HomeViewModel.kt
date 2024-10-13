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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val repository = HomeRepository(DataStore(application), application)
    private val _uiState = MutableStateFlow(UiHomeModel())
    val uiState: StateFlow<UiHomeModel> = _uiState

    init {
        updateStorageInfo()
        populateFileTypesData()
    }

    private fun updateStorageInfo() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getStorageInfo { uiHomeModel ->
                _uiState.update { it.copy(
                    storageUsageProgress = uiHomeModel.storageUsageProgress,
                    usedStorageFormatted = uiHomeModel.usedStorageFormatted,
                    totalStorageFormatted = uiHomeModel.totalStorageFormatted
                ) }
            }
        }
    }

    fun analyze() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            repository.analyzeFiles { result ->
                val filteredFiles = result.first
                val emptyFolders = result.second
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        analyzeState = currentUiState.analyzeState.copy(
                            scannedFileList = filteredFiles,
                            emptyFolderList = emptyFolders,
                            isAnalyzeScreenVisible = true,
                            isFileScanEmpty = filteredFiles.isEmpty() && emptyFolders.isEmpty()
                        )
                    )
                }
            }
            hideLoading()
        }
    }

    fun rescanFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    analyzeState = currentUiState.analyzeState.copy(
                        scannedFileList = emptyList()
                    )
                )
            }
            repository.rescanFiles { filteredFiles ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        analyzeState = currentUiState.analyzeState.copy(
                            scannedFileList = filteredFiles,
                            isAnalyzeScreenVisible = true
                        )
                    )
                }
            }
            hideLoading()
        }
    }

    fun onCloseAnalyzeComposable() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    analyzeState = currentUiState.analyzeState.copy(
                        isAnalyzeScreenVisible = false
                    )
                )
            }
        }
    }

    fun onFileSelectionChange(file: File, isChecked: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val updatedFileSelectionStates = _uiState.value.analyzeState.fileSelectionMap + (file to isChecked)
            val newSelectedCount = updatedFileSelectionStates.count { it.value }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    analyzeState = currentUiState.analyzeState.copy(
                        fileSelectionMap = updatedFileSelectionStates,
                        selectedFilesCount = newSelectedCount,
                        areAllFilesSelected = when {
                            newSelectedCount == currentUiState.analyzeState.scannedFileList.size && newSelectedCount > 0 -> true
                            newSelectedCount == 0 -> false
                            isChecked -> currentUiState.analyzeState.areAllFilesSelected
                            else -> false
                        }
                    )
                )
            }
        }
    }

    fun toggleSelectAllFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val newState = !_uiState.value.analyzeState.areAllFilesSelected
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    analyzeState = currentUiState.analyzeState.copy(
                        areAllFilesSelected = newState,
                        fileSelectionMap = if (newState) {
                            currentUiState.analyzeState.scannedFileList.associateWith { true }
                        } else {
                            emptyMap()
                        },
                        selectedFilesCount = if (newState) {
                            currentUiState.analyzeState.scannedFileList.size
                        } else {
                            0
                        }
                    )
                )
            }
        }
    }

    fun clean() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToDelete = _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys
            showLoading()
            repository.deleteFiles(filesToDelete) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        analyzeState = currentUiState.analyzeState.copy(
                            scannedFileList = currentUiState.analyzeState.scannedFileList.filterNot {
                                filesToDelete.contains(it)
                            },
                            selectedFilesCount = 0,
                            areAllFilesSelected = false,
                            fileSelectionMap = emptyMap()
                        )
                    )
                }
                updateStorageInfo()
            }
            hideLoading()
        }
    }

    fun moveToTrash() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToMove = _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys.toList()
            showLoading()
            repository.moveToTrash(filesToMove) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        analyzeState = currentUiState.analyzeState.copy(
                            scannedFileList = currentUiState.analyzeState.scannedFileList.filterNot { existingFile ->
                                filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                            },
                            selectedFilesCount = 0,
                            areAllFilesSelected = false,
                            fileSelectionMap = emptyMap()
                        )
                    )
                }
                updateStorageInfo()
            }
            hideLoading()
        }
    }

    private fun populateFileTypesData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getFileTypesData { fileTypesData ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        analyzeState = currentUiState.analyzeState.copy(
                            fileTypesData = fileTypesData
                        )
                    )
                }
            }
        }
    }
}