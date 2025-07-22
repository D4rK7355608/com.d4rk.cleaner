package com.d4rk.cleaner.app.clean.scanner.ui

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.net.toUri
import androidx.paging.PagingData
import androidx.paging.map
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.setLoading
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.clean.scanner.domain.actions.ScannerAction
import com.d4rk.cleaner.app.clean.scanner.domain.actions.ScannerEvent
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.CleaningState
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.CleaningType
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.FileTypesData
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.AnalyzeFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.DeleteFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetFileTypesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetLargestFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetPromotedAppUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetStorageInfoUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.MoveToTrashUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.UpdateTrashSizeUseCase
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter
import com.d4rk.cleaner.app.clean.scanner.utils.helpers.getWhatsAppMediaSummary
import com.d4rk.cleaner.app.settings.cleaning.utils.constants.ExtensionsConstants
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.clearClipboardCompat
import com.d4rk.cleaner.core.utils.helpers.CleaningEventBus
import com.d4rk.cleaner.app.clean.scanner.utils.helpers.CleaningProgressBus
import com.d4rk.cleaner.core.utils.extensions.md5
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.d4rk.cleaner.app.clean.scanner.services.CleaningService
import kotlinx.coroutines.runBlocking

private const val RESULT_DELAY_MS = 3600L

@OptIn(ExperimentalCoroutinesApi::class)
class ScannerViewModel(
    private val application: Application,
    private val getStorageInfoUseCase: GetStorageInfoUseCase,
    private val getFileTypesUseCase: GetFileTypesUseCase,
    private val analyzeFilesUseCase: AnalyzeFilesUseCase,
    private val deleteFilesUseCase: DeleteFilesUseCase,
    private val moveToTrashUseCase: MoveToTrashUseCase,
    private val updateTrashSizeUseCase: UpdateTrashSizeUseCase,
    private val getPromotedAppUseCase: GetPromotedAppUseCase,
    private val getLargestFilesUseCase: GetLargestFilesUseCase,
    private val dispatchers: DispatcherProvider,
    private val dataStore: DataStore
) : ScreenViewModel<UiScannerModel, ScannerEvent, ScannerAction>(
    initialState = UiStateScreen(data = UiScannerModel())
) {

    private val clipboardManager =
        application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val clipboardListener =
        ClipboardManager.OnPrimaryClipChangedListener { loadClipboardData() }

    private val _whatsAppMediaSummary = MutableStateFlow(WhatsAppMediaSummary())
    val whatsAppMediaSummary: StateFlow<WhatsAppMediaSummary> = _whatsAppMediaSummary
    private val _whatsAppMediaLoaded = MutableStateFlow(false)
    val whatsAppMediaLoaded: StateFlow<Boolean> = _whatsAppMediaLoaded
    private val _isWhatsAppInstalled = MutableStateFlow(false)
    val isWhatsAppInstalled: StateFlow<Boolean> = _isWhatsAppInstalled

    private val _clipboardPreview = MutableStateFlow<String?>(null)
    val clipboardPreview: StateFlow<String?> = _clipboardPreview

    private val _clipboardDetectedSensitive = MutableStateFlow(false)
    val clipboardDetectedSensitive: StateFlow<Boolean> = _clipboardDetectedSensitive

    private val _cleanStreak = MutableStateFlow(0)
    val cleanStreak: StateFlow<Int> = _cleanStreak

    private val _showStreakCard = MutableStateFlow(true)
    val showStreakCard: StateFlow<Boolean> = _showStreakCard
    private val _showStreakCardPref = MutableStateFlow(true)
    val showStreakCardPref: StateFlow<Boolean> = _showStreakCardPref
    private val _streakHideUntil = MutableStateFlow(0L)
    val streakHideUntil: StateFlow<Long> = _streakHideUntil

    private val _largestFiles = MutableStateFlow<List<File>>(emptyList())
    val largestFiles: StateFlow<List<File>> = _largestFiles

    init {
        clipboardManager.addPrimaryClipChangedListener(clipboardListener)
        onEvent(ScannerEvent.LoadInitialData)
        loadCleanedSpace()
        checkWhatsAppInstalled()
        loadWhatsAppMedia()
        loadClipboardData()
        loadPromotedApp()
        loadLargestFilesPreview()
        loadCleanStreak()
        loadStreakCardVisibility()
        launch(dispatchers.io) {
            CleaningEventBus.events.collectLatest {
                onEvent(ScannerEvent.RefreshData)
            }
        }
        launch(dispatchers.io) {
            CleaningProgressBus.progress.collectLatest { progress ->
                _uiState.update { state ->
                    val data = state.data ?: UiScannerModel()
                    state.copy(data = data.copy(analyzeState = data.analyzeState.copy(cleaningProgress = progress)))
                }
                if (progress == 100) {
                    refreshData()
                }
            }
        }
    }

    override fun onEvent(event: ScannerEvent) {
        when (event) {
            is ScannerEvent.LoadInitialData -> loadInitialData()
            is ScannerEvent.AnalyzeFiles -> analyzeFiles()
            is ScannerEvent.RefreshData -> refreshData()
            is ScannerEvent.DeleteFiles -> deleteFiles(files = event.files)
            is ScannerEvent.MoveToTrash -> moveToTrash(files = event.files)
            is ScannerEvent.ToggleAnalyzeScreen -> toggleAnalyzeScreen(visible = event.visible)
            is ScannerEvent.OnFileSelectionChange -> onFileSelectionChange(
                file = event.file,
                isChecked = event.isChecked
            )

            is ScannerEvent.ToggleSelectAllFiles -> toggleSelectAllFiles()
            is ScannerEvent.ToggleSelectFilesForCategory -> toggleSelectFilesForCategory(category = event.category)
            is ScannerEvent.ToggleSelectFilesForDate -> toggleSelectFilesForDate(event.files , event.isChecked)
            is ScannerEvent.CleanFiles -> cleanFiles()
            is ScannerEvent.CleanWhatsAppFiles -> onCleanWhatsAppFiles()
            is ScannerEvent.CleanCache -> onCleanCache()
            is ScannerEvent.MoveSelectedToTrash -> moveSelectedToTrash()
            is ScannerEvent.SetDeleteForeverConfirmationDialogVisibility -> setDeleteForeverConfirmationDialogVisibility(
                isVisible = event.isVisible
            )

            is ScannerEvent.SetMoveToTrashConfirmationDialogVisibility -> setMoveToTrashConfirmationDialogVisibility(
                isVisible = event.isVisible
            )
            is ScannerEvent.SetHideStreakDialogVisibility -> setHideStreakDialogVisibility(event.isVisible)
            ScannerEvent.HideStreakForNow -> hideStreakForNow()
            ScannerEvent.HideStreakPermanently -> hideStreakPermanently()
            is ScannerEvent.DismissSnackbar -> screenState.dismissSnackbar()
        }
    }

    private fun loadInitialData() {
        launch(context = dispatchers.io) {
            _uiState.setLoading()
            _uiState.updateData(newState = _uiState.value.screenState) { currentData: UiScannerModel ->
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
            ) { storageState: DataState<UiScannerModel, Errors> , fileTypesState: DataState<FileTypesData, Errors> , cleanedSpaceValue: Long ->

                Triple(first = storageState, second = fileTypesState, third = cleanedSpaceValue)
            }.collect { (storageState: DataState<UiScannerModel, Errors>, fileTypesState: DataState<FileTypesData, Errors>, cleanedSpaceValue: Long) ->
                _uiState.update { currentState: UiStateScreen<UiScannerModel> ->
                    val currentData: UiScannerModel = currentState.data ?: UiScannerModel()
                    val updatedStorageInfo: StorageInfo = currentData.storageInfo.copy(
                        isFreeSpaceLoading = storageState is DataState.Loading,
                        isCleanedSpaceLoading = false,
                        cleanedSpace = FileSizeFormatter.format(cleanedSpaceValue)
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

                    val updatedData: UiScannerModel = currentData.copy(
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
        loadWhatsAppMedia()
        loadClipboardData()
        loadLargestFilesPreview()
        if (screenData?.analyzeState?.isAnalyzeScreenVisible == true) {
            analyzeFiles()
        }
    }

    private fun analyzeFiles() {
        if (_uiState.value.data?.analyzeState?.state != CleaningState.Idle) {
            return
        }

        launch(dispatchers.io) {
            analyzeFilesUseCase().collectLatest { result: DataState<Flow<PagingData<File>>, Errors> ->
                _uiState.update { currentState: UiStateScreen<UiScannerModel> ->
                    val currentData: UiScannerModel = currentState.data ?: UiScannerModel()
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
                            val allFiles = withContext(dispatchers.io) { result.data.toList() }
                            val (groupedFiles, duplicateOriginals, duplicateGroups) =
                                withContext(dispatchers.io) {
                                    computeGroupedFiles(
                                        scannedFiles = allFiles,
                                        emptyFolders = emptyList(),
                                        fileTypesData = fileTypesData,
                                        preferences = preferences,
                                        includeDuplicates = includeDuplicates
                                    )
                                }
                            currentState.copy(
                                screenState = ScreenState.Success(),
                                data = currentData.copy(
                                    analyzeState = currentData.analyzeState.copy(
                                        scannedFileList = allFiles,
                                        emptyFolderList = emptyList(),
                                        groupedFiles = groupedFiles,
                                        duplicateOriginals = duplicateOriginals,
                                        duplicateGroups = duplicateGroups,
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

    private suspend fun computeGroupedFiles(
        scannedFiles: List<File>,
        emptyFolders: List<File>,
        fileTypesData: FileTypesData,
        preferences: Map<String, Boolean>,
        includeDuplicates: Boolean
    ): Triple<Map<String, List<File>>, Set<File>, List<List<File>>> = withContext(dispatchers.default) {
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

        Triple(filteredMap, duplicateOriginals, duplicateGroups)
    }

    private suspend fun findDuplicateGroups(files: List<File>): List<List<File>> =
        withContext(dispatchers.default) {
            val hashMap = mutableMapOf<String, MutableList<File>>()
            files.filter { it.isFile }.forEach { file ->
                val hash = file.md5() ?: return@forEach
                hashMap.getOrPut(hash) { mutableListOf() }.add(file)
            }
            hashMap.values.filter { it.size > 1 }
        }


    private fun deleteFiles(files: Set<File>) {
        launch(context = dispatchers.io) {
            if (files.isEmpty()) {
                postSnackbar(
                    message = UiTextHelper.StringResource(R.string.no_files_selected_to_delete),
                    isError = false
                )
                return@launch
            }
            _uiState.update { state: UiStateScreen<UiScannerModel> ->
                val currentData: UiScannerModel = state.data ?: UiScannerModel()
                state.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            state = CleaningState.Cleaning,
                            cleaningType = CleaningType.DELETE
                        )
                    )
                )
            }

            val intent = Intent(application, CleaningService::class.java).apply {
                putExtra(CleaningService.EXTRA_PATHS, files.map { it.absolutePath }.toTypedArray())
            }
            startForegroundService(application, intent)
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
            _uiState.update { state: UiStateScreen<UiScannerModel> ->
                val currentData: UiScannerModel = state.data ?: UiScannerModel()
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
                ) { _: Unit, currentData: UiScannerModel ->
                    val (groupedFilesUpdated2, duplicateOriginals2, duplicateGroups2) =
                        runBlocking(dispatchers.io) {
                            computeGroupedFiles(
                                scannedFiles = currentData.analyzeState.scannedFileList.filterNot { existingFile: File ->
                                    files.any { movedFile: File -> existingFile.absolutePath == movedFile.absolutePath }
                                },
                                emptyFolders = currentData.analyzeState.emptyFolderList,
                                fileTypesData = currentData.analyzeState.fileTypesData,
                                preferences = mapOf(),
                                includeDuplicates = includeDuplicates
                            )
                        }

                    currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            scannedFileList = currentData.analyzeState.scannedFileList.filterNot { existingFile: File ->
                                files.any { movedFile: File -> existingFile.absolutePath == movedFile.absolutePath }
                            },
                            groupedFiles = groupedFilesUpdated2,
                            duplicateOriginals = duplicateOriginals2,
                            duplicateGroups = duplicateGroups2,
                            selectedFilesCount = 0,
                            areAllFilesSelected = false,
                            isAnalyzeScreenVisible = false,
                            fileSelectionMap = emptyMap()
                        )
                    )

                }

                if (result is DataState.Success) {
                    delay(RESULT_DELAY_MS)
                    _uiState.update { state ->
                        val current = state.data ?: UiScannerModel()
                        state.copy(data = current.copy(analyzeState = current.analyzeState.copy(state = CleaningState.Result)))
                    }
                    updateTrashSize(sizeChange = totalFileSizeToMove)
                    loadInitialData()
                    loadWhatsAppMedia()
                    loadClipboardData()
                    CleaningEventBus.notifyCleaned()
                }
            }
        }
    }

    fun onCloseAnalyzeComposable() {
        _uiState.update { state: UiStateScreen<UiScannerModel> ->
            val currentData: UiScannerModel = state.data ?: UiScannerModel()
            state.copy(
                data = currentData.copy(
                    analyzeState = currentData.analyzeState.copy(
                        isAnalyzeScreenVisible = false,
                        scannedFileList = emptyList(),
                        emptyFolderList = emptyList(),
                        groupedFiles = emptyMap(),
                        duplicateGroups = emptyList(),
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
        _uiState.update { state: UiStateScreen<UiScannerModel> ->
            val currentData: UiScannerModel = state.data ?: UiScannerModel()
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
        launch(dispatchers.default) {
            val current = _uiState.value.data ?: UiScannerModel()
            val visibleFiles = current.analyzeState.groupedFiles.values.flatten()
            val duplicateOriginals = current.analyzeState.duplicateOriginals
            val newState = !current.analyzeState.areAllFilesSelected
            val selection = if (newState) {
                visibleFiles.associateWith { file ->
                    if (file in duplicateOriginals) current.analyzeState.fileSelectionMap[file] == true else true
                }
            } else emptyMap()
            val count = if (newState) {
                visibleFiles.count { file -> file !in duplicateOriginals || current.analyzeState.fileSelectionMap[file] == true }
            } else 0
            _uiState.update { state ->
                val data = state.data ?: UiScannerModel()
                state.copy(
                    data = data.copy(
                        analyzeState = data.analyzeState.copy(
                            areAllFilesSelected = newState,
                            fileSelectionMap = selection,
                            selectedFilesCount = count
                        )
                    )
                )
            }
        }
    }

    fun toggleSelectFilesForCategory(category: String) {
        launch(context = dispatchers.default) {
            _uiState.update { currentState: UiStateScreen<UiScannerModel> ->
                val currentData: UiScannerModel = currentState.data ?: UiScannerModel()
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
            _uiState.update { currentState: UiStateScreen<UiScannerModel> ->
                val currentData = currentState.data ?: UiScannerModel()
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

            _uiState.update { state: UiStateScreen<UiScannerModel> ->
                val currentData = state.data ?: UiScannerModel()
                state.copy(
                    data = currentData.copy(
                        analyzeState = currentData.analyzeState.copy(
                            state = CleaningState.Cleaning,
                            cleaningType = CleaningType.DELETE
                        )
                    )
                )
            }

            val currentScreenData: UiScannerModel = screenData ?: run {
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
                ) { _: Unit, currentData: UiScannerModel ->
                    val (groupedFilesUpdated, duplicateOriginals, duplicateGroups) =
                        runBlocking(dispatchers.io) {
                            computeGroupedFiles(
                                scannedFiles = currentData.analyzeState.scannedFileList.filterNot {
                                    filesToDelete.contains(it)
                                },
                                emptyFolders = currentData.analyzeState.emptyFolderList,
                                fileTypesData = currentData.analyzeState.fileTypesData,
                                preferences = mapOf(),
                                includeDuplicates = includeDuplicates
                            )
                        }

                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot {
                        filesToDelete.contains(it)
                    },
                        groupedFiles = groupedFilesUpdated,
                        duplicateOriginals = duplicateOriginals,
                        duplicateGroups = duplicateGroups,
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
                    delay(RESULT_DELAY_MS)
                    _uiState.update { state ->
                        val current = state.data ?: UiScannerModel()
                        state.copy(data = current.copy(analyzeState = current.analyzeState.copy(state = CleaningState.Result)))
                    }
                    launch {
                        dataStore.saveLastScanTimestamp(timestamp = System.currentTimeMillis())
                    }
                    loadInitialData()
                    loadWhatsAppMedia()
                    loadClipboardData()
                    CleaningEventBus.notifyCleaned()
                } else if (result is DataState.Error) {
                    _uiState.update { s ->
                        val currentErrorData = s.data ?: UiScannerModel()
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
                val currentData = state.data ?: UiScannerModel()
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
                    val (groupedFilesUpdated3, duplicateOriginals3, duplicateGroups3) =
                        runBlocking(dispatchers.io) {
                            computeGroupedFiles(
                                scannedFiles = currentData.analyzeState.scannedFileList.filterNot { existingFile ->
                                    filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                                },
                                emptyFolders = currentData.analyzeState.emptyFolderList,
                                fileTypesData = currentData.analyzeState.fileTypesData,
                                preferences = mapOf(),
                                includeDuplicates = includeDuplicates
                            )
                        }

                    currentData.copy(analyzeState = currentData.analyzeState.copy(scannedFileList = currentData.analyzeState.scannedFileList.filterNot { existingFile ->
                        filesToMove.any { movedFile -> existingFile.absolutePath == movedFile.absolutePath }
                    },

                        groupedFiles = groupedFilesUpdated3,
                        duplicateOriginals = duplicateOriginals3,
                        duplicateGroups = duplicateGroups3,
                        selectedFilesCount = 0,
                        areAllFilesSelected = false,
                        isAnalyzeScreenVisible = false,
                        fileSelectionMap = emptyMap()))
                }

                if (result is DataState.Success) {
                    delay(RESULT_DELAY_MS)
                    _uiState.update { state ->
                        val current = state.data ?: UiScannerModel()
                        state.copy(data = current.copy(analyzeState = current.analyzeState.copy(state = CleaningState.Result)))
                    }
                    updateTrashSize(totalFileSizeToMove)
                    loadInitialData()
                    loadWhatsAppMedia()
                    loadClipboardData()
                    CleaningEventBus.notifyCleaned()
                } else if (result is DataState.Error) {
                    _uiState.update { s ->
                        val currentErrorData = s.data ?: UiScannerModel()
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
                            cleanedSpace = FileSizeFormatter.format(cleanedSpace)
                        )
                    )
                }
            }
        }
    }

    private fun loadWhatsAppMedia() {
        _whatsAppMediaLoaded.value = false
        launch(context = dispatchers.io) {
            val (images, videos, docs) = getWhatsAppMediaSummary()
            _whatsAppMediaSummary.value = WhatsAppMediaSummary(
                images = images,
                videos = videos,
                documents = docs
            )
            _whatsAppMediaLoaded.value = true
        }
    }

    private fun checkWhatsAppInstalled() {
        launch(context = dispatchers.io) {
            val installed = runCatching {
                val uri = "content://com.whatsapp.provider.media".toUri()
                application.contentResolver.getType(uri)
                true
            }.getOrElse { false }
            _isWhatsAppInstalled.value = installed
        }
    }

    private fun loadPromotedApp() {
        launch(dispatchers.io) {
            val promoted = getPromotedAppUseCase(application.packageName)
            promoted?.let {
                _uiState.updateData(ScreenState.Success()) { current ->
                    current.copy(promotedApp = promoted)
                }
            }
        }
    }

    private fun loadCleanStreak() {
        launch(dispatchers.io) {
            dataStore.streakCount.collect { streak ->
                _cleanStreak.value = streak
            }
        }
    }

    private fun loadLargestFilesPreview() {
        launch(dispatchers.io) {
            getLargestFilesUseCase(5).collectLatest { result ->
                if (result is DataState.Success) {
                    _largestFiles.value = result.data
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

                _uiState.update { state: UiStateScreen<UiScannerModel> ->
                    val currentData: UiScannerModel = state.data ?: UiScannerModel()
                    state.copy(
                        data = currentData.copy(
                            analyzeState = currentData.analyzeState.copy(
                                isAnalyzeScreenVisible = true,
                                scannedFileList = emptyList(),
                                emptyFolderList = emptyList(),
                                groupedFiles = emptyMap(),
                                duplicateGroups = emptyList(),
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
                        duplicateGroups = emptyList(),
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

    fun onCleanApks(apkFiles: List<File>) {
        deleteFiles(apkFiles.toSet())
    }

    fun onCleanWhatsAppFiles() {
        _whatsAppMediaSummary.value = WhatsAppMediaSummary()
        CleaningEventBus.notifyCleaned()
        postSnackbar(UiTextHelper.StringResource(R.string.feature_not_available), isError = false)
    }

    fun onCleanCache() {
        postSnackbar(UiTextHelper.StringResource(R.string.feature_not_available), isError = false)
    }

    fun setHideStreakDialogVisibility(isVisible: Boolean) {
        _uiState.updateData(ScreenState.Success()) { currentData ->
            currentData.copy(isHideStreakDialogVisible = isVisible)
        }
    }

    fun hideStreakForNow() {
        launch(dispatchers.io) { dataStore.saveStreakHideUntil(startOfNextWeek()) }
        setHideStreakDialogVisibility(false)
    }

    fun hideStreakPermanently() {
        launch(dispatchers.io) { dataStore.saveShowStreakCard(false) }
        setHideStreakDialogVisibility(false)
    }

    private fun loadStreakCardVisibility() {
        launch(dispatchers.io) {
            combine(dataStore.showStreakCard, dataStore.streakHideUntil) { show, hide ->
                _showStreakCardPref.value = show
                _streakHideUntil.value = hide
                show && hide <= System.currentTimeMillis()
            }.collect { visible ->
                _showStreakCard.value = visible
            }
        }
    }

    private fun startOfNextWeek(): Long {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            java.time.LocalDate.now()
                .with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } else {
            val cal = java.util.Calendar.getInstance()
            val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
            var daysUntilMonday = (java.util.Calendar.MONDAY - dayOfWeek + 7) % 7
            if (daysUntilMonday == 0) daysUntilMonday = 7
            cal.add(java.util.Calendar.DAY_OF_YEAR, daysUntilMonday)
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
    }

    fun onClipboardClear() {
        runCatching {
            clipboardManager.clearClipboardCompat()
        }
        _clipboardPreview.value = null
        _clipboardDetectedSensitive.value = false
        CleaningEventBus.notifyCleaned()
    }

    private fun loadClipboardData() {
        val text = clipboardManager.primaryClip?.takeIf { it.itemCount > 0 }
            ?.getItemAt(0)?.coerceToText(application)?.toString()?.trim()
        _clipboardPreview.value = text
        _clipboardDetectedSensitive.value = text?.let { detectSensitive(it) } ?: false
    }

    private fun detectSensitive(text: String): Boolean {
        val urlPattern = Regex("https?://")
        val emailPattern = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        val pwdPattern = Regex("(?i)password")
        return urlPattern.containsMatchIn(text) || emailPattern.containsMatchIn(text) || pwdPattern.containsMatchIn(text)
    }

    private fun postSnackbar(message : UiTextHelper , isError : Boolean) {
        screenState.showSnackbar(snackbar = UiSnackbar(message = message , isError = isError , timeStamp = System.currentTimeMillis() , type = ScreenMessageType.SNACKBAR))
    }

    private suspend fun Flow<PagingData<File>>.toList(): List<File> {
        val list = mutableListOf<File>()
        this.collectLatest { pagingData ->
            pagingData.map { file ->
                list.add(file)
                file
            }
        }
        return list
    }

    override fun onCleared() {
        clipboardManager.removePrimaryClipChangedListener(clipboardListener)
        super.onCleared()
    }
}