package com.d4rk.cleaner.app.widgets.data

import android.app.Application
import com.d4rk.cleaner.app.clean.memory.data.MemoryRepositoryImpl
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo

class StorageStatsRepository(private val application: Application) {
    suspend fun getStorageInfo(): StorageInfo {
        return MemoryRepositoryImpl(application).getStorageInfo()
    }
}
