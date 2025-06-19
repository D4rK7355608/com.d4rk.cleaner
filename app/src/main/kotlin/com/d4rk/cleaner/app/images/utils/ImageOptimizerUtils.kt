package com.d4rk.cleaner.app.images.utils

import com.d4rk.cleaner.app.images.compressor.domain.data.model.CompressionLevel

fun getCompressionLevelFromSliderValue(sliderValue : Float) : CompressionLevel {
    return when {
        sliderValue < 33f -> CompressionLevel.LOW
        sliderValue < 66f -> CompressionLevel.MEDIUM
        else -> CompressionLevel.HIGH
    }
}