package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class GetLargestFilesUseCase(private val repository: ScannerRepositoryInterface) {
    operator fun invoke(limit: Int): Flow<DataState<List<File>, Errors>> = flow {
        emit(DataState.Loading())
        runCatching {
            repository.getLargestFiles(limit)
        }.onSuccess { files ->
            emit(DataState.Success(files))
        }.onFailure { throwable ->
            if (throwable is CancellationException) throw throwable
            emit(DataState.Error(error = throwable.toError()))
        }
    }
}
