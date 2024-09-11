package com.d4rk.cleaner.ui.appmanager

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.d4rk.cleaner.data.model.ui.screens.UiAppManagerModel
import com.d4rk.cleaner.ui.appmanager.repository.AppManagerRepository
import com.d4rk.cleaner.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppManagerViewModel(application: Application) : BaseViewModel(application) {
    private val repository = AppManagerRepository(application)
    private val _uiState = MutableStateFlow(UiAppManagerModel())
    val uiState: StateFlow<UiAppManagerModel> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            loadAppData()
        }
    }

    private suspend fun loadAppData() {
        showLoading()
        try {
            val installedApps = withContext(Dispatchers.IO) {
                repository.getInstalledApps()
            }
            val apkFiles = withContext(Dispatchers.IO) {
                repository.getApkFilesFromStorage()
            }
            _uiState.value = UiAppManagerModel(installedApps, apkFiles)
        } catch (e: Exception) {
            handleError(ErrorType.APP_LOADING_ERROR, e)
        } finally {
            hideLoading()
        }
    }

    override fun handleError(errorType: ErrorType, exception: Throwable) {
        when (errorType) {
            ErrorType.APP_LOADING_ERROR -> _uiState.value = _uiState.value.copy(
                showErrorDialog = true,
                errorMessage = exception.message
                    ?: getApplication<Application>().getString(R.string.app_loading_error)
            )
            else -> {
                _uiState.value = _uiState.value.copy(
                    showErrorDialog = true,
                    errorMessage = getApplication<Application>().getString(R.string.unknown_error)
                )
            }
        }
    }
}