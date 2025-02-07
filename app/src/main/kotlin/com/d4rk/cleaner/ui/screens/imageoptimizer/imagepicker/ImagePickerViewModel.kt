package com.d4rk.cleaner.ui.screens.imageoptimizer.imagepicker

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.d4rk.cleaner.core.ui.viewmodel.BaseViewModel

class ImagePickerViewModel(application : Application) : BaseViewModel(application = application) {
    private val _selectedImageUri = mutableStateOf<Uri?>(value = null)
    val selectedImageUri: Uri? get() = _selectedImageUri.value

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }
}