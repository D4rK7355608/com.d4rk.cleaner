package com.d4rk.cleaner.app.apps.manager.domain.usecases

import android.content.Intent
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppSharer
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

class ShareAppUseCase(private val appSharer: AppSharer) {
    operator fun invoke(packageName: String): Flow<DataState<Intent, Errors>> {
        return appSharer.shareApp(packageName = packageName)
    }
}