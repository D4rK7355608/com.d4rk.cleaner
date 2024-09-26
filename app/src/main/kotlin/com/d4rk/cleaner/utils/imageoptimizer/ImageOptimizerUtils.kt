package com.d4rk.cleaner.utils.imageoptimizer

import com.d4rk.cleaner.data.model.ui.imageoptimizer.CompressionLevel

// TODO move in quick compression repository impl
fun getCompressionLevelFromSliderValue(sliderValue: Float): CompressionLevel {
    return when {
        sliderValue < 33f -> CompressionLevel.LOW
        sliderValue < 66f -> CompressionLevel.MEDIUM
        else -> CompressionLevel.HIGH
    }
}