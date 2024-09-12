package com.d4rk.cleaner.utils.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.d4rk.cleaner.constants.error.ErrorType.STORAGE_PERMISSION
import com.d4rk.cleaner.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.utils.error.ErrorHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.FileNotFoundException
import java.io.IOException

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiErrorModel = MutableStateFlow(UiErrorModel())
    val uiErrorModel: StateFlow<UiErrorModel> = _uiErrorModel.asStateFlow()

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    private fun handleError(exception: Throwable) {
        val errorType : ErrorType = when (exception) {
            is SecurityException -> STORAGE_PERMISSION
            is IOException -> ErrorType.ANALYSIS_ERROR
            is FileNotFoundException -> ErrorType.CLEANING_ERROR
            else -> ErrorType.UNKNOWN_ERROR
        }
        handleError(errorType, exception)

        _uiErrorModel.value = UiErrorModel(
            showErrorDialog = true,
            errorMessage = when (errorType) {
                STORAGE_PERMISSION -> getApplication<Application>().getString(R.string.storage_permission_error)
                ErrorType.ANALYSIS_ERROR -> getApplication<Application>().getString(R.string.analysis_error)
                ErrorType.CLEANING_ERROR -> exception.message ?: getApplication<Application>().getString(R.string.cleaning_error)
                else -> getApplication<Application>().getString(R.string.unknown_error)
            }
        )
    }

    fun dismissErrorDialog() {
        _uiErrorModel.value = UiErrorModel(showErrorDialog = false)
    }

    protected open fun handleError(errorType: ErrorType, exception: Throwable) {
        ErrorHandler.handleError(getApplication(), errorType)
    }

    protected fun showLoading() {
        _isLoading.value = true
    }

    protected fun hideLoading() {
        _isLoading.value = false
    }
}