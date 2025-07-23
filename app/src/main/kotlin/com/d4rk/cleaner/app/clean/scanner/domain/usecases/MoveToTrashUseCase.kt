package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class MoveToTrashUseCase(
    private val homeRepository: ScannerRepositoryInterface
) {
    operator fun invoke(filesToMove: List<File>): Flow<DataState<Unit, Errors>> = flow {
        runCatching {
            emit(DataState.Loading())
            homeRepository.moveToTrash(filesToMove)
            emit(DataState.Success(Unit))
        }.onFailure {
            //    e -> emit(DataState.Error(Errors.Other(e)))
        }
    }
}