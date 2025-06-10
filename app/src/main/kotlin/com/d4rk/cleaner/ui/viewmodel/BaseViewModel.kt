package com.d4rk.cleaner.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel(application : Application) : AndroidViewModel(application) {
    private val _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading

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

        }
    }

    fun dismissErrorDialog() {
        viewModelScope.launch(context = coroutineExceptionHandler) {

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