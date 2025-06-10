package com.d4rk.cleaner.app.apps.manager.domain.usecases

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.PackageManagerFacade
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow


class GetInstalledAppsUseCase(private val packageManagerFacade : PackageManagerFacade) {
    operator fun invoke() : Flow<DataState<List<ApplicationInfo> , Errors>> {
        return packageManagerFacade.getInstalledApplications(flags = PackageManager.GET_META_DATA)
    }
}