package com.d4rk.cleaner.utils.viewmodel

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
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
        Log.e("BaseViewModel" , "Coroutine Exception: " , exception)
        handleError(exception)
    }

    private fun handleError(exception: Throwable) {
        val errorType: ErrorType = when (exception) {
            is SecurityException -> ErrorType.STORAGE_PERMISSION
            is IOException -> ErrorType.ANALYSIS_ERROR
            is FileNotFoundException -> ErrorType.CLEANING_ERROR
            is PackageManager.NameNotFoundException -> ErrorType.APP_LOADING_ERROR
            is ActivityNotFoundException -> ErrorType.APP_INFO_ERROR
            else -> ErrorType.UNKNOWN_ERROR
        }
        handleError(errorType, exception)

        _uiErrorModel.value = UiErrorModel(
            showErrorDialog = true,
            errorMessage = when (errorType) {
                ErrorType.STORAGE_PERMISSION -> getApplication<Application>().getString(R.string.storage_permission_error)
                ErrorType.ANALYSIS_ERROR -> getApplication<Application>().getString(R.string.analysis_error)
                ErrorType.CLEANING_ERROR -> exception.message ?: getApplication<Application>().getString(R.string.cleaning_error)
                ErrorType.APP_LOADING_ERROR -> getApplication<Application>().getString(R.string.app_loading_error)
                ErrorType.APK_INSTALLATION_ERROR -> getApplication<Application>().getString(R.string.apk_installation_error)
                ErrorType.APK_SHARING_ERROR -> getApplication<Application>().getString(R.string.apk_sharing_error)
                ErrorType.APP_SHARING_ERROR -> getApplication<Application>().getString(R.string.app_sharing_error)
                ErrorType.APP_INFO_ERROR -> getApplication<Application>().getString(R.string.app_info_error)
                ErrorType.APP_UNINSTALLATION_ERROR -> getApplication<Application>().getString(R.string.app_uninstallation_error)
                ErrorType.STORAGE_INFO_ERROR -> getApplication<Application>().getString(R.string.storage_info_error)
                ErrorType.RAM_INFO_ERROR -> getApplication<Application>().getString(R.string.ram_info_error)
                ErrorType.STORAGE_BREAKDOWN_ERROR -> getApplication<Application>().getString(R.string.storage_breakdown_error)
                ErrorType.UNKNOWN_ERROR -> getApplication<Application>().getString(R.string.unknown_error)
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