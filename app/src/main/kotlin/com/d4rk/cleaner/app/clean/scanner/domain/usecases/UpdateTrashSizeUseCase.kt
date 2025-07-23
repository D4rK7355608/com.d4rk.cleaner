package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Errors
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateTrashSizeUseCase(
    private val homeRepository: ScannerRepositoryInterface
) {
    operator fun invoke(sizeChange: Long): Flow<DataState<Unit, Errors>> = flow {
        runCatching {
            if (sizeChange > 0) {
                homeRepository.addTrashSize(size = sizeChange)
            } else if (sizeChange < 0) {
                homeRepository.subtractTrashSize(size = -sizeChange)
            }
            emit(DataState.Success(data = Unit))
        }
    }
}