package com.d4rk.cleaner.ui.memory

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.d4rk.cleaner.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.data.model.ui.screens.UiMemoryManagerModel
import com.d4rk.cleaner.ui.memory.repository.MemoryManagerRepository
import com.d4rk.cleaner.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing and providing information about device memory (RAM and storage).
 */
class MemoryManagerViewModel(
    application: Application,
) : BaseViewModel(application) {
    private val repository = MemoryManagerRepository(application)
    private val _uiMemoryManagerModel = MutableStateFlow(UiMemoryManagerModel())
    val uiMemoryManagerModel: StateFlow<UiMemoryManagerModel> = _uiMemoryManagerModel.asStateFlow()

    init {
        loadMemoryData()

        viewModelScope.launch(coroutineExceptionHandler) {
            while (true) {
                delay(5000)
                val ramInfo = repository.getRamInfo()
                _uiMemoryManagerModel.value = _uiMemoryManagerModel.value.copy(ramInfo = ramInfo)
            }
        }
    }

    override fun handleError(errorType: ErrorType, exception: Throwable) {
        when (errorType) {
            ErrorType.ANALYSIS_ERROR,
            ErrorType.STORAGE_PERMISSION -> TODO()

            ErrorType.STORAGE_INFO_ERROR,
            ErrorType.RAM_INFO_ERROR,
            ErrorType.STORAGE_BREAKDOWN_ERROR -> {
                _uiMemoryManagerModel.value = _uiMemoryManagerModel.value.copy(
                    error = UiErrorModel(
                        showErrorDialog = true,
                        errorMessage = when (errorType) {
                            ErrorType.STORAGE_INFO_ERROR -> getApplication<Application>().getString(
                                R.string.storage_info_error
                            )

                            ErrorType.RAM_INFO_ERROR -> getApplication<Application>().getString(R.string.ram_info_error)
                            ErrorType.STORAGE_BREAKDOWN_ERROR -> getApplication<Application>().getString(
                                R.string.storage_breakdown_error
                            )

                            else -> getApplication<Application>().getString(R.string.unknown_error) // Default case
                        }
                    )
                )
            }

            else -> super.handleError(errorType, exception)
        }
    }

    private fun loadMemoryData() {
        viewModelScope.launch(coroutineExceptionHandler) {
            showLoading()
            val uiMemoryData = repository.getMemoryManagerData()
            _uiMemoryManagerModel.value = uiMemoryData
            hideLoading()
        }
    }

    fun toggleListExpanded() {
        _uiMemoryManagerModel.value = _uiMemoryManagerModel.value.copy(
            listExpanded = !_uiMemoryManagerModel.value.listExpanded
        )
    }
}