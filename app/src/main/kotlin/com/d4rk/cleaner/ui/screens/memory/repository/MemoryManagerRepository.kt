package com.d4rk.cleaner.ui.screens.memory.repository

import android.app.Application
import com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo
import com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.core.data.model.ui.screens.UiMemoryManagerModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class MemoryManagerRepository(application : Application) :
    MemoryManagerRepositoryImplementation(application) {

    suspend fun getMemoryManagerData(onSuccess : (com.d4rk.cleaner.core.data.model.ui.screens.UiMemoryManagerModel) -> Unit) {
        withContext(Dispatchers.IO) {

            val storageInfoDeferred : Deferred<com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo> = async { getStorageInfo() }
            val ramInfoDeferred : Deferred<com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo> = async { getRamInfo() }

            val storageInfo : com.d4rk.cleaner.core.data.model.ui.memorymanager.StorageInfo = storageInfoDeferred.await()
            val ramInfo : com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo = ramInfoDeferred.await()
            withContext(Dispatchers.Main) {
                onSuccess(com.d4rk.cleaner.core.data.model.ui.screens.UiMemoryManagerModel(storageInfo = storageInfo , ramInfo = ramInfo))
            }
        }
    }

    suspend fun getRamInfo(onSuccess : (com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo) -> Unit) {
        withContext(Dispatchers.IO) {
            val ramInfo : com.d4rk.cleaner.core.data.model.ui.memorymanager.RamInfo = getRamInfo()
            withContext(Dispatchers.Main) {
                onSuccess(ramInfo)
            }
        }
    }
}