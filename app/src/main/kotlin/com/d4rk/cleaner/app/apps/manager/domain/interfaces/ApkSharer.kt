package com.d4rk.cleaner.app.apps.manager.domain.interfaces

import android.content.Intent
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow

interface ApkSharer {
    fun prepareShareIntent(apkPath: String): Flow<DataState<Intent, Errors>>
}