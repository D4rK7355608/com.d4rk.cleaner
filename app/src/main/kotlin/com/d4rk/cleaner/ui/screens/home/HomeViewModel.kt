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

/**
 * ViewModel for the home screen.
 * Manages the UI state and interacts with the repository to perform file operations.
 *
 * @param application The application instance.
 * @author Mihai-Cristian Condrea
 */
class HomeViewModel(application : Application) : BaseViewModel(application) {
    private val repository = HomeRepository(DataStore(application) , application)
    private val _uiState = MutableStateFlow(UiHomeModel())
    val uiState : StateFlow<UiHomeModel> = _uiState

    init {
        prepareScreenData()
    }

    /**
     * Loads initial screen data, including storage info and file types data.
     */
    private fun prepareScreenData() {
        updateStorageInfo()
        populateFileTypesData()
    }

    /**
     * Updates the storage information in the UI state.
     */
    private fun updateStorageInfo() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getStorageInfo { uiHomeModel ->
                _uiState.update {
                    it.copy(
                        storageUsageProgress = uiHomeModel.storageUsageProgress ,
                        usedStorageFormatted = uiHomeModel.usedStorageFormatted ,
                        totalStorageFormatted = uiHomeModel.totalStorageFormatted
                    )
                }
            }
        }
    }

    /**
     * Initiates file analysis and updates the UI state with the results.
     */
    fun analyze() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            repository.analyzeFiles { result ->
                val (filteredFiles , emptyFolders) = result
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        analyzeState = currentUiState.analyzeState.copy(
                            scannedFileList = filteredFiles ,
                            emptyFolderList = emptyFolders ,
                            isAnalyzeScreenVisible = true ,
                        )
                    )
                }
            }
            hideLoading()
        }
    }

    /**
     * Rescans files and updates the UI state with the new results.
     */
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
                            scannedFileList = filteredFiles , isAnalyzeScreenVisible = true
                        )
                    )
                }
            }
            hideLoading()
        }
    }

    /**
     * Hides the analyze screen.
     */
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

    /**
     * Updates the file selection state and selected file count.
     *
     * @param file The file whose selection state has changed.
     * @param isChecked True if the file is now selected, false otherwise.
     */
    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val updatedFileSelectionStates =
                    _uiState.value.analyzeState.fileSelectionMap + (file to isChecked)
            val newSelectedCount = updatedFileSelectionStates.count { it.value }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    analyzeState = currentUiState.analyzeState.copy(
                        fileSelectionMap = updatedFileSelectionStates ,
                        selectedFilesCount = newSelectedCount ,
                        areAllFilesSelected = when {
                            newSelectedCount == currentUiState.analyzeState.scannedFileList.size && newSelectedCount > 0 -> true
                            newSelectedCount == 0 -> false
                            isChecked -> currentUiState.analyzeState.areAllFilesSelected // Maintain 'select all' state if an item was checked and all were already checked
                            else -> false
                        }
                    )
                )
            }
        }
    }

    /**
     * Toggles the "select all" state for files.
     */
    fun toggleSelectAllFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val newState = ! _uiState.value.analyzeState.areAllFilesSelected
            _uiState.update { currentUiState ->
                currentUiState.copy(analyzeState = currentUiState.analyzeState.copy(areAllFilesSelected = newState ,
                                                                                    fileSelectionMap = if (newState) {
                                                                                        currentUiState.analyzeState.scannedFileList.associateWith { true }
                                                                                    }
                                                                                    else {
                                                                                        emptyMap()
                                                                                    } ,
                                                                                    selectedFilesCount = if (newState) {
                                                                                        currentUiState.analyzeState.scannedFileList.size
                                                                                    }
                                                                                    else {
                                                                                        0
                                                                                    }))
            }
        }
    }

    /**
     * Deletes selected files and updates the UI state.
     */
    fun clean() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToDelete =
                    _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys
            showLoading()
            repository.deleteFiles(filesToDelete) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(analyzeState = currentUiState.analyzeState.copy(
                        scannedFileList = currentUiState.analyzeState.scannedFileList.filterNot {
                            filesToDelete.contains(it)
                        } ,
                        selectedFilesCount = 0 ,
                        areAllFilesSelected = false ,
                        fileSelectionMap = emptyMap() ,
                        isAnalyzeScreenVisible = false ,
                    ))
                }
                updateStorageInfo()
            }
            hideLoading()
        }
    }

    /**
     * Moves selected files to the trash and updates the UI state.
     */
    fun moveToTrash() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToMove =
                    _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys.toList()
            showLoading()
            repository.moveToTrash(filesToMove) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(analyzeState = currentUiState.analyzeState.copy(
                        scannedFileList = currentUiState.analyzeState.scannedFileList.filterNot { existingFile ->
                            filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                        } ,
                        selectedFilesCount = 0 ,
                        areAllFilesSelected = false ,
                        fileSelectionMap = emptyMap()))
                }
                updateStorageInfo()
            }
            hideLoading()
        }
    }

    /**
     * Populates file types data in the UI state.
     */
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

    /**
     * Sets the visibility of the "delete forever" confirmation dialog.
     * @param isVisible True to show the dialog, false to hide it.
     */
    fun setDeleteForeverConfirmationDialogVisibility(isVisible : Boolean) {
        _uiState.update {
            it.copy(analyzeState = it.analyzeState.copy(isDeleteForeverConfirmationDialogVisible = isVisible))
        }
    }

    /**
     * Sets the visibility of the "move to trash" confirmation dialog.
     * @param isVisible True to show the dialog, false to hide it.
     */
    fun setMoveToTrashConfirmationDialogVisibility(isVisible : Boolean) {
        _uiState.update {
            it.copy(analyzeState = it.analyzeState.copy(isMoveToTrashConfirmationDialogVisible = isVisible))
        }
    }
}