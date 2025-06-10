package com.d4rk.cleaner.app.apps.manager.data

import android.app.Application
import android.content.pm.ApplicationInfo
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.PackageManagerFacade
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PackageManagerFacadeImpl(private val application : Application) : PackageManagerFacade {
    override fun getInstalledApplications(flags : Int) : Flow<DataState<List<ApplicationInfo> , Errors>> = flow {
        runCatching {
            application.packageManager.getInstalledApplications(flags)
        }.onSuccess { installedApps ->
            emit(value = DataState.Success(data = installedApps))
        }.onFailure { throwable : Throwable ->
            emit(value = DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_GET_INSTALLED_APPS)))
        }
    }
}