package com.d4rk.cleaner.data.model.ui.imageoptimizer

import android.net.Uri

data class ImageOptimizerState(
    val selectedImageUri: Uri? = null,
    val compressedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val quickCompressValue: Int = 50,
    val fileSizeKB: Int = 0,
    val manualWidth: Int = 0,
    val manualHeight: Int = 0,
    val manualQuality: Int = 50,
    val currentTab: Int = 0,
)