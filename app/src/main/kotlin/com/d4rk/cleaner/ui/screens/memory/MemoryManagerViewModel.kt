package com.d4rk.cleaner.ui.screens.memory

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.model.ui.screens.UiMemoryManagerModel
import com.d4rk.cleaner.ui.screens.memory.repository.MemoryManagerRepository
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing and providing information about device memory (RAM and storage).
 */
class MemoryManagerViewModel(
    application : Application ,
) : BaseViewModel(application) {
    private val repository = MemoryManagerRepository(application)
    private val _uiMemoryManagerModel = MutableStateFlow(UiMemoryManagerModel())
    val uiMemoryManagerModel : StateFlow<UiMemoryManagerModel> = _uiMemoryManagerModel.asStateFlow()

    init {
        loadMemoryData()

        viewModelScope.launch(context = coroutineExceptionHandler) {
            while (true) {
                delay(timeMillis = 5000)
                updateRamMemoryData()
            }
        }
    }

    private fun loadMemoryData() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            showLoading()
            repository.getMemoryManagerData { uiMemoryData ->
                _uiMemoryManagerModel.update { uiMemoryData }
            }
            hideLoading()
        }
    }

    private fun updateRamMemoryData() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getRamInfo { ramInfo ->
                _uiMemoryManagerModel.update { it.copy(ramInfo = ramInfo) }
            }
        }
    }

    fun toggleListExpanded() {
        _uiMemoryManagerModel.update {
            it.copy(
                listExpanded = ! it.listExpanded
            )
        }
    }
}