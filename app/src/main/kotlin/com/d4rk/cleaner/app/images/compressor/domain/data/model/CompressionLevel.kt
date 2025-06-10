package com.d4rk.cleaner.app.images.compressor.domain.data.model

import com.d4rk.cleaner.R

enum class CompressionLevel(val stringRes : Int , val defaultPercentage : Int) {
    LOW(R.string.low , defaultPercentage = 25) , MEDIUM(
        R.string.medium , defaultPercentage = 50
    ) ,
    HIGH(R.string.high , defaultPercentage = 75)
}