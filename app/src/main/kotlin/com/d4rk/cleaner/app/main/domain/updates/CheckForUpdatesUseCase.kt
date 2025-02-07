package com.d4rk.cleaner.app.main.domain.updates

import com.d4rk.cleaner.core.domain.DataError
import com.d4rk.cleaner.core.domain.Result

interface CheckForUpdatesUseCase {
    suspend fun execute(): Result<Boolean, DataError>
}
