package com.d4rk.cleaner.ui.screens.trash

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.core.data.model.ui.screens.UiTrashModel
import com.d4rk.cleaner.ui.screens.home.repository.HomeRepository
import com.d4rk.cleaner.core.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for the Trash screen. Manages the display and interaction with trashed files.
 *
 * @param application The application instance.
 * @author Mihai-Cristian Condrea
 */
class TrashViewModel(application : Application) : BaseViewModel(application) {
    private val repository = HomeRepository(DataStore(application) , application)
    private val _uiState = MutableStateFlow(com.d4rk.cleaner.core.data.model.ui.screens.UiTrashModel())
    val uiState : StateFlow<com.d4rk.cleaner.core.data.model.ui.screens.UiTrashModel> = _uiState

    init {
        loadTrashItems()
    }

    /**
     * Loads the list of trashed files from the repository.
     */
    private fun loadTrashItems() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            _uiState.value = _uiState.value.copy(trashFiles = repository.getTrashFiles())
            hideLoading()
        }
    }

    /**
     * Handles changes in file selection state.
     *
     * @param file The file whose selection state has changed.
     * @param isChecked True if the file is selected, false otherwise.
     */
    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val updatedSelections = _uiState.value.fileSelectionStates + (file to isChecked)
            _uiState.value = _uiState.value.copy(fileSelectionStates = updatedSelections ,
                                                 selectedFileCount = updatedSelections.count { it.value })
        }
    }

    /**
     * Restores selected files from the trash.
     */
    fun restoreFromTrash() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            val filesToRestore = _uiState.value.fileSelectionStates.filter { it.value }.keys
            val totalFileSizeToRestore = filesToRestore.sumOf { it.length() }
            repository.restoreFromTrashRepository(filesToRestore) {
                loadTrashItems()
            }
            repository.subtractTrashSize(totalFileSizeToRestore)
            hideLoading()
        }
    }

    /**
     * Permanently deletes selected files from the trash.
     */
    fun clean() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            showLoading()
            val filesToDelete = _uiState.value.fileSelectionStates.filter { it.value }.keys
            val totalFileSizeToDelete = filesToDelete.sumOf { it.length() }
            with(repository) {
                deleteFilesRepository(filesToDelete) {
                    loadTrashItems()
                }

                with(dataStore) {
                    subtractTrashSize(totalFileSizeToDelete)
                    addCleanedSpace(totalFileSizeToDelete)
                    saveLastScanTimestamp(System.currentTimeMillis())
                }
            }
            hideLoading()
        }
    }
}