package com.d4rk.cleaner.core.ui.viewmodel

import android.app.Application
import android.content.ActivityNotFoundException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.data.model.ui.error.UiErrorModel
import com.d4rk.android.libs.apptoolkit.utils.error.ErrorHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException

open class BaseViewModel(application : Application) : AndroidViewModel(application) {
    private val _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading

    private val _uiErrorModel : MutableStateFlow<UiErrorModel> = MutableStateFlow(UiErrorModel())
    val uiErrorModel : StateFlow<UiErrorModel> = _uiErrorModel.asStateFlow()

    protected val coroutineExceptionHandler : CoroutineExceptionHandler = CoroutineExceptionHandler { _ , exception ->
        Log.e("BaseViewModel" , "Coroutine Exception:" , exception)
        handleError(exception = exception)
    }

    val _visibilityStates : MutableStateFlow<List<Boolean>> = MutableStateFlow(emptyList())
    val visibilityStates : StateFlow<List<Boolean>> = _visibilityStates.asStateFlow()

    private val _isFabVisible : MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val isFabVisible : StateFlow<Boolean> = _isFabVisible.asStateFlow()

    private fun handleError(exception : Throwable) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val errorType : com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType = when (exception) {
                is SecurityException -> com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.SECURITY_EXCEPTION
                is IOException -> com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.IO_EXCEPTION
                is ActivityNotFoundException -> com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.ACTIVITY_NOT_FOUND
                is IllegalArgumentException -> com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.ILLEGAL_ARGUMENT
                is FileNotFoundException -> com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.FILE_NOT_FOUND
                else -> com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.UNKNOWN_ERROR
            }

            _uiErrorModel.value = UiErrorModel(
                showErrorDialog = true , errorMessage = getErrorMessage(errorType = errorType)
            )

            ErrorHandler.handleError(
                applicationContext = getApplication() ,
                errorType = errorType ,
                exception = exception
            )
        }
    }

    private fun getErrorMessage(errorType : com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType) : String {
        return getApplication<Application>().getString(
            when (errorType) {
                com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.SECURITY_EXCEPTION -> com.d4rk.android.libs.apptoolkit.R.string.security_error
                com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.IO_EXCEPTION -> com.d4rk.android.libs.apptoolkit.R.string.io_error
                com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.ACTIVITY_NOT_FOUND -> com.d4rk.android.libs.apptoolkit.R.string.activity_not_found
                com.d4rk.android.libs.apptoolkit.utils.constants.error.ErrorType.ILLEGAL_ARGUMENT -> com.d4rk.android.libs.apptoolkit.R.string.illegal_argument_error
                else -> com.d4rk.android.libs.apptoolkit.R.string.unknown_error
            }
        )
    }

    fun dismissErrorDialog() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiErrorModel.value = UiErrorModel(showErrorDialog = false)
        }
    }

    protected fun showLoading() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _isLoading.value = true
        }
    }

    protected fun hideLoading() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _isLoading.value = false
        }
    }

    protected fun showFab() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _isFabVisible.value = true
        }
    }
}