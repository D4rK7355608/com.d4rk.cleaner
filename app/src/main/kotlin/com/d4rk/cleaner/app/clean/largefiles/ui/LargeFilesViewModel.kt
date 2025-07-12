package com.d4rk.cleaner.app.clean.largefiles.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.clean.largefiles.domain.actions.LargeFilesAction
import com.d4rk.cleaner.app.clean.largefiles.domain.actions.LargeFilesEvent
import com.d4rk.cleaner.app.clean.largefiles.domain.data.model.ui.UiLargeFilesModel
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.DeleteFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetLargestFilesUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class LargeFilesViewModel(
    private val getLargestFilesUseCase: GetLargestFilesUseCase,
    private val deleteFilesUseCase: DeleteFilesUseCase,
    private val dispatchers: DispatcherProvider
) : ScreenViewModel<UiLargeFilesModel, LargeFilesEvent, LargeFilesAction>(
    initialState = UiStateScreen(data = UiLargeFilesModel())
) {

    private val limit = 20

    init {
        onEvent(LargeFilesEvent.LoadLargeFiles)
    }

    override fun onEvent(event: LargeFilesEvent) {
        when (event) {
            LargeFilesEvent.LoadLargeFiles -> loadLargeFiles()
            is LargeFilesEvent.OnFileSelectionChange -> onFileSelectionChange(event.file, event.isChecked)
            LargeFilesEvent.DeleteSelectedFiles -> deleteSelected()
        }
    }

    private fun loadLargeFiles() {
        launch(context = dispatchers.io) {
            getLargestFilesUseCase(limit).collectLatest { result ->
                _uiState.update { current ->
                    when (result) {
                        is DataState.Loading -> current.copy(screenState = ScreenState.IsLoading())
                        is DataState.Success -> current.copy(
                            screenState = if (result.data.isEmpty()) ScreenState.NoData() else ScreenState.Success(),
                            data = current.data?.copy(files = result.data, fileSelectionStates = emptyMap(), selectedFileCount = 0)
                                ?: UiLargeFilesModel(files = result.data)
                        )
                        is DataState.Error -> current.copy(
                            screenState = ScreenState.Error(),
                            errors = current.errors + UiSnackbar(
                                message = UiTextHelper.DynamicString("${result.error}"),
                                isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun onFileSelectionChange(file: File, isChecked: Boolean) {
        _uiState.updateData(newState = _uiState.value.screenState) { current ->
            val updated = current.fileSelectionStates.toMutableMap().apply { this[file] = isChecked }
            current.copy(fileSelectionStates = updated, selectedFileCount = updated.count { it.value })
        }
    }

    private fun deleteSelected() {
        launch(context = dispatchers.io) {
            val files = _uiState.value.data?.fileSelectionStates?.filter { it.value }?.keys ?: emptySet()
            if (files.isEmpty()) {
                sendAction(LargeFilesAction.ShowSnackbar(UiSnackbar(message = UiTextHelper.DynamicString("No files selected"))))
                return@launch
            }
            deleteFilesUseCase(files).collectLatest { result ->
                if (result is DataState.Error) {
                    _uiState.update { current ->
                        current.copy(errors = current.errors + UiSnackbar(message = UiTextHelper.DynamicString("Failed to delete files: ${result.error}"), isError = true))
                    }
                }
                onEvent(LargeFilesEvent.LoadLargeFiles)
            }
        }
    }
}
