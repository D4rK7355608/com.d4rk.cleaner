package com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageOptimizerViewModel : ViewModel() {
    val compressionLevelLiveData = MutableLiveData<Int>()
    fun setCompressionLevel(compressionLevel: Int) {
        compressionLevelLiveData.value = compressionLevel
    }
}
