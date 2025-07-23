package com.d4rk.cleaner.app.apps.manager.domain.usecases


import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppUninstaller
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow


class UninstallAppUseCase(private val appUninstaller: AppUninstaller) {
    operator fun invoke(packageName: String): Flow<DataState<Unit, Errors>> {
        return appUninstaller.uninstallApp(packageName = packageName)
    }
}