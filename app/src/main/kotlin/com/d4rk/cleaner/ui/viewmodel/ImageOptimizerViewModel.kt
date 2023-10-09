package com.d4rk.cleaner.ui.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class ImageOptimizerViewModel : ViewModel() {
    val compressionLevelLiveData = MutableLiveData<Int>()
    fun setCompressionLevel(compressionLevel: Int) {
        compressionLevelLiveData.value = compressionLevel
    }
}
