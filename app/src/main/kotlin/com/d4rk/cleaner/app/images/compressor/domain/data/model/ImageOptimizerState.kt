package com.d4rk.cleaner.app.images.compressor.domain.data.model

import android.net.Uri

data class ImageOptimizerState(
    val selectedImageUri : Uri? = null ,
    val compressedImageUri : Uri? = null ,
    val compressedSizeKB : Double = 0.0 ,
    val isLoading : Boolean = false ,
    val quickCompressValue : Int = 50 ,
    val fileSizeKB : Int = 0 ,
    val originalWidth : Int = 0 ,
    val originalHeight : Int = 0 ,
    val manualWidth : Int = 0 ,
    val manualHeight : Int = 0 ,
    val manualQuality : Int = 50 ,
    val currentTab : Int = 0 ,
    val showSaveSnackbar : Boolean = false
)