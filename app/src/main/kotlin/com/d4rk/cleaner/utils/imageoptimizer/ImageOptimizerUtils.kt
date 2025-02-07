package com.d4rk.cleaner.utils.imageoptimizer

import com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel

// TODO move in quick compression repository impl
fun getCompressionLevelFromSliderValue(sliderValue: Float): com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel {
    return when {
        sliderValue < 33f -> com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel.LOW
        sliderValue < 66f -> com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel.MEDIUM
        else -> com.d4rk.cleaner.core.data.model.ui.imageoptimizer.CompressionLevel.HIGH
    }
}