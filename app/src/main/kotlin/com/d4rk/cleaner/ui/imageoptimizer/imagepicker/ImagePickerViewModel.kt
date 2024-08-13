package com.d4rk.cleaner.ui.imageoptimizer.imagepicker

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ImagePickerViewModel : ViewModel() {
    private val _selectedImageUri = mutableStateOf<Uri?>(value = null)
    val selectedImageUri: Uri? get() = _selectedImageUri.value

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }
}