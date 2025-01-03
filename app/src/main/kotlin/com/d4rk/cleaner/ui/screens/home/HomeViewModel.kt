package com.d4rk.cleaner.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.screens.home.repository.HomeRepository
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
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
class HomeViewModel(application : Application) : BaseViewModel(application = application) {
    private val repository : HomeRepository = HomeRepository(DataStore(application) , application)
    private val _uiState : MutableStateFlow<UiHomeModel> = MutableStateFlow(UiHomeModel())
    val uiState : StateFlow<UiHomeModel> = _uiState

    init {
        getStorageInfo()
        getFileTypes()
        loadCleanedSpace()
    }

    /**
     * Updates the storage information (used and total storage) in the UI state.
     */
    private fun getStorageInfo() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getStorageInfoRepository { uiHomeModel ->
                _uiState.update { state ->
                    state.copy(
                        storageInfo = state.storageInfo.copy(
                            storageUsageProgress = uiHomeModel.storageInfo.storageUsageProgress ,
                            freeSpacePercentage = uiHomeModel.storageInfo.freeSpacePercentage
                        )
                    )
                }
            }
        }
    }

    /**
     * Loads the cleaned space and last scan information from DataStore and updates the UI state.
     */
    private fun loadCleanedSpace() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            with(receiver = repository) {
                dataStore.cleanedSpace.collect { cleanedSpace ->
                    _uiState.update { state ->
                        state.copy(
                            storageInfo = state.storageInfo.copy(
                                cleanedSpace = StorageUtils.formatSize(cleanedSpace)
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Analyzes files to find duplicates, empty folders, and other relevant information.
     * Updates the UI state with the analysis results.
     */
    fun analyze() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            repository.analyzeFiles { result ->
                val (filteredFiles : List<File> , emptyFolders : List<File>) = result
                _uiState.update { state ->
                    state.copy(
                        analyzeState = state.analyzeState.copy(
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
            _uiState.update { state ->
                state.copy(
                    analyzeState = state.analyzeState.copy(
                        scannedFileList = emptyList()
                    )
                )
            }

            repository.rescanFiles { filteredFiles ->
                _uiState.update { state ->
                    state.copy(
                        analyzeState = state.analyzeState.copy(
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
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(
                    analyzeState = state.analyzeState.copy(
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
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val updatedFileSelectionStates : Map<File , Boolean> =
                    _uiState.value.analyzeState.fileSelectionMap + (file to isChecked)
            val newSelectedCount : Int = updatedFileSelectionStates.count { it.value }

            _uiState.update { state ->
                state.copy(
                    analyzeState = state.analyzeState.copy(
                        fileSelectionMap = updatedFileSelectionStates ,
                        selectedFilesCount = newSelectedCount ,
                        areAllFilesSelected = when {
                            newSelectedCount == state.analyzeState.scannedFileList.size && newSelectedCount > 0 -> {
                                true
                            }

                            newSelectedCount == 0 -> {
                                false
                            }

                            isChecked -> {
                                state.analyzeState.areAllFilesSelected
                            }

                            else -> {
                                false
                            }
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
            val newState : Boolean = ! _uiState.value.analyzeState.areAllFilesSelected
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(areAllFilesSelected = newState ,
                                                                  fileSelectionMap = when {
                                                                      newState -> {
                                                                          (state.analyzeState.scannedFileList + state.analyzeState.emptyFolderList).associateWith { true }
                                                                      }

                                                                      else -> {
                                                                          emptyMap()
                                                                      }
                                                                  } ,
                                                                  selectedFilesCount = if (newState) {
                                                                      (state.analyzeState.scannedFileList.size + state.analyzeState.emptyFolderList.size)
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
            showLoading()
            val filesToDelete : Set<File> =
                    _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys
            val clearedSpaceTotalSize : Long = filesToDelete.sumOf { it.length() }
            with(receiver = repository) {
                deleteFilesRepository(filesToDelete = filesToDelete) {
                    _uiState.update { state ->
                        state.copy(analyzeState = state.analyzeState.copy(
                            scannedFileList = state.analyzeState.scannedFileList.filterNot {
                                filesToDelete.contains(it)
                            } ,
                            selectedFilesCount = 0 ,
                            areAllFilesSelected = false ,
                            fileSelectionMap = emptyMap() ,
                            isAnalyzeScreenVisible = false ,
                        ))
                    }
                    getStorageInfo()
                }
                with(receiver = dataStore) {
                    addCleanedSpace(space = clearedSpaceTotalSize)
                    saveLastScanTimestamp(timestamp = System.currentTimeMillis())
                }
            }
            hideLoading()
        }
    }

    /**
     * Moves selected files to the trash and updates the UI state.
     */
    fun moveToTrash() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            val filesToMove : List<File> =
                    _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys.toList()
            val totalFileSizeToMove : Long = filesToMove.sumOf { it.length() }
            with(receiver = repository) {
                moveToTrashRepository(filesToMove = filesToMove) {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(analyzeState = currentUiState.analyzeState.copy(
                            scannedFileList = currentUiState.analyzeState.scannedFileList.filterNot { existingFile ->
                                filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                            } ,
                            selectedFilesCount = 0 ,
                            areAllFilesSelected = false ,
                            isAnalyzeScreenVisible = false ,
                            fileSelectionMap = emptyMap()))
                    }
                    getStorageInfo()
                }
                addTrashSize(size = totalFileSizeToMove)
            }
            hideLoading()
        }
    }

    /**
     * Populates file types data in the UI state.
     */
    private fun getFileTypes() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getFileTypesRepository { fileTypesData ->
                _uiState.update { state ->
                    state.copy(
                        analyzeState = state.analyzeState.copy(
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
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(
                    analyzeState = state.analyzeState.copy(
                        isDeleteForeverConfirmationDialogVisible = isVisible
                    )
                )
            }
        }
    }

    /**
     * Sets the visibility of the "move to trash" confirmation dialog.
     * @param isVisible True to show the dialog, false to hide it.
     */
    fun setMoveToTrashConfirmationDialogVisibility(isVisible : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(
                    analyzeState = state.analyzeState.copy(
                        isMoveToTrashConfirmationDialogVisible = isVisible
                    )
                )
            }
        }
    }
}