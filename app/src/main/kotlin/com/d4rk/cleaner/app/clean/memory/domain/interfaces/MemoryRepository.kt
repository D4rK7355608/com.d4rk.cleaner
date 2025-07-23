package com.d4rk.cleaner.app.clean.memory.domain.interfaces

import com.d4rk.cleaner.app.clean.memory.domain.data.model.RamInfo // Assuming these are now in domain
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo

interface MemoryRepository {
    suspend fun getStorageInfo(): StorageInfo
    fun getRamInfo(): RamInfo
}