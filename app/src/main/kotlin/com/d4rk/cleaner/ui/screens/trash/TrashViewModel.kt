package com.d4rk.cleaner.ui.screens.trash

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.screens.UiTrashModel
import com.d4rk.cleaner.ui.screens.home.repository.HomeRepository
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class TrashViewModel(application : Application) : BaseViewModel(application) {
    private val repository = HomeRepository(DataStore(application) , application)
    private val _uiState = MutableStateFlow(UiTrashModel())
    val uiState : StateFlow<UiTrashModel> = _uiState

    init {
        loadTrashItems()
    }

    private fun loadTrashItems() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            val trashFiles = repository.getTrashFiles()
            _uiState.value = _uiState.value.copy(trashFiles = trashFiles)
            hideLoading()
        }
    }


    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val updatedSelections = _uiState.value.fileSelectionStates + (file to isChecked)
            _uiState.value = _uiState.value.copy(fileSelectionStates = updatedSelections ,
                                                 selectedFileCount = updatedSelections.count { it.value })
        }
    }

    fun restoreFromTrash() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val filesToRestore = _uiState.value.fileSelectionStates.filter { it.value }.keys
            showLoading()
            repository.restoreFromTrash(filesToRestore) {
                loadTrashItems()
            }
            hideLoading()
        }
    }

    fun clean() {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val filesToDelete = _uiState.value.fileSelectionStates.filter { it.value }.keys
            showLoading()
            repository.deleteFiles(filesToDelete)
            hideLoading()
        }
    }
}