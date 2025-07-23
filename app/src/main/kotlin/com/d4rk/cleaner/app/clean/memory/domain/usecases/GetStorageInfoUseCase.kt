package com.d4rk.cleaner.app.clean.memory.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.clean.memory.domain.interfaces.MemoryRepository
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetStorageInfoUseCase(private val memoryRepository: MemoryRepository) {

    operator fun invoke(): Flow<DataState<StorageInfo, Errors>> = flow {

        runCatching {
            memoryRepository.getStorageInfo()
        }.onSuccess { storageInfo ->
            emit(DataState.Success(data = storageInfo))
        }.onFailure { throwable ->
            emit(DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_GET_STORAGE_INFO)))
        }
    }
}