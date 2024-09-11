package com.d4rk.cleaner.utils.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.d4rk.cleaner.constants.error.ErrorType
import com.d4rk.cleaner.constants.error.ErrorType.STORAGE_PERMISSION
import com.d4rk.cleaner.utils.error.ErrorHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.FileNotFoundException
import java.io.IOException

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    private fun handleError(exception: Throwable) {
        val errorType = when (exception) {
            is SecurityException -> STORAGE_PERMISSION
            is IOException -> ErrorType.ANALYSIS_ERROR
            is FileNotFoundException -> ErrorType.CLEANING_ERROR
            else -> ErrorType.UNKNOWN_ERROR
        }
        handleError(errorType, exception)
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