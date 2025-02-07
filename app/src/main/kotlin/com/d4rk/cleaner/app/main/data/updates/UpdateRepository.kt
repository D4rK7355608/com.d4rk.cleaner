package com.d4rk.cleaner.app.main.data.updates

import com.d4rk.cleaner.core.domain.DataError
import com.d4rk.cleaner.core.domain.Result

interface UpdateRepository {
    suspend fun checkForUpdates(): Result<Boolean, DataError>
}
