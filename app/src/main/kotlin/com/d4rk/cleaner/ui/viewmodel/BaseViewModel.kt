package com.d4rk.cleaner.ui.viewmodel

import android.app.Application
import android.content.ActivityNotFoundException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.R
import com.d4rk.cleaner.constants.error.ErrorType
import com.d4rk.cleaner.utils.error.ErrorHandler
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
            val errorType : ErrorType = when (exception) {
                is SecurityException -> ErrorType.SECURITY_EXCEPTION
                is IOException -> ErrorType.IO_EXCEPTION
                is FileNotFoundException -> ErrorType.FILE_NOT_FOUND
                is ActivityNotFoundException -> ErrorType.ACTIVITY_NOT_FOUND
                is IllegalArgumentException -> ErrorType.ILLEGAL_ARGUMENT
                else -> ErrorType.UNKNOWN_ERROR
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

    private fun getErrorMessage(errorType : ErrorType) : String {
        return getApplication<Application>().getString(
            when (errorType) {
                ErrorType.SECURITY_EXCEPTION -> R.string.security_error
                ErrorType.IO_EXCEPTION -> R.string.io_error
                ErrorType.FILE_NOT_FOUND -> R.string.file_not_found
                ErrorType.ACTIVITY_NOT_FOUND -> R.string.activity_not_found
                ErrorType.ILLEGAL_ARGUMENT -> R.string.illegal_argument_error
                ErrorType.UNKNOWN_ERROR -> R.string.unknown_error
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