package com.d4rk.cleaner.app.images.picker.ui

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImagePickerViewModel(application : Application) : BaseViewModel(application = application) {
    private val _selectedImageUri : MutableState<Uri?> = mutableStateOf(value = null)
    val selectedImageUri : Uri? get() = _selectedImageUri.value

    init {
        initializeVisibilityStates()
    }

    private fun initializeVisibilityStates() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            delay(timeMillis = 100L)
            showFab()
        }
    }

    fun setSelectedImageUri(uri : Uri?) {
        _selectedImageUri.value = uri
    }
}