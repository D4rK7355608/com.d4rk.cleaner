package com.d4rk.cleaner.data.model.ui.imageoptimizer

import com.d4rk.cleaner.R

enum class CompressionLevel(val stringRes : Int , val defaultPercentage : Int) {
    LOW(R.string.low , 25) , MEDIUM(R.string.medium , 50) , HIGH(R.string.high , 75)
}