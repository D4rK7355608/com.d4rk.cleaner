package com.d4rk.cleaner.data.model.ui

import com.d4rk.cleaner.constants.error.ErrorType

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val errorType: ErrorType, val message: String? = null) : UiState<Nothing>()
}