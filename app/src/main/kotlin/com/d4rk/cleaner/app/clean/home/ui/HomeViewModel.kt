package com.d4rk.cleaner.app.clean.home.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.setLoading
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.clean.home.domain.actions.HomeAction
import com.d4rk.cleaner.app.clean.home.domain.actions.HomeEvent
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.FileTypesData
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.UiHomeModel
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.CleaningState
import com.d4rk.cleaner.app.clean.home.domain.usecases.AnalyzeFilesUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.DeleteFilesUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.GetFileTypesUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.GetStorageInfoUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.MoveToTrashUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.UpdateTrashSizeUseCase
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.settings.cleaning.utils.constants.ExtensionsConstants
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getStorageInfoUseCase : GetStorageInfoUseCase ,
    private val getFileTypesUseCase : GetFileTypesUseCase ,
    private val analyzeFilesUseCase : AnalyzeFilesUseCase ,
    private val deleteFilesUseCase : DeleteFilesUseCase ,
    private val moveToTrashUseCase : MoveToTrashUseCase ,
    private val updateTrashSizeUseCase : UpdateTrashSizeUseCase ,
    private val dispatchers : DispatcherProvider ,
    private val dataStore : DataStore
) : ScreenViewModel<UiHomeModel , HomeEvent , HomeAction>(
    initialState = UiStateScreen(data = UiHomeModel())
) {

    init {
        onEvent(HomeEvent.LoadInitialData)
        loadCleanedSpace()
    }

    override fun onEvent(event : HomeEvent) {
        when (event) {
            is HomeEvent.LoadInitialData -> loadInitialData()
            is HomeEvent.AnalyzeFiles -> analyzeFiles()
            is HomeEvent.RefreshData -> refreshData()
            is HomeEvent.DeleteFiles -> deleteFiles(files = event.files)
            is HomeEvent.MoveToTrash -> moveToTrash(files = event.files)
            is HomeEvent.ToggleAnalyzeScreen -> toggleAnalyzeScreen(visible = event.visible)
            is HomeEvent.OnFileSelectionChange -> onFileSelectionChange(file = event.file , isChecked = event.isChecked)
            is HomeEvent.ToggleSelectAllFiles -> toggleSelectAllFiles()
            is HomeEvent.ToggleSelectFilesForCategory -> toggleSelectFilesForCategory(category = event.category)
            is HomeEvent.CleanFiles -> cleanFiles()
            is HomeEvent.MoveSelectedToTrash -> moveSelectedToTrash()
            is HomeEvent.SetDeleteForeverConfirmationDialogVisibility -> setDeleteForeverConfirmationDialogVisibility(isVisible = event.isVisible)
            is HomeEvent.SetMoveToTrashConfirmationDialogVisibility -> setMoveToTrashConfirmationDialogVisibility(isVisible = event.isVisible)
        }
    }

    private fun loadInitialData() {
        launch(context = dispatchers.io) {
            _uiState.setLoading()
            _uiState.updateData(newState = _uiState.value.screenState) { currentData : UiHomeModel ->
                currentData.copy(storageInfo = currentData.storageInfo.copy(isCleanedSpaceLoading = true , isFreeSpaceLoading = true))
            }

            combine<DataState<UiHomeModel , Errors> , DataState<FileTypesData , Errors> , Long , Triple<DataState<UiHomeModel , Errors> , DataState<FileTypesData , Errors> , Long>>(
                flow = getStorageInfoUseCase() , flow2 = getFileTypesUseCase() , flow3 = dataStore.cleanedSpace.distinctUntilChanged()
            ) { storageState : DataState<UiHomeModel , Errors> , fileTypesState : DataState<FileTypesData , Errors> , cleanedSpaceValue : Long ->

                Triple(first = storageState , second = fileTypesState , third = cleanedSpaceValue)
            }.collect { (storageState : DataState<UiHomeModel , Errors> , fileTypesState : DataState<FileTypesData , Errors> , cleanedSpaceValue : Long) ->
                _uiState.update { currentState : UiStateScreen<UiHomeModel> ->
                    val currentData : UiHomeModel = currentState.data ?: UiHomeModel()
                    val updatedStorageInfo : StorageInfo = currentData.storageInfo.copy(isFreeSpaceLoading = storageState is DataState.Loading , isCleanedSpaceLoading = false , cleanedSpace = "$cleanedSpaceValue KB")
                    val finalStorageInfo = if (storageState is DataState.Success) {
                        storageState.data.storageInfo.copy(cleanedSpace = updatedStorageInfo.cleanedSpace , isCleanedSpaceLoading = updatedStorageInfo.isCleanedSpaceLoading , isFreeSpaceLoading = false)
                    }
                    else {
                        updatedStorageInfo
                    }

                    val updatedData : UiHomeModel = currentData.copy(storageInfo = finalStorageInfo , analyzeState = currentData.analyzeState.copy(fileTypesData = if (fileTypesState is DataState.Success) fileTypesState.data else currentData.analyzeState.fileTypesData))
                    val errors : List<UiSnackbar> = buildList {
                        if (storageState is DataState.Error) {
                            add(element = UiSnackbar(message = UiTextHelper.DynamicString(content = "Failed to load storage info: ${storageState.error}") , isError = true))
                        }
                        if (fileTypesState is DataState.Error) {
                            if (storageState !is DataState.Error || storageState.error != fileTypesState.error) {
                                add(element = UiSnackbar(message = UiTextHelper.DynamicString("Failed to load file types: ${fileTypesState.error}") , isError = true))
                            }
                        }
                    }.takeIf { it.isNotEmpty() } ?: emptyList()

                    currentState.copy(
                        screenState = when {
                            storageState is DataState.Loading || fileTypesState is DataState.Loading -> ScreenState.IsLoading()
                            storageState is DataState.Error || fileTypesState is DataState.Error -> ScreenState.Error()
                            storageState is DataState.Success && fileTypesState is DataState.Success -> ScreenState.Success()
                            else -> currentState.screenState
                        } , data = updatedData , errors = currentState.errors + errors , snackbar = currentState.snackbar
                    )
                }
            }
        }
    }

    private fun refreshData() {
        loadInitialData()
        if (screenData?.analyzeState?.isAnalyzeScreenVisible == true) {
            analyzeFiles()
        }
    }

    private fun analyzeFiles() {
        if (_uiState.value.data?.analyzeState?.state != CleaningState.Idle) {
            return
        }

        launch(dispatchers.io) {
            analyzeFilesUseCase().collectLatest { result : DataState<Pair<List<File> , List<File>> , Errors> ->
                _uiState.update { currentState : UiStateScreen<UiHomeModel> ->
                    val currentData : UiHomeModel = currentState.data ?: UiHomeModel()
                    when (result) {
                        is DataState.Loading -> currentState.copy(
                            screenState = ScreenState.IsLoading(),
                            data = currentData.copy(
                                analyzeState = currentData.analyzeState.copy(state = CleaningState.Analyzing)
                            )
                        )
                        is DataState.Success -> {
                            val fileTypesData = currentData.analyzeState.fileTypesData
                            val preferences = mapOf(
                                ExtensionsConstants.GENERIC_EXTENSIONS to true ,
                                ExtensionsConstants.IMAGE_EXTENSIONS to true ,
                                ExtensionsConstants.VIDEO_EXTENSIONS to true ,
                                ExtensionsConstants.AUDIO_EXTENSIONS to true ,
                                ExtensionsConstants.OFFICE_EXTENSIONS to true ,
                                ExtensionsConstants.ARCHIVE_EXTENSIONS to true ,
                                ExtensionsConstants.APK_EXTENSIONS to true ,
                                ExtensionsConstants.FONT_EXTENSIONS to true ,
                                ExtensionsConstants.WINDOWS_EXTENSIONS to true ,
                                ExtensionsConstants.EMPTY_FOLDERS to true ,
                                ExtensionsConstants.OTHER_EXTENSIONS to true
                            )

                            val groupedFiles : Map<String , List<File>> = withContext(dispatchers.default) {
                                computeGroupedFiles(scannedFiles = result.data.first , emptyFolders = result.data.second , fileTypesData = fileTypesData , preferences = preferences)
                            }
                            currentState.copy(
                                screenState = ScreenState.Success(),
                                data = currentData.copy(
                                    analyzeState = currentData.analyzeState.copy(
                                        scannedFileList = result.data.first,
                                        emptyFolderList = result.data.second,
                                        groupedFiles = groupedFiles,
                                        state = CleaningState.Idle
                                    )
                                )
                            )
                        }

                        is DataState.Error -> currentState.copy(
                            screenState = ScreenState.Error(),
                            data = currentData.copy(
                                analyzeState = currentData.analyzeState.copy(state = CleaningState.Error)
                            ),
                            errors = currentState.errors + UiSnackbar(
                                message = UiTextHelper.DynamicString("Failed to analyze files: ${result.error}"),
                                isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun computeGroupedFiles(
        scannedFiles : List<File> , emptyFolders : List<File> , fileTypesData : FileTypesData , preferences : Map<String , Boolean>
    ) : Map<String , List<File>> {
        val knownExtensions : Set<String> =
                (fileTypesData.imageExtensions + fileTypesData.videoExtensions + fileTypesData.audioExtensions + fileTypesData.officeExtensions + fileTypesData.archiveExtensions + fileTypesData.apkExtensions + fileTypesData.fontExtensions + fileTypesData.windowsExtensions).toSet()

        val filesMap : LinkedHashMap<String , MutableList<File>> = linkedMapOf()
        filesMap.putAll(fileTypesData.fileTypesTitles.associateWith { mutableListOf() })

        scannedFiles.forEach { file : File ->
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
            category?.let { filesMap[it]?.add(element = file) }
        }

        if (emptyFolders.isNotEmpty() && preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) {
            filesMap[fileTypesData.fileTypesTitles[8]] = emptyFolders.toMutableList()
        }

        return filesMap.filter { it.value.isNotEmpty() }
    }

    private fun deleteFiles(files : Set<File>) {
        launch(context = dispatchers.io) {
            if (files.isEmpty()) {
                sendAction(HomeAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("No files selected to delete.") , isError = false)))
                return@launch
            }

            deleteFilesUseCase(filesToDelete = files).collectLatest { result : DataState<Unit , Errors> ->
                _uiState.applyResult(result = result , errorMessage = UiTextHelper.DynamicString("Failed to delete files:")) { data , currentData ->
                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot { files.contains(it) } ,
                                                                                  groupedFiles = computeGroupedFiles(scannedFiles = currentData.analyzeState.scannedFileList.filterNot { files.contains(it) } ,
                                                                                                                     emptyFolders = currentData.analyzeState.emptyFolderList ,
                                                                                                                     fileTypesData = currentData.analyzeState.fileTypesData ,
                                                                                                                     preferences = mapOf()) ,
                                                                                  selectedFilesCount = 0 ,
                                                                                  areAllFilesSelected = false ,
                                                                                  fileSelectionMap = emptyMap() ,
                                                                                  isAnalyzeScreenVisible = false) , storageInfo = currentData.storageInfo.copy(isFreeSpaceLoading = true , isCleanedSpaceLoading = true))
                }

                if (result is DataState.Success) {
                    val clearedSpaceTotalSize : Long = files.sumOf { it.length() }
                    launch {
                        dataStore.addCleanedSpace(space = clearedSpaceTotalSize)
                        dataStore.saveLastScanTimestamp(timestamp = System.currentTimeMillis())

                    }
                    loadInitialData()
                }
            }
        }
    }

    private fun moveToTrash(files : List<File>) {
        launch(context = dispatchers.io) {
            if (files.isEmpty()) {
                sendAction(HomeAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("No files selected to move to trash.") , isError = false)))
                return@launch
            }

            val totalFileSizeToMove : Long = files.sumOf { it.length() }

            moveToTrashUseCase(filesToMove = files).collectLatest { result : DataState<Unit , Errors> ->
                _uiState.applyResult(result = result , errorMessage = UiTextHelper.DynamicString("Failed to move files to trash:")) { data : Unit , currentData : UiHomeModel ->
                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot { existingFile : File ->
                        files.any { movedFile : File -> existingFile.absolutePath == movedFile.absolutePath }
                    } , groupedFiles = computeGroupedFiles(scannedFiles = currentData.analyzeState.scannedFileList.filterNot { existingFile : File ->
                        files.any { movedFile : File -> existingFile.absolutePath == movedFile.absolutePath }
                    } , emptyFolders = currentData.analyzeState.emptyFolderList , fileTypesData = currentData.analyzeState.fileTypesData , preferences = mapOf()) , selectedFilesCount = 0 , areAllFilesSelected = false , isAnalyzeScreenVisible = false , fileSelectionMap = emptyMap(),
          state = CleaningState.Success))
                }

                if (result is DataState.Success) {
                    updateTrashSize(sizeChange = totalFileSizeToMove)
                    loadInitialData()
                }
            }
        }
    }

    fun onCloseAnalyzeComposable() {
        _uiState.update { state : UiStateScreen<UiHomeModel> ->
            val currentData : UiHomeModel = state.data ?: UiHomeModel()
            state.copy(
                data = currentData.copy(
                    analyzeState = currentData.analyzeState.copy(
                        isAnalyzeScreenVisible = false ,
                        scannedFileList = emptyList() ,
                        emptyFolderList = emptyList() ,
                        groupedFiles = emptyMap() ,
                        fileSelectionMap = emptyMap() ,
                        selectedFilesCount = 0 ,
                        areAllFilesSelected = false ,
                        state = CleaningState.Idle
                    )
                )
            )
        }
    }

    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        _uiState.update { state : UiStateScreen<UiHomeModel> ->
            val currentData : UiHomeModel = state.data ?: UiHomeModel()
            val updatedFileSelectionStates : Map<File , Boolean> = currentData.analyzeState.fileSelectionMap.toMutableMap().apply { this[file] = isChecked }
            val visibleFiles : List<File> = currentData.analyzeState.groupedFiles.values.flatten()
            val selectedVisibleCount : Int = updatedFileSelectionStates.filterKeys { it in visibleFiles }.count { it.value }
            state.copy(data = currentData.copy(analyzeState = currentData.analyzeState.copy(fileSelectionMap = updatedFileSelectionStates , selectedFilesCount = selectedVisibleCount , areAllFilesSelected = selectedVisibleCount == visibleFiles.size && visibleFiles.isNotEmpty())))
        }
    }

    fun toggleSelectAllFiles() {
        _uiState.update { state : UiStateScreen<UiHomeModel> ->
            val currentData : UiHomeModel = state.data ?: UiHomeModel()
            val newState : Boolean = currentData.analyzeState.areAllFilesSelected != true
            val visibleFiles : List<File> = currentData.analyzeState.groupedFiles.values.flatten()
            state.copy(data = currentData.copy(analyzeState = currentData.analyzeState.copy(areAllFilesSelected = newState , fileSelectionMap = if (newState) visibleFiles.associateWith { true } else emptyMap() , selectedFilesCount = if (newState) visibleFiles.size else 0)))
        }
    }

    fun toggleSelectFilesForCategory(category : String) {
        launch(context = dispatchers.default) {
            _uiState.update { currentState : UiStateScreen<UiHomeModel> ->
                val currentData : UiHomeModel = currentState.data ?: UiHomeModel()
                val filesInCategory : List<File> = currentData.analyzeState.groupedFiles[category] ?: emptyList()
                val currentSelectionMap : Map<File , Boolean> = currentData.analyzeState.fileSelectionMap
                val allSelected : Boolean = filesInCategory.all { currentSelectionMap[it] == true }
                val updatedSelectionMap : MutableMap<File , Boolean> = currentSelectionMap.toMutableMap().apply {
                    filesInCategory.forEach { file : File ->
                        this[file] = ! allSelected
                    }
                }

                val visibleFiles : List<File> = currentData.analyzeState.groupedFiles.values.flatten()
                val selectedVisibleCount : Int = updatedSelectionMap.filterKeys {
                    it in visibleFiles
                }.count { it.value }

                currentState.copy(data = currentData.copy(analyzeState = currentData.analyzeState.copy(fileSelectionMap = updatedSelectionMap , selectedFilesCount = selectedVisibleCount , areAllFilesSelected = selectedVisibleCount == visibleFiles.size && visibleFiles.isNotEmpty())))
            }
        }
    }

    fun cleanFiles() {
        if (_uiState.value.data?.analyzeState?.state != CleaningState.Idle) {
            return
        }

        launch(context = dispatchers.io) {

            _uiState.update { state : UiStateScreen<UiHomeModel> ->
                val currentData = state.data ?: UiHomeModel()
                state.copy(data = currentData.copy(analyzeState = currentData.analyzeState.copy(state = CleaningState.Cleaning)))
            }

            val currentScreenData : UiHomeModel = screenData ?: run {
                sendAction(HomeAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("Data not available.") , isError = true)))
                return@launch
            }

            val filesToDelete : Set<File> = currentScreenData.analyzeState.fileSelectionMap.filter { it.value }.keys
            if (filesToDelete.isEmpty()) {
                sendAction(HomeAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("No files selected to clean.") , isError = false)))
                return@launch
            }

            deleteFilesUseCase(filesToDelete = filesToDelete).collectLatest { result : DataState<Unit , Errors> ->
                _uiState.applyResult(result = result , errorMessage = UiTextHelper.DynamicString("Failed to delete files: ")) { _ : Unit , currentData : UiHomeModel ->
                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot { filesToDelete.contains(it) } ,
                                                                                  groupedFiles = computeGroupedFiles(scannedFiles = currentData.analyzeState.scannedFileList.filterNot { filesToDelete.contains(it) } ,
                                                                                                                     emptyFolders = currentData.analyzeState.emptyFolderList ,
                                                                                                                     fileTypesData = currentData.analyzeState.fileTypesData ,
                                                                                                                     preferences = mapOf()) ,
                                                                                  selectedFilesCount = 0 ,
                                                                                  areAllFilesSelected = false ,
                                                                                  fileSelectionMap = emptyMap() ,
  isAnalyzeScreenVisible = false ,
  state = CleaningState.Success) , storageInfo = currentData.storageInfo.copy(isFreeSpaceLoading = true , isCleanedSpaceLoading = true))
                }

                if (result is DataState.Success) {
                    println(message = "Debugging ---> Clean Files triggered, result is success")
                    val clearedSpaceTotalSize : Long = filesToDelete.sumOf { it.length() }
                    launch {
                        dataStore.addCleanedSpace(space = clearedSpaceTotalSize)
                        dataStore.saveLastScanTimestamp(timestamp = System.currentTimeMillis())
                    }
                    loadInitialData()
                } else if (result is DataState.Error) {
                    _uiState.update { s ->
                        val currentErrorData = s.data ?: UiHomeModel()
                        s.copy(data = currentErrorData.copy(analyzeState = currentErrorData.analyzeState.copy(state = CleaningState.Error)))
                    }
                }
            }
        }
    }

    fun moveSelectedToTrash() {
        if (_uiState.value.data?.analyzeState?.state != CleaningState.Idle) {
            return
        }

        launch(dispatchers.io) {

            _uiState.update { state ->
                val currentData = state.data ?: UiHomeModel()
                state.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(state = CleaningState.Cleaning)
                    )
                )
            }

            val currentScreenData = screenData ?: run {
                sendAction(HomeAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("Data not available.") , isError = true)))
                return@launch
            }

            val filesToMove : List<File> = currentScreenData.analyzeState.fileSelectionMap.filter { it.value }.keys.toList()
            if (filesToMove.isEmpty()) {
                sendAction(HomeAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("No files selected to move to trash.") , isError = false)))
                return@launch
            }

            val totalFileSizeToMove : Long = filesToMove.sumOf { it.length() }

            moveToTrashUseCase(filesToMove).collectLatest { result ->
                _uiState.applyResult(result = result , errorMessage = UiTextHelper.DynamicString("Failed to move files to trash:")) { _ , currentData ->

                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot { existingFile ->
                        filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                    } ,

                                                                                  groupedFiles = computeGroupedFiles(scannedFiles = currentData.analyzeState.scannedFileList.filterNot { existingFile ->
                                                                                      filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                                                                                  } , emptyFolders = currentData.analyzeState.emptyFolderList , fileTypesData = currentData.analyzeState.fileTypesData , preferences = mapOf()) ,
                                                                                  selectedFilesCount = 0 ,
                                                                                  areAllFilesSelected = false ,
                                                                                  isAnalyzeScreenVisible = false ,
                                                                                  fileSelectionMap = emptyMap(),
          state = CleaningState.Success))
                }

                if (result is DataState.Success) {
                    updateTrashSize(totalFileSizeToMove)
                    loadInitialData()
                } else if (result is DataState.Error) {
                    _uiState.update { s ->
                        val currentErrorData = s.data ?: UiHomeModel()
                        s.copy(data = currentErrorData.copy(analyzeState = currentErrorData.analyzeState.copy(state = CleaningState.Error)))
                    }
                }
            }
        }
    }

    private fun updateTrashSize(sizeChange : Long) {
        launch(dispatchers.io) {
            updateTrashSizeUseCase(sizeChange).collectLatest { result ->
                _uiState.applyResult(result = result , errorMessage = UiTextHelper.DynamicString("Failed to update trash size: ")) { data , currentData ->
                    currentData
                }
            }
        }
    }

    private fun loadCleanedSpace() {
        launch(dispatchers.io) {
            dataStore.cleanedSpace.collect { cleanedSpace ->
                _uiState.successData {

                    copy(storageInfo = storageInfo.copy(cleanedSpace = cleanedSpace.toString()))
                }
            }
        }
    }

    private fun toggleAnalyzeScreen(visible : Boolean) {
        _uiState.updateData(ScreenState.Success()) { currentData ->
            currentData.copy(
                analyzeState = currentData.analyzeState.copy(
                    isAnalyzeScreenVisible = visible,
                    state = if (visible) currentData.analyzeState.state else CleaningState.Idle
                )
            )
        }

        if (visible) {
            analyzeFiles()
        }
        else {
            _uiState.updateData(_uiState.value.screenState) { currentData ->
                currentData.copy(analyzeState = currentData.analyzeState.copy(fileSelectionMap = emptyMap() , selectedFilesCount = 0 , areAllFilesSelected = false))
            }
        }
    }

    fun setDeleteForeverConfirmationDialogVisibility(isVisible : Boolean) {
        _uiState.updateData(ScreenState.Success()) { currentData ->
            currentData.copy(analyzeState = currentData.analyzeState.copy(isDeleteForeverConfirmationDialogVisible = isVisible))
        }
    }

    fun setMoveToTrashConfirmationDialogVisibility(isVisible : Boolean) {
        _uiState.updateData(ScreenState.Success()) { currentData ->
            currentData.copy(analyzeState = currentData.analyzeState.copy(isMoveToTrashConfirmationDialogVisible = isVisible))
        }
    }
}