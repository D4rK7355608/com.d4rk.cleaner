package com.d4rk.cleaner.app.images.compressor.domain.data.model.ui

import android.net.Uri

/** UI representation for the Image Optimizer screen */
data class UiImageOptimizerState(
    val selectedImageUri: Uri? = null,
    val compressedImageUri: Uri? = null,
    val compressedSizeKB: Double = 0.0,
    val isLoading: Boolean = false,
    val quickCompressValue: Int = 50,
    val fileSizeKB: Int = 0,
    val originalWidth: Int = 0,
    val originalHeight: Int = 0,
    val manualWidth: Int = 0,
    val manualHeight: Int = 0,
    val manualQuality: Int = 50,
    val currentTab: Int = 0,
    val showSaveSnackbar: Boolean = false
)
