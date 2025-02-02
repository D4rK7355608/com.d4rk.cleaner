package com.d4rk.cleaner.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.screens.home.repository.HomeRepository
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import com.d4rk.cleaner.utils.constants.cleaning.ExtensionsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(application : Application) : BaseViewModel(application) {
    private val repository : HomeRepository = HomeRepository(dataStore = AppCoreManager.dataStore , application = application)
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
                val (scannedFiles : List<File> , emptyFolders : List<File>) = result
                val currentFileTypesData : FileTypesData = _uiState.value.analyzeState.fileTypesData

                viewModelScope.launch(context = Dispatchers.IO) {
                    val prefs : Map<String , Boolean> = repository.getPreferences()
                    val groupedFiles : Map<String , List<File>> = computeGroupedFiles(scannedFiles = scannedFiles , emptyFolders = emptyFolders , fileTypesData = currentFileTypesData , preferences = prefs)
                    _uiState.update { state ->
                        state.copy(analyzeState = state.analyzeState.copy(scannedFileList = scannedFiles , emptyFolderList = emptyFolders , isAnalyzeScreenVisible = true , groupedFiles = groupedFiles))
                    }
                }
            }
            hideLoading()
        }
    }

    private fun computeGroupedFiles(
        scannedFiles : List<File> , emptyFolders : List<File> , fileTypesData : FileTypesData , preferences : Map<String , Boolean>
    ) : Map<String , List<File>> {
        val knownExtensions : Set<String> =
                (fileTypesData.imageExtensions + fileTypesData.videoExtensions + fileTypesData.audioExtensions + fileTypesData.officeExtensions + fileTypesData.archiveExtensions + fileTypesData.apkExtensions + fileTypesData.fontExtensions + fileTypesData.windowsExtensions).toSet()

        val filesMap : LinkedHashMap<String , MutableList<File>> = linkedMapOf()
        filesMap.putAll(fileTypesData.fileTypesTitles.associateWith { mutableListOf() })

        scannedFiles.forEach { file ->
            val category : String? = when (val extension : String = file.extension.lowercase()) {
                in fileTypesData.imageExtensions -> if (preferences[ExtensionsConstants.IMAGE_EXTENSIONS] == true) fileTypesData.fileTypesTitles[0] else null
                in fileTypesData.videoExtensions -> if (preferences[ExtensionsConstants.VIDEO_EXTENSIONS] == true) fileTypesData.fileTypesTitles[1] else null
                in fileTypesData.audioExtensions -> if (preferences[ExtensionsConstants.AUDIO_EXTENSIONS] == true) fileTypesData.fileTypesTitles[2] else null
                in fileTypesData.officeExtensions -> if (preferences[ExtensionsConstants.OFFICE_EXTENSIONS] == true) fileTypesData.fileTypesTitles[3] else null
                in fileTypesData.archiveExtensions -> if (preferences[ExtensionsConstants.ARCHIVE_EXTENSIONS] == true) fileTypesData.fileTypesTitles[4] else null
                in fileTypesData.apkExtensions -> if (preferences[ExtensionsConstants.APK_EXTENSIONS] == true) fileTypesData.fileTypesTitles[5] else null
                in fileTypesData.fontExtensions -> if (preferences[ExtensionsConstants.FONT_EXTENSIONS] == true) fileTypesData.fileTypesTitles[6] else null
                in fileTypesData.windowsExtensions -> if (preferences[ExtensionsConstants.WINDOWS_EXTENSIONS] == true) fileTypesData.fileTypesTitles[7] else null
                else -> if (! knownExtensions.contains(extension) && preferences[ExtensionsConstants.OTHER_EXTENSIONS] == true) fileTypesData.fileTypesTitles[9] else null
            }
            category?.let { filesMap[it]?.add(file) }
        }

        if (emptyFolders.isNotEmpty() && preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) {
            filesMap[fileTypesData.fileTypesTitles[8]] = emptyFolders.toMutableList()
        }

        return filesMap.filter { it.value.isNotEmpty() }
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
            val visibleFiles : List<File> = _uiState.value.analyzeState.groupedFiles.values.flatten()
            val selectedVisibleCount : Int = updatedFileSelectionStates.filterKeys { it in visibleFiles }.count { it.value }

            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(fileSelectionMap = updatedFileSelectionStates , selectedFilesCount = selectedVisibleCount , areAllFilesSelected = selectedVisibleCount == visibleFiles.size && visibleFiles.isNotEmpty()))
            }
        }
    }

    fun toggleSelectAllFiles() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val newState : Boolean = ! _uiState.value.analyzeState.areAllFilesSelected
            val visibleFiles : List<File> = _uiState.value.analyzeState.groupedFiles.values.flatten()

            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(areAllFilesSelected = newState , fileSelectionMap = if (newState) visibleFiles.associateWith { true } else emptyMap() , selectedFilesCount = if (newState) visibleFiles.size else 0))
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
                        state.copy(analyzeState = state.analyzeState.copy(scannedFileList = state.analyzeState.scannedFileList.filterNot { filesToDelete.contains(it) } , selectedFilesCount = 0 , areAllFilesSelected = false , fileSelectionMap = emptyMap() , isAnalyzeScreenVisible = false))
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