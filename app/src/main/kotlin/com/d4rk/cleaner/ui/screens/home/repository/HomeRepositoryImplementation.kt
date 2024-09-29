package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(val application : Application) {

    suspend fun getStorageInfo() : UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(application) { used , total , usageProgress ->
                continuation.resume(
                    UiHomeModel(
                        progress = usageProgress , storageUsed = used , storageTotal = total
                    )
                )
            }
        }
    }

    fun deleteFiles(filesToDelete : Set<File>) {
        filesToDelete.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }
    }
}