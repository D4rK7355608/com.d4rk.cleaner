package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import androidx.paging.PagingData
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnalyzeFilesUseCase(
    private val homeRepository : ScannerRepositoryInterface
) {
    operator fun invoke() : Flow<DataState<Flow<PagingData<File>>, Errors>> = flow {
        emit(DataState.Loading())
        runCatching {
            val result = homeRepository.getAllFiles()
            emit(DataState.Success(result))
        }.onFailure { throwable ->
            emit(value = DataState.Error(error = throwable.toError()))
        }
    }
}