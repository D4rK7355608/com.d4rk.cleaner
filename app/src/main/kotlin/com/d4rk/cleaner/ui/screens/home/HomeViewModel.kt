package com.d4rk.cleaner.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
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
import java.util.LinkedHashMap

class HomeViewModel(application : Application) : BaseViewModel(application) {
    private val repository : HomeRepository = HomeRepository(DataStore(application) , application)
    private val _uiState : MutableStateFlow<UiHomeModel> = MutableStateFlow(UiHomeModel())
    val uiState : StateFlow<UiHomeModel> = _uiState

    init {
        getStorageInfo()
        getFileTypes()
        loadCleanedSpace()
    }

    fun analyze() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            repository.analyzeFiles { result ->
                val (filteredFiles : List<File> , emptyFolders : List<File>) = result
                val currentFileTypesData : FileTypesData = _uiState.value.analyzeState.fileTypesData
                val groupedFiles : Map<String , List<File>> = computeGroupedFiles(filteredFiles , emptyFolders , currentFileTypesData)
                _uiState.update { state ->
                    state.copy(analyzeState = state.analyzeState.copy(scannedFileList = filteredFiles , emptyFolderList = emptyFolders , isAnalyzeScreenVisible = true , groupedFiles = groupedFiles))
                }
            }
            hideLoading()
        }
    }

    private fun computeGroupedFiles(
        scannedFiles : List<File> , emptyFolders : List<File> , fileTypesData : FileTypesData
    ) : Map<String , List<File>> {
        val filesMap : LinkedHashMap<String , MutableList<File>> = linkedMapOf()
        filesMap.putAll(fileTypesData.fileTypesTitles.associateWith { mutableListOf() })

        scannedFiles.forEach { file ->
            val extension = file.extension.lowercase()
            val category = when (extension) {
                in fileTypesData.imageExtensions -> fileTypesData.fileTypesTitles[0]
                in fileTypesData.videoExtensions -> fileTypesData.fileTypesTitles[1]
                in fileTypesData.audioExtensions -> fileTypesData.fileTypesTitles[2]
                in fileTypesData.officeExtensions -> fileTypesData.fileTypesTitles[3]
                in fileTypesData.archiveExtensions -> fileTypesData.fileTypesTitles[4]
                in fileTypesData.apkExtensions -> fileTypesData.fileTypesTitles[5]
                in fileTypesData.fontExtensions -> fileTypesData.fileTypesTitles[6]
                in fileTypesData.windowsExtensions -> fileTypesData.fileTypesTitles[7]
                in fileTypesData.otherExtensions -> fileTypesData.fileTypesTitles[9]
                else -> fileTypesData.fileTypesTitles[9]
            }

            filesMap[category]?.add(file)
        }

        if (emptyFolders.isNotEmpty()) {
            filesMap[fileTypesData.fileTypesTitles[8]] = emptyFolders.toMutableList()
        }

        val orderedFilesMap : Map<String , MutableList<File>> = filesMap.filter { it.value.isNotEmpty() }

        return orderedFilesMap
    }

    fun rescanFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(scannedFileList = emptyList()))
            }

            repository.rescanFiles { filteredFiles ->
                _uiState.update { state ->
                    state.copy(analyzeState = state.analyzeState.copy(scannedFileList = filteredFiles , isAnalyzeScreenVisible = true))
                }
            }
            hideLoading()
        }
    }

    fun onCloseAnalyzeComposable() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(isAnalyzeScreenVisible = false))
            }
        }
    }

    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val updatedFileSelectionStates : Map<File , Boolean> = _uiState.value.analyzeState.fileSelectionMap + (file to isChecked)
            val newSelectedCount : Int = updatedFileSelectionStates.count { it.value }

            _uiState.update { state ->
                state.copy(
                    analyzeState = state.analyzeState.copy(
                        fileSelectionMap = updatedFileSelectionStates , selectedFilesCount = newSelectedCount , areAllFilesSelected = when {
                            newSelectedCount == state.analyzeState.scannedFileList.size && newSelectedCount > 0 -> true
                            newSelectedCount == 0 -> false
                            isChecked -> state.analyzeState.areAllFilesSelected
                            else -> false
                        }
                    )
                )
            }
        }
    }

    fun toggleSelectAllFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val newState : Boolean = ! _uiState.value.analyzeState.areAllFilesSelected
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(areAllFilesSelected = newState , fileSelectionMap = when {
                    newState -> (state.analyzeState.scannedFileList + state.analyzeState.emptyFolderList).associateWith { true }
                    else -> emptyMap()
                } , selectedFilesCount = if (newState) {
                    state.analyzeState.scannedFileList.size + state.analyzeState.emptyFolderList.size
                }
                else {
                    0
                }))
            }
        }
    }

    fun clean() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            val filesToDelete : Set<File> = _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys
            val clearedSpaceTotalSize : Long = filesToDelete.sumOf { it.length() }
            with(repository) {
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
                with(dataStore) {
                    addCleanedSpace(space = clearedSpaceTotalSize)
                    saveLastScanTimestamp(timestamp = System.currentTimeMillis())
                }
            }
            hideLoading()
        }
    }

    fun moveToTrash() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            val filesToMove : List<File> = _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys.toList()
            val totalFileSizeToMove : Long = filesToMove.sumOf { it.length() }
            with(repository) {
                moveToTrashRepository(filesToMove = filesToMove) {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(analyzeState = currentUiState.analyzeState.copy(scannedFileList = currentUiState.analyzeState.scannedFileList.filterNot { existingFile ->
                            filesToMove.any { movedFile ->
                                existingFile.absolutePath == movedFile.absolutePath
                            }
                        } , selectedFilesCount = 0 , areAllFilesSelected = false , isAnalyzeScreenVisible = false , fileSelectionMap = emptyMap()))
                    }
                    getStorageInfo()
                }
                addTrashSize(size = totalFileSizeToMove)
            }
            hideLoading()
        }
    }

    private fun getFileTypes() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getFileTypesRepository { fileTypesData ->
                _uiState.update { state ->
                    state.copy(analyzeState = state.analyzeState.copy(fileTypesData = fileTypesData))
                }
            }
        }
    }

    fun setDeleteForeverConfirmationDialogVisibility(isVisible : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(isDeleteForeverConfirmationDialogVisible = isVisible))
            }
        }
    }

    fun setMoveToTrashConfirmationDialogVisibility(isVisible : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(isMoveToTrashConfirmationDialogVisible = isVisible))
            }
        }
    }

    private fun getStorageInfo() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getStorageInfoRepository { uiHomeModel ->
                _uiState.update { state ->
                    state.copy(storageInfo = state.storageInfo.copy(storageUsageProgress = uiHomeModel.storageInfo.storageUsageProgress , freeSpacePercentage = uiHomeModel.storageInfo.freeSpacePercentage))
                }
            }
        }
    }

    private fun loadCleanedSpace() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            with(repository) {
                dataStore.cleanedSpace.collect { cleanedSpace ->
                    _uiState.update { state ->
                        state.copy(storageInfo = state.storageInfo.copy(cleanedSpace = StorageUtils.formatSize(cleanedSpace)))
                    }
                }
            }
        }
    }
}