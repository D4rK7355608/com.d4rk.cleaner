package com.d4rk.cleaner.app.clean.trash.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.DeleteFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.UpdateTrashSizeUseCase
import com.d4rk.cleaner.app.clean.trash.domain.actions.TrashAction
import com.d4rk.cleaner.app.clean.trash.domain.actions.TrashEvent
import com.d4rk.cleaner.app.clean.trash.domain.data.model.ui.UiTrashModel
import com.d4rk.cleaner.app.clean.trash.domain.usecases.GetTrashFilesUseCase
import com.d4rk.cleaner.app.clean.trash.domain.usecases.RestoreFromTrashUseCase
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class TrashViewModel(
    private val getTrashFilesUseCase : GetTrashFilesUseCase ,
    private val deleteFilesUseCase : DeleteFilesUseCase ,
    private val updateTrashSizeUseCase : UpdateTrashSizeUseCase ,
    private val restoreFromTrashUseCase : RestoreFromTrashUseCase ,
    private val dispatchers : DispatcherProvider ,
    private val dataStore: DataStore,
) : ScreenViewModel<UiTrashModel , TrashEvent , TrashAction>(
    initialState = UiStateScreen(data = UiTrashModel())
) {

    init {
        onEvent(TrashEvent.LoadTrashItems)
        observeTrashInfo()
    }

    private fun observeTrashInfo() {
        launch(context = dispatchers.io) {
            dataStore.trashSize.collect { size ->
                _uiState.updateData(newState = _uiState.value.screenState) { current ->
                    current.copy(trashSize = size)
                }
            }
        }
        launch(context = dispatchers.io) {
            dataStore.trashFileOriginalPaths.collect { paths ->
                _uiState.updateData(newState = _uiState.value.screenState) { current ->
                    current.copy(trashFileOriginalPaths = paths)
                }
            }
        }
    }

    override fun onEvent(event : TrashEvent) {
        when (event) {
            TrashEvent.LoadTrashItems -> loadTrashItems()
            is TrashEvent.OnFileSelectionChange -> onFileSelectionChange(event.file , event.isChecked)
            TrashEvent.RestoreSelectedFiles -> restoreSelectedFromTrash()
            TrashEvent.DeleteSelectedFilesPermanently -> deleteSelectedPermanently()
        }
    }

    private fun loadTrashItems() {
        launch(context = dispatchers.io) {
            getTrashFilesUseCase().collectLatest { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is DataState.Loading -> currentState.copy(

                        )

                        is DataState.Success -> {
                            if (result.data.isEmpty()) {
                                currentState.copy(
                                    screenState = ScreenState.NoData() , data = currentState.data?.copy(
                                        trashFiles = emptyList() ,
                                        fileSelectionStates = emptyMap() ,
                                        selectedFileCount = 0 ,
                                    ) ?: UiTrashModel(trashFiles = emptyList())
                                )
                            }
                            else {
                                currentState.copy(
                                    screenState = ScreenState.Success() , data = currentState.data?.copy(
                                        trashFiles = result.data ,
                                        fileSelectionStates = emptyMap() ,
                                        selectedFileCount = 0 ,
                                    ) ?: UiTrashModel(trashFiles = result.data)
                                )
                            }
                        }

                        is DataState.Error -> currentState.copy(
                            screenState = ScreenState.Error() , errors = currentState.errors + UiSnackbar(
                                message = UiTextHelper.DynamicString("Failed to load trash items: ${result.error}") , isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun onFileSelectionChange(file : File , isChecked : Boolean) {
        _uiState.updateData(newState = _uiState.value.screenState) { currentData ->
            val updatedSelections = currentData.fileSelectionStates.toMutableMap().apply {
                this[file.absolutePath] = isChecked
            }
            currentData.copy(
                fileSelectionStates = updatedSelections , selectedFileCount = updatedSelections.count { it.value })
        }
    }

    private fun restoreSelectedFromTrash() {
        launch(context = dispatchers.io) {
            val pathsToRestore = _uiState.value.data?.fileSelectionStates?.filter { it.value }?.keys ?: emptySet()
            val filesToRestore = pathsToRestore.map { File(it) }.toSet()
            if (filesToRestore.isEmpty()) {
                sendAction(TrashAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("No files selected to restore."))))
                return@launch
            }

            val totalFileSizeToRestore = filesToRestore.sumOf { it.length() }

            restoreFromTrashUseCase(filesToRestore).collectLatest { restoreResult ->
                if (restoreResult is DataState.Success) {

                    updateTrashSizeUseCase(- totalFileSizeToRestore).collectLatest { updateSizeResult ->
                        if (updateSizeResult is DataState.Error) {
                            _uiState.update { it.copy(errors = it.errors + UiSnackbar(message = UiTextHelper.DynamicString("Failed to update trash size: ${updateSizeResult.error}") , isError = true)) }
                        }

                        onEvent(TrashEvent.LoadTrashItems)
                    }
                }
                else if (restoreResult is DataState.Error) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            errors = currentState.errors + UiSnackbar(
                                message = UiTextHelper.DynamicString("Failed to restore files: ${restoreResult.error}") , isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun deleteSelectedPermanently() {
        launch(context = dispatchers.io) {
            val pathsToDelete = _uiState.value.data?.fileSelectionStates?.filter { it.value }?.keys ?: emptySet()
            val filesToDelete = pathsToDelete.map { File(it) }.toSet()
            if (filesToDelete.isEmpty()) {
                sendAction(TrashAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("No files selected to delete."))))
                return@launch
            }

            val totalFileSizeToDelete = filesToDelete.sumOf { it.length() }

            deleteFilesUseCase(filesToDelete).collectLatest { deleteResult ->
                if (deleteResult is DataState.Success) {

                    updateTrashSizeUseCase(- totalFileSizeToDelete).collectLatest { updateSizeResult ->
                        if (updateSizeResult is DataState.Error) {
                            handleOperationError("Failed to update trash size: ${updateSizeResult.error}")
                        }

                        onEvent(TrashEvent.LoadTrashItems)
                    }
                }
                else if (deleteResult is DataState.Error) {
                    handleOperationError("Failed to delete files permanently: ${deleteResult.error}")

                }
            }
        }
    }

    private fun handleOperationError(message : String) {
        _uiState.update { currentState ->
            currentState.copy(

                errors = currentState.errors + UiSnackbar(
                    message = UiTextHelper.DynamicString(message) , isError = true
                )
            )
        }
    }
}