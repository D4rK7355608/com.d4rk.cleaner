package com.d4rk.cleaner.app.apps.manager.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkFileManager
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

class GetApkFilesFromStorageUseCase(private val apkFileManager : ApkFileManager) {
    operator fun invoke() : Flow<DataState<List<ApkInfo> , Errors>> {
        return apkFileManager.getApkFilesFromStorage()
    }
}