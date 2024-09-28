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

        viewModelScope.launch(coroutineExceptionHandler) {
            while (true) {
                delay(timeMillis = 5000)
                updateRamMemoryData()
            }
        }
    }

    private fun loadMemoryData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            repository.getMemoryManagerData { uiMemoryData ->
                _uiMemoryManagerModel.value = uiMemoryData
            }
            hideLoading()
        }
    }

    private fun updateRamMemoryData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.getRamInfo { ramInfo ->
                _uiMemoryManagerModel.value = _uiMemoryManagerModel.value.copy(ramInfo = ramInfo)
            }
        }
    }

    fun toggleListExpanded() {
        _uiMemoryManagerModel.value = _uiMemoryManagerModel.value.copy(
            listExpanded = ! _uiMemoryManagerModel.value.listExpanded
        )
    }
}