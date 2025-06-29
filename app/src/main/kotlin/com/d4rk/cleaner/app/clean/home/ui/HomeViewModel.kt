package com.d4rk.cleaner.app.clean.home.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.setLoading
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.home.domain.actions.HomeAction
import com.d4rk.cleaner.app.clean.home.domain.actions.HomeEvent
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.CleaningState
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.CleaningType
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.FileTypesData
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.UiHomeModel
import com.d4rk.cleaner.app.clean.home.domain.usecases.AnalyzeFilesUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.DeleteFilesUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.GetFileTypesUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.GetStorageInfoUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.MoveToTrashUseCase
import com.d4rk.cleaner.app.clean.home.domain.usecases.UpdateTrashSizeUseCase
import com.d4rk.cleaner.app.clean.home.utils.helpers.StorageUtils
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.settings.cleaning.utils.constants.ExtensionsConstants
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getStorageInfoUseCase: GetStorageInfoUseCase,
    private val getFileTypesUseCase: GetFileTypesUseCase,
    private val analyzeFilesUseCase: AnalyzeFilesUseCase,
    private val deleteFilesUseCase: DeleteFilesUseCase,
    private val moveToTrashUseCase: MoveToTrashUseCase,
    private val updateTrashSizeUseCase: UpdateTrashSizeUseCase,
    private val dispatchers: DispatcherProvider,
    private val dataStore: DataStore
) : ScreenViewModel<UiHomeModel, HomeEvent, HomeAction>(
    initialState = UiStateScreen(data = UiHomeModel())
) {

    init {
        onEvent(HomeEvent.LoadInitialData)
        loadCleanedSpace()
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadInitialData -> loadInitialData()
            is HomeEvent.AnalyzeFiles -> analyzeFiles()
            is HomeEvent.RefreshData -> refreshData()
            is HomeEvent.DeleteFiles -> deleteFiles(files = event.files)
            is HomeEvent.MoveToTrash -> moveToTrash(files = event.files)
            is HomeEvent.ToggleAnalyzeScreen -> toggleAnalyzeScreen(visible = event.visible)
            is HomeEvent.OnFileSelectionChange -> onFileSelectionChange(
                file = event.file,
                isChecked = event.isChecked
            )

            is HomeEvent.ToggleSelectAllFiles -> toggleSelectAllFiles()
            is HomeEvent.ToggleSelectFilesForCategory -> toggleSelectFilesForCategory(category = event.category)
            is HomeEvent.ToggleSelectFilesForDate -> toggleSelectFilesForDate(event.files, event.isChecked)
            is HomeEvent.CleanFiles -> cleanFiles()
            is HomeEvent.MoveSelectedToTrash -> moveSelectedToTrash()
            is HomeEvent.SetDeleteForeverConfirmationDialogVisibility -> setDeleteForeverConfirmationDialogVisibility(
                isVisible = event.isVisible
            )

            is HomeEvent.SetMoveToTrashConfirmationDialogVisibility -> setMoveToTrashConfirmationDialogVisibility(
                isVisible = event.isVisible
            )
            is HomeEvent.DismissSnackbar -> screenState.dismissSnackbar()
        }
    }

    private fun loadInitialData() {
        launch(context = dispatchers.io) {
            _uiState.setLoading()
            _uiState.updateData(newState = _uiState.value.screenState) { currentData: UiHomeModel ->
                currentData.copy(
                    storageInfo = currentData.storageInfo.copy(
                        isCleanedSpaceLoading = true,
                        isFreeSpaceLoading = true
                    )
                )
            }

            combine(
                flow = getStorageInfoUseCase(),
                flow2 = getFileTypesUseCase(),
                flow3 = dataStore.cleanedSpace.distinctUntilChanged()
            ) { storageState: DataState<UiHomeModel, Errors>, fileTypesState: DataState<FileTypesData, Errors>, cleanedSpaceValue: Long ->

                Triple(first = storageState, second = fileTypesState, third = cleanedSpaceValue)
            }.collect { (storageState: DataState<UiHomeModel, Errors>, fileTypesState: DataState<FileTypesData, Errors>, cleanedSpaceValue: Long) ->
                _uiState.update { currentState: UiStateScreen<UiHomeModel> ->
                    val currentData: UiHomeModel = currentState.data ?: UiHomeModel()
                    val updatedStorageInfo: StorageInfo = currentData.storageInfo.copy(
                        isFreeSpaceLoading = storageState is DataState.Loading,
                        isCleanedSpaceLoading = false,
                        cleanedSpace = StorageUtils.formatSizeReadable(cleanedSpaceValue)
                    )
                    val finalStorageInfo = if (storageState is DataState.Success) {
                        storageState.data.storageInfo.copy(
                            cleanedSpace = updatedStorageInfo.cleanedSpace,
                            isCleanedSpaceLoading = updatedStorageInfo.isCleanedSpaceLoading,
                            isFreeSpaceLoading = false
                        )
                    } else {
                        updatedStorageInfo
                    }

                    val updatedData: UiHomeModel = currentData.copy(
                        storageInfo = finalStorageInfo,
                        analyzeState = currentData.analyzeState.copy(fileTypesData = if (fileTypesState is DataState.Success) fileTypesState.data else currentData.analyzeState.fileTypesData)
                    )
                    val errors: List<UiSnackbar> = buildList {
                        if (storageState is DataState.Error) {
                            add(
                                element = UiSnackbar(
                                    message = UiTextHelper.StringResource(R.string.failed_to_load_storage_info),
                                    isError = true
                                )
                            )
                        }
                        if (fileTypesState is DataState.Error) {
                            if (storageState !is DataState.Error || storageState.error != fileTypesState.error) {
                                add(
                                    element = UiSnackbar(
                                        message = UiTextHelper.StringResource(R.string.failed_to_load_file_types),
                                        isError = true
                                    )
                                )
                            }
                        }
                    }.takeIf { it.isNotEmpty() } ?: emptyList()

                    currentState.copy(
                        screenState = when {
                            storageState is DataState.Loading || fileTypesState is DataState.Loading -> ScreenState.IsLoading()
                            storageState is DataState.Error || fileTypesState is DataState.Error -> ScreenState.Error()
                            storageState is DataState.Success && fileTypesState is DataState.Success -> ScreenState.Success()
                            else -> currentState.screenState
                        },
                        data = updatedData,
                        errors = currentState.errors + errors,
                        snackbar = currentState.snackbar
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
            analyzeFilesUseCase().collectLatest { result: DataState<Pair<List<File>, List<File>>, Errors> ->
                _uiState.update { currentState: UiStateScreen<UiHomeModel> ->
                    val currentData: UiHomeModel = currentState.data ?: UiHomeModel()
                    when (result) {
                        is DataState.Loading -> currentState.copy(
                            screenState = ScreenState.IsLoading(),
                            data = currentData.copy(
                                analyzeState = currentData.analyzeState.copy(
                                    state = CleaningState.Analyzing,
                                    cleaningType = CleaningType.NONE
                                )
                            )
                        )

                        is DataState.Success -> {
                            val fileTypesData = currentData.analyzeState.fileTypesData
                            val preferences = mapOf(
                                ExtensionsConstants.GENERIC_EXTENSIONS to dataStore.genericFilter.first(),
                                ExtensionsConstants.IMAGE_EXTENSIONS to dataStore.deleteImageFiles.first(),
                                ExtensionsConstants.VIDEO_EXTENSIONS to dataStore.deleteVideoFiles.first(),
                                ExtensionsConstants.AUDIO_EXTENSIONS to dataStore.deleteAudioFiles.first(),
                                ExtensionsConstants.OFFICE_EXTENSIONS to dataStore.deleteOfficeFiles.first(),
                                ExtensionsConstants.ARCHIVE_EXTENSIONS to dataStore.deleteArchives.first(),
                                ExtensionsConstants.APK_EXTENSIONS to dataStore.deleteApkFiles.first(),
                                ExtensionsConstants.FONT_EXTENSIONS to dataStore.deleteFontFiles.first(),
                                ExtensionsConstants.WINDOWS_EXTENSIONS to dataStore.deleteWindowsFiles.first(),
                                ExtensionsConstants.EMPTY_FOLDERS to dataStore.deleteEmptyFolders.first(),
                                ExtensionsConstants.OTHER_EXTENSIONS to dataStore.deleteOtherFiles.first()
                            )

                            val includeDuplicates = dataStore.deleteDuplicateFiles.first()
                            val (groupedFiles, duplicateOriginals) =
                                withContext(dispatchers.default) {
                                    computeGroupedFiles(
                                        scannedFiles = result.data.first,
                                        emptyFolders = result.data.second,
                                        fileTypesData = fileTypesData,
                                        preferences = preferences,
                                        includeDuplicates = includeDuplicates
                                    )
                                }
                            currentState.copy(
                                screenState = ScreenState.Success(),
                                data = currentData.copy(
                                    analyzeState = currentData.analyzeState.copy(
                                        scannedFileList = result.data.first,
                                        emptyFolderList = result.data.second,
                                        groupedFiles = groupedFiles,
                                        duplicateOriginals = duplicateOriginals,
                                        // Files are ready for the user to review
                                        // before starting the cleaning step
                                        state = CleaningState.ReadyToClean,
                                        cleaningType = CleaningType.NONE
                                    )
                                )
                            )
                        }

                        is DataState.Error -> currentState.copy(
                            screenState = ScreenState.Error(),
                            data = currentData.copy(
                                analyzeState = currentData.analyzeState.copy(
                                    state = CleaningState.Error,
                                    cleaningType = CleaningType.NONE
                                )
                            ),
                            errors = currentState.errors + UiSnackbar(
                                message = UiTextHelper.StringResource(R.string.failed_to_analyze_files),
                                isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun computeGroupedFiles(
        scannedFiles: List<File>,
        emptyFolders: List<File>,
        fileTypesData: FileTypesData,
        preferences: Map<String, Boolean>,
        includeDuplicates: Boolean
    ): Pair<Map<String, List<File>>, Set<File>> {
        val knownExtensions: Set<String> =
            (fileTypesData.imageExtensions + fileTypesData.videoExtensions + fileTypesData.audioExtensions + fileTypesData.officeExtensions + fileTypesData.archiveExtensions + fileTypesData.apkExtensions + fileTypesData.fontExtensions + fileTypesData.windowsExtensions).toSet()

        val baseDefaultTitles = listOf(
            "Images",
            "Videos",
            "Audios",
            "Documents",
            "Archives",
            "APKs",
            "Fonts",
            "Windows Files",
            "Empty Folders",
            "Other Files"
        )

        val baseFinalTitles = baseDefaultTitles.mapIndexed { index, fallback ->
            fileTypesData.fileTypesTitles.getOrElse(index) { fallback }
        }

        val duplicatesTitle = if (includeDuplicates) {
            fileTypesData.fileTypesTitles.getOrElse(10) { "Duplicates" }
        } else null

        val filesMap: LinkedHashMap<String, MutableList<File>> = linkedMapOf()
        filesMap.putAll(baseFinalTitles.associateWith { mutableListOf() })
        duplicatesTitle?.let { filesMap[it] = mutableListOf() }

        val duplicateGroups: List<List<File>> = if (includeDuplicates) findDuplicateGroups(scannedFiles) else emptyList()
        val duplicateFiles: Set<File> = if (includeDuplicates) duplicateGroups.flatten().toSet() else emptySet()
        val duplicateOriginals: Set<File> = if (includeDuplicates) duplicateGroups.mapNotNull { group ->
            group.minByOrNull { it.lastModified() }
        }.toSet() else emptySet()

        scannedFiles.forEach { file: File ->
            if (includeDuplicates && file in duplicateFiles) {
                duplicatesTitle?.let { title -> filesMap.getOrPut(title) { mutableListOf() }.add(file) }
            } else {
                val category: String? = when (val extension: String = file.extension.lowercase()) {
                    in fileTypesData.imageExtensions -> if (preferences[ExtensionsConstants.IMAGE_EXTENSIONS] == true) baseFinalTitles[0] else null
                    in fileTypesData.videoExtensions -> if (preferences[ExtensionsConstants.VIDEO_EXTENSIONS] == true) baseFinalTitles[1] else null
                    in fileTypesData.audioExtensions -> if (preferences[ExtensionsConstants.AUDIO_EXTENSIONS] == true) baseFinalTitles[2] else null
                    in fileTypesData.officeExtensions -> if (preferences[ExtensionsConstants.OFFICE_EXTENSIONS] == true) baseFinalTitles[3] else null
                    in fileTypesData.archiveExtensions -> if (preferences[ExtensionsConstants.ARCHIVE_EXTENSIONS] == true) baseFinalTitles[4] else null
                    in fileTypesData.apkExtensions -> if (preferences[ExtensionsConstants.APK_EXTENSIONS] == true) baseFinalTitles[5] else null
                    in fileTypesData.fontExtensions -> if (preferences[ExtensionsConstants.FONT_EXTENSIONS] == true) baseFinalTitles[6] else null
                    in fileTypesData.windowsExtensions -> if (preferences[ExtensionsConstants.WINDOWS_EXTENSIONS] == true) baseFinalTitles[7] else null
                    else -> if (!knownExtensions.contains(extension) && preferences[ExtensionsConstants.OTHER_EXTENSIONS] == true) baseFinalTitles[9] else null
                }
                category?.let { filesMap[it]?.add(element = file) }
            }
        }

        val emptyFoldersTitle = baseFinalTitles[8]
        if (preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) {
            filesMap[emptyFoldersTitle] = emptyFolders.toMutableList()
        }

        val filteredMap = filesMap.filter { (key, value) ->
            value.isNotEmpty() || (key == emptyFoldersTitle && preferences[ExtensionsConstants.EMPTY_FOLDERS] == true)
        }

        return filteredMap to duplicateOriginals
    }

    private fun findDuplicateGroups(files: List<File>): List<List<File>> {
        val hashMap = mutableMapOf<String, MutableList<File>>()
        files.filter { it.isFile }.forEach { file ->
            val hash = file.md5() ?: return@forEach
            hashMap.getOrPut(hash) { mutableListOf() }.add(file)
        }
        return hashMap.values.filter { it.size > 1 }
    }

    private fun File.md5(): String? = runCatching {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val md = MessageDigest.getInstance("MD5")
        inputStream().use { stream ->
            var read = stream.read(buffer)
            while (read > 0) {
                md.update(buffer, 0, read)
                read = stream.read(buffer)
            }
        }
        md.digest().joinToString("") { "%02x".format(it) }
    }.getOrNull()

    private fun deleteFiles(files: Set<File>) {
        launch(context = dispatchers.io) {
            if (files.isEmpty()) {
                postSnackbar(
                    message = UiTextHelper.StringResource(R.string.no_files_selected_to_delete),
                    isError = false
                )
                return@launch
            }
            _uiState.update { state: UiStateScreen<UiHomeModel> ->
                val currentData: UiHomeModel = state.data ?: UiHomeModel()
                state.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            state = CleaningState.Cleaning,
                            cleaningType = CleaningType.DELETE
                        )
                    )
                )
            }


            deleteFilesUseCase(filesToDelete = files).collectLatest { result: DataState<Unit, Errors> ->
                val includeDuplicates = dataStore.deleteDuplicateFiles.first()
                _uiState.applyResult(
                    result = result,
                    errorMessage = UiTextHelper.StringResource(R.string.failed_to_delete_files)
                ) { data, currentData ->
                    val (groupedFilesUpdated, duplicateOriginals) = computeGroupedFiles(
                        scannedFiles = currentData.analyzeState.scannedFileList.filterNot { files.contains(it) },
                        emptyFolders = currentData.analyzeState.emptyFolderList,
                        fileTypesData = currentData.analyzeState.fileTypesData,
                        preferences = mapOf(),
                        includeDuplicates = includeDuplicates
                    )

                    currentData.copy(analyzeState = currentData.analyzeState.copy(
                        scannedFileList = currentData.analyzeState.scannedFileList.filterNot {
                            files.contains(it)
                        },
                        groupedFiles = groupedFilesUpdated,
                        duplicateOriginals = duplicateOriginals,
                        selectedFilesCount = 0,
                        areAllFilesSelected = false,
                        fileSelectionMap = emptyMap(),
                        isAnalyzeScreenVisible = false),
                        storageInfo = currentData.storageInfo.copy(
                            isFreeSpaceLoading = true,
                            isCleanedSpaceLoading = true
                        ))
                }

                if (result is DataState.Success) {
                    launch {
                        dataStore.saveLastScanTimestamp(timestamp = System.currentTimeMillis())
                    }
                    loadInitialData()
                }
            }
        }
    }

    private fun moveToTrash(files: List<File>) {
        launch(context = dispatchers.io) {
            if (files.isEmpty()) {
                postSnackbar(
                    message = UiTextHelper.StringResource(R.string.no_files_selected_move_to_trash),
                    isError = false
                )
                return@launch
            }
            _uiState.update { state: UiStateScreen<UiHomeModel> ->
                val currentData: UiHomeModel = state.data ?: UiHomeModel()
                state.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            state = CleaningState.Cleaning,
                            cleaningType = CleaningType.MOVE_TO_TRASH
                        )
                    )
                )
            }


            val totalFileSizeToMove: Long = files.sumOf { it.length() }

            val includeDuplicates = dataStore.deleteDuplicateFiles.first()
            moveToTrashUseCase(filesToMove = files).collectLatest { result: DataState<Unit, Errors> ->
                _uiState.applyResult(
                    result = result,
                    errorMessage = UiTextHelper.StringResource(R.string.failed_to_move_files_to_trash)
                ) { _: Unit, currentData: UiHomeModel ->
                    val (groupedFilesUpdated2, duplicateOriginals2) = computeGroupedFiles(
                        scannedFiles = currentData.analyzeState.scannedFileList.filterNot { existingFile: File ->
                            files.any { movedFile: File -> existingFile.absolutePath == movedFile.absolutePath }
                        },
                        emptyFolders = currentData.analyzeState.emptyFolderList,
                        fileTypesData = currentData.analyzeState.fileTypesData,
                        preferences = mapOf(),
                        includeDuplicates = includeDuplicates
                    )

                    currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            scannedFileList = currentData.analyzeState.scannedFileList.filterNot { existingFile: File ->
                                files.any { movedFile: File -> existingFile.absolutePath == movedFile.absolutePath }
                            },
                            groupedFiles = groupedFilesUpdated2,
                            duplicateOriginals = duplicateOriginals2,
                            selectedFilesCount = 0,
                            areAllFilesSelected = false,
                            isAnalyzeScreenVisible = false,
                            fileSelectionMap = emptyMap(),
                            state = CleaningState.Result
                        )
                    )

                }

                if (result is DataState.Success) {
                    updateTrashSize(sizeChange = totalFileSizeToMove)
                    loadInitialData()
                }
            }
        }
    }

    fun onCloseAnalyzeComposable() {
        _uiState.update { state: UiStateScreen<UiHomeModel> ->
            val currentData: UiHomeModel = state.data ?: UiHomeModel()
            state.copy(
                data = currentData.copy(
                    analyzeState = currentData.analyzeState.copy(
                        isAnalyzeScreenVisible = false,
                        scannedFileList = emptyList(),
                        emptyFolderList = emptyList(),
                        groupedFiles = emptyMap(),
                        fileSelectionMap = emptyMap(),
                        selectedFilesCount = 0,
                        areAllFilesSelected = false,
                        state = CleaningState.Idle,
                        cleaningType = CleaningType.NONE
                    )
                )
            )
        }
    }

    fun onFileSelectionChange(file: File, isChecked: Boolean) {
        _uiState.update { state: UiStateScreen<UiHomeModel> ->
            val currentData: UiHomeModel = state.data ?: UiHomeModel()
            val updatedFileSelectionStates: Map<File, Boolean> =
                currentData.analyzeState.fileSelectionMap.toMutableMap()
                    .apply { this[file] = isChecked }
            val visibleFiles: List<File> = currentData.analyzeState.groupedFiles.values.flatten()
            val selectedVisibleCount: Int =
                updatedFileSelectionStates.filterKeys { it in visibleFiles }.count { it.value }
            state.copy(
                data = currentData.copy(
                    analyzeState = currentData.analyzeState.copy(
                        fileSelectionMap = updatedFileSelectionStates,
                        selectedFilesCount = selectedVisibleCount,
                        areAllFilesSelected = selectedVisibleCount == visibleFiles.size && visibleFiles.isNotEmpty()
                    )
                )
            )
        }
    }

    fun toggleSelectAllFiles() {
        _uiState.update { state: UiStateScreen<UiHomeModel> ->
            val currentData: UiHomeModel = state.data ?: UiHomeModel()
            val newState: Boolean = !currentData.analyzeState.areAllFilesSelected
            val visibleFiles: List<File> = currentData.analyzeState.groupedFiles.values.flatten()
            val duplicateOriginals = currentData.analyzeState.duplicateOriginals
            state.copy(
                data = currentData.copy(
                    analyzeState = currentData.analyzeState.copy(
                        areAllFilesSelected = newState,
                        fileSelectionMap = if (newState) visibleFiles.associateWith { file ->
                            if (file in duplicateOriginals) currentData.analyzeState.fileSelectionMap[file] == true else true
                        } else emptyMap(),
                        selectedFilesCount = if (newState) visibleFiles.count { file ->
                            file !in duplicateOriginals || currentData.analyzeState.fileSelectionMap[file] == true
                        } else 0)))
        }
    }

    fun toggleSelectFilesForCategory(category: String) {
        launch(context = dispatchers.default) {
            _uiState.update { currentState: UiStateScreen<UiHomeModel> ->
                val currentData: UiHomeModel = currentState.data ?: UiHomeModel()
                val filesInCategory: List<File> =
                    currentData.analyzeState.groupedFiles[category] ?: emptyList()
                val duplicateOriginals = currentData.analyzeState.duplicateOriginals
                val currentSelectionMap: Map<File, Boolean> =
                    currentData.analyzeState.fileSelectionMap
                val allSelected: Boolean = filesInCategory.filterNot { it in duplicateOriginals }.all { currentSelectionMap[it] == true }
                val updatedSelectionMap: MutableMap<File, Boolean> =
                    currentSelectionMap.toMutableMap().apply {
                        filesInCategory.forEach { file: File ->
                            if (file in duplicateOriginals) {
                                if (allSelected) this[file] = false else this[file] = currentSelectionMap[file] ?: false
                            } else {
                                this[file] = !allSelected
                            }
                        }
                    }

                val visibleFiles: List<File> =
                    currentData.analyzeState.groupedFiles.values.flatten()
                val selectedVisibleCount: Int = updatedSelectionMap.filterKeys {
                    it in visibleFiles
                }.count { it.value }

                currentState.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            fileSelectionMap = updatedSelectionMap,
                            selectedFilesCount = selectedVisibleCount,
                            areAllFilesSelected = selectedVisibleCount == visibleFiles.size && visibleFiles.isNotEmpty()
                        )
                    )
                )
            }
        }
    }

    fun toggleSelectFilesForDate(files: List<File>, isChecked: Boolean) {
        launch(context = dispatchers.default) {
            _uiState.update { currentState: UiStateScreen<UiHomeModel> ->
                val currentData = currentState.data ?: UiHomeModel()
                val duplicateOriginals = currentData.analyzeState.duplicateOriginals
                val updatedSelectionMap = currentData.analyzeState.fileSelectionMap.toMutableMap().apply {
                    files.forEach { file ->
                        if (isChecked && file in duplicateOriginals && (this[file] != true)) {
                            // keep unchecked
                        } else {
                            this[file] = isChecked
                        }
                    }
                }

                val visibleFiles = currentData.analyzeState.groupedFiles.values.flatten()
                val selectedVisibleCount = updatedSelectionMap.filterKeys { it in visibleFiles }.count { it.value }

                currentState.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            fileSelectionMap = updatedSelectionMap,
                            selectedFilesCount = selectedVisibleCount,
                            areAllFilesSelected = selectedVisibleCount == visibleFiles.size && visibleFiles.isNotEmpty()
                        )
                    )
                )
            }
        }
    }

    fun cleanFiles() {
        if (_uiState.value.data?.analyzeState?.state != CleaningState.ReadyToClean) {
            return
        }

        launch(context = dispatchers.io) {

            _uiState.update { state: UiStateScreen<UiHomeModel> ->
                val currentData = state.data ?: UiHomeModel()
                state.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            state = CleaningState.Cleaning,
                            cleaningType = CleaningType.DELETE
                        )
                    )
                )
            }

            val currentScreenData: UiHomeModel = screenData ?: run {
                postSnackbar(
                    message = UiTextHelper.StringResource(R.string.data_not_available),
                    isError = true
                )
                return@launch
            }

            val filesToDelete: Set<File> =
                currentScreenData.analyzeState.fileSelectionMap.filter { it.value }.keys
            if (filesToDelete.isEmpty()) {
                postSnackbar(
                    message = UiTextHelper.StringResource(R.string.no_files_selected_to_clean),
                    isError = false
                )
                return@launch
            }

            val includeDuplicates = dataStore.deleteDuplicateFiles.first()
            deleteFilesUseCase(filesToDelete = filesToDelete).collectLatest { result: DataState<Unit, Errors> ->
                _uiState.applyResult(
                    result = result,
                    errorMessage = UiTextHelper.StringResource(R.string.failed_to_delete_files)
                ) { _: Unit, currentData: UiHomeModel ->
                    val (groupedFilesUpdated, duplicateOriginals) = computeGroupedFiles(
                        scannedFiles = currentData.analyzeState.scannedFileList.filterNot {
                            filesToDelete.contains(it)
                        },
                        emptyFolders = currentData.analyzeState.emptyFolderList,
                        fileTypesData = currentData.analyzeState.fileTypesData,
                        preferences = mapOf(),
                        includeDuplicates = includeDuplicates
                    )

                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot {
                        filesToDelete.contains(it)
                    },
                        groupedFiles = groupedFilesUpdated,
                        duplicateOriginals = duplicateOriginals,
                        selectedFilesCount = 0,
                        areAllFilesSelected = false,
                        fileSelectionMap = emptyMap(),
                        isAnalyzeScreenVisible = false,
                        state = CleaningState.Result),
                        storageInfo = currentData.storageInfo.copy(
                            isFreeSpaceLoading = true,
                            isCleanedSpaceLoading = true
                        ))
                }

                if (result is DataState.Success) {
                    launch {
                        dataStore.saveLastScanTimestamp(timestamp = System.currentTimeMillis())
                    }
                    loadInitialData()
                } else if (result is DataState.Error) {
                    _uiState.update { s ->
                        val currentErrorData = s.data ?: UiHomeModel()
                        s.copy(
                            data = currentErrorData.copy(
                                analyzeState = currentErrorData.analyzeState.copy(
                                    state = CleaningState.Error
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun moveSelectedToTrash() {
        if (_uiState.value.data?.analyzeState?.state != CleaningState.ReadyToClean) {
            return
        }

        launch(dispatchers.io) {

            _uiState.update { state ->
                val currentData = state.data ?: UiHomeModel()
                state.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            state = CleaningState.Cleaning,
                            cleaningType = CleaningType.MOVE_TO_TRASH
                        )
                    )
                )
            }

            val currentScreenData = screenData ?: run {
                postSnackbar(
                    message = UiTextHelper.StringResource(R.string.data_not_available),
                    isError = true
                )
                return@launch
            }

            val filesToMove: List<File> =
                currentScreenData.analyzeState.fileSelectionMap.filter { it.value }.keys.toList()
            if (filesToMove.isEmpty()) {
                postSnackbar(
                    message = UiTextHelper.StringResource(R.string.no_files_selected_move_to_trash),
                    isError = false
                )
                return@launch
            }

            val totalFileSizeToMove: Long = filesToMove.sumOf { it.length() }

            val includeDuplicates = dataStore.deleteDuplicateFiles.first()
            moveToTrashUseCase(filesToMove).collectLatest { result ->
                _uiState.applyResult(
                    result = result,
                    errorMessage = UiTextHelper.StringResource(R.string.failed_to_move_files_to_trash)
                ) { _, currentData ->
                    val (groupedFilesUpdated3, duplicateOriginals3) = computeGroupedFiles(
                        scannedFiles = currentData.analyzeState.scannedFileList.filterNot { existingFile ->
                            filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                        },
                        emptyFolders = currentData.analyzeState.emptyFolderList,
                        fileTypesData = currentData.analyzeState.fileTypesData,
                        preferences = mapOf(),
                        includeDuplicates = includeDuplicates
                    )

                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot { existingFile ->
                        filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                    },

                        groupedFiles = groupedFilesUpdated3,
                        duplicateOriginals = duplicateOriginals3,
                        selectedFilesCount = 0,
                        areAllFilesSelected = false,
                        isAnalyzeScreenVisible = false,
                        fileSelectionMap = emptyMap(),
                        state = CleaningState.Result))
                }

                if (result is DataState.Success) {
                    updateTrashSize(totalFileSizeToMove)
                    loadInitialData()
                } else if (result is DataState.Error) {
                    _uiState.update { s ->
                        val currentErrorData = s.data ?: UiHomeModel()
                        s.copy(
                            data = currentErrorData.copy(
                                analyzeState = currentErrorData.analyzeState.copy(
                                    state = CleaningState.Error
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun updateTrashSize(sizeChange: Long) {
        launch(dispatchers.io) {
            updateTrashSizeUseCase(sizeChange).collectLatest { result ->
                _uiState.applyResult(
                    result = result,
                    errorMessage = UiTextHelper.StringResource(R.string.failed_to_update_trash_size)
                ) { data, currentData ->
                    currentData
                }
            }
        }
    }

    private fun loadCleanedSpace() {
        launch(dispatchers.io) {
            dataStore.cleanedSpace.collect { cleanedSpace ->
                _uiState.successData {
                    copy(
                        storageInfo = storageInfo.copy(
                            cleanedSpace = StorageUtils.formatSizeReadable(cleanedSpace)
                        )
                    )
                }
            }
        }
    }

    private fun toggleAnalyzeScreen(visible: Boolean) {
        if (visible) {
            launch(dispatchers.io) {
                val anyEnabled = listOf(
                    dataStore.genericFilter.first(),
                    dataStore.deleteEmptyFolders.first(),
                    dataStore.deleteArchives.first(),
                    dataStore.deleteInvalidMedia.first(),
                    dataStore.deleteCorpseFiles.first(),
                    dataStore.deleteApkFiles.first(),
                    dataStore.deleteAudioFiles.first(),
                    dataStore.deleteVideoFiles.first(),
                    dataStore.deleteWindowsFiles.first(),
                    dataStore.deleteOfficeFiles.first(),
                    dataStore.deleteFontFiles.first(),
                    dataStore.deleteOtherFiles.first(),
                    dataStore.deleteImageFiles.first(),
                    dataStore.clipboardClean.first(),
                    dataStore.deleteDuplicateFiles.first()
                ).any { it }

                if (!anyEnabled) {
                    postSnackbar(
                        message = UiTextHelper.StringResource(R.string.no_cleaning_options_selected),
                        isError = false
                    )
                    return@launch
                }

                _uiState.update { state: UiStateScreen<UiHomeModel> ->
                    val currentData: UiHomeModel = state.data ?: UiHomeModel()
                    state.copy(
                        data = currentData.copy(
                            analyzeState = currentData.analyzeState.copy(
                                isAnalyzeScreenVisible = true,
                                scannedFileList = emptyList(),
                                emptyFolderList = emptyList(),
                                groupedFiles = emptyMap(),
                                fileSelectionMap = emptyMap(),
                                selectedFilesCount = 0,
                                areAllFilesSelected = false,
                                state = CleaningState.Idle,
                                cleaningType = CleaningType.NONE
                            )
                        )
                    )
                }
                analyzeFiles()
            }
        } else {
            _uiState.updateData(ScreenState.Success()) { currentData ->
                currentData.copy(
                    analyzeState = currentData.analyzeState.copy(
                        fileSelectionMap = emptyMap(),
                        selectedFilesCount = 0,
                        areAllFilesSelected = false,
                        isAnalyzeScreenVisible = false,
                        state = CleaningState.Idle,
                        cleaningType = CleaningType.NONE
                    )
                )
            }
        }
    }

    fun setDeleteForeverConfirmationDialogVisibility(isVisible: Boolean) {
        _uiState.updateData(ScreenState.Success()) { currentData ->
            currentData.copy(
                analyzeState = currentData.analyzeState.copy(
                    isDeleteForeverConfirmationDialogVisible = isVisible
                )
            )
        }
    }

    fun setMoveToTrashConfirmationDialogVisibility(isVisible: Boolean) {
        _uiState.updateData(ScreenState.Success()) { currentData ->
            currentData.copy(
                analyzeState = currentData.analyzeState.copy(
                    isMoveToTrashConfirmationDialogVisible = isVisible
                )
            )
        }
    }

    private fun postSnackbar(message : UiTextHelper , isError : Boolean) {
        screenState.showSnackbar(snackbar = UiSnackbar(message = message , isError = isError , timeStamp = System.currentTimeMillis() , type = ScreenMessageType.SNACKBAR))
    }
}