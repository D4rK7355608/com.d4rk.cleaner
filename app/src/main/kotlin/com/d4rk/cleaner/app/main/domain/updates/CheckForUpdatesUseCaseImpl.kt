package com.d4rk.cleaner.app.main.domain.updates

import com.d4rk.cleaner.app.main.data.updates.UpdateRepository
import com.d4rk.cleaner.core.domain.DataError
import com.d4rk.cleaner.core.domain.Result
import com.d4rk.cleaner.core.domain.map

class CheckForUpdatesUseCaseImpl(
    private val repository: UpdateRepository
) : CheckForUpdatesUseCase {
    override suspend fun execute(): Result<Boolean, DataError> {
        val result = repository.checkForUpdates()
        return result.map { isUpdateAvailable ->
            isUpdateAvailable
        }
    }
}
