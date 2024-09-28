package com.d4rk.cleaner.ui.screens.memory.repository

import android.app.Application
import com.d4rk.cleaner.data.model.ui.memorymanager.RamInfo
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.data.model.ui.screens.UiMemoryManagerModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class MemoryManagerRepository(application : Application) :
    MemoryManagerRepositoryImplementation(application) {

    suspend fun getMemoryManagerData(onSuccess : (UiMemoryManagerModel) -> Unit) {
        withContext(Dispatchers.IO) {

            val storageInfoDeferred : Deferred<StorageInfo> = async { getStorageInfo() }
            val ramInfoDeferred : Deferred<RamInfo> = async { getRamInfo() }

            val storageInfo : StorageInfo = storageInfoDeferred.await()
            val ramInfo : RamInfo = ramInfoDeferred.await()
            withContext(Dispatchers.Main) {
                onSuccess(UiMemoryManagerModel(storageInfo = storageInfo , ramInfo = ramInfo))
            }
        }
    }

    suspend fun getRamInfo(onSuccess : (RamInfo) -> Unit) {
        withContext(Dispatchers.IO) {
            val ramInfo : RamInfo = getRamInfo()
            withContext(Dispatchers.Main) {
                onSuccess(ramInfo)
            }
        }
    }
}