package com.d4rk.cleaner.app.apps.manager.domain.usecases

import android.content.Intent
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkSharer
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

class ShareApkUseCase(private val apkSharer: ApkSharer) {
    operator fun invoke(apkPath: String): Flow<DataState<Intent, Errors>> {
        return apkSharer.prepareShareIntent(apkPath = apkPath)
    }
}