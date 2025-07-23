package com.d4rk.cleaner.app.apps.manager.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkInstaller
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

class InstallApkUseCase(private val apkInstaller: ApkInstaller) {
    operator fun invoke(apkPath: String): Flow<DataState<Unit, Errors>> {
        return apkInstaller.installApk(apkPath = apkPath)
    }
}