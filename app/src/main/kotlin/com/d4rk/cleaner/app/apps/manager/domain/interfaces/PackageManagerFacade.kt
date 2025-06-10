package com.d4rk.cleaner.app.apps.manager.domain.interfaces

import android.content.pm.ApplicationInfo
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

interface PackageManagerFacade {
    fun getInstalledApplications(flags : Int) : Flow<DataState<List<ApplicationInfo> , Errors>>
}