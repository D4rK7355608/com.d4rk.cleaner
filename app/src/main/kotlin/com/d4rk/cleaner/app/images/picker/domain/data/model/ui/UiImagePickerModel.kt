package com.d4rk.cleaner.app.images.picker.domain.data.model.ui

import android.net.Uri

data class UiImagePickerModel(
    val selectedImageUri: Uri? = null,
    val isFabVisible: Boolean = false
)
