package com.d4rk.cleaner.app.clean.whatsapp.summary.ui.model

// TODO: use the one from lib
sealed class ViewState<out T> {
    data object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    data class Error(val message: String) : ViewState<Nothing>()
}
