package com.d4rk.cleaner.ui.home

import android.app.Application
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.storage.StorageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

class HomeViewModel(application : Application) : AndroidViewModel(application) {
    val progress = MutableLiveData(0f)
    val storageUsed = MutableLiveData<String>()
    val storageTotal = MutableLiveData<String>()

    init {
        updateStorageInfo()
    }

    private fun updateStorageInfo() {
        viewModelScope.launch {
            val storageManager =
                    getApplication<Application>().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageStatsManager =
                    getApplication<Application>().getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageVolume = storageManager.primaryStorageVolume
            val totalSize : Long
            val usedSize : Long
            val uuidStr = storageVolume.uuid
            val uuid : UUID =
                    if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)
            totalSize = storageStatsManager.getTotalBytes(uuid)
            usedSize = totalSize - storageStatsManager.getFreeBytes(uuid)
            storageUsed.postValue((usedSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString())
            storageTotal.postValue((totalSize / (1024.0 * 1024.0 * 1024.0)).roundToInt().toString())
            progress.postValue(usedSize.toFloat() / totalSize.toFloat())
        }
    }

    fun analyze() {

    }

    fun clean() {

    }
}