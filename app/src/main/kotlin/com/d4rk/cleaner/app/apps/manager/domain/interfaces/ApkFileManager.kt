package com.d4rk.cleaner.app.apps.manager.domain.interfaces

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

interface ApkFileManager {
    fun getApkFilesFromStorage(): Flow<DataState<List<ApkInfo>, Errors>>
}