package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class DeleteFilesUseCase(
    private val homeRepository : ScannerRepositoryInterface
) {
    operator fun invoke(filesToDelete: Set<File>): Flow<DataState<Unit, Errors>> = flow {
        runCatching {
            homeRepository.deleteFiles(filesToDelete)
        }.onSuccess {
            emit(DataState.Success(Unit))
        }.onFailure { throwable ->
            if (throwable is CancellationException) throw throwable
            emit(DataState.Error(error = throwable.toError()))
        }
    }
}