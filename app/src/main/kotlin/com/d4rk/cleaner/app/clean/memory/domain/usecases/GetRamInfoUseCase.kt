package com.d4rk.cleaner.app.clean.memory.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.memory.domain.data.model.RamInfo
import com.d4rk.cleaner.app.clean.memory.domain.interfaces.MemoryRepository
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRamInfoUseCase(private val memoryRepository: MemoryRepository) {

    operator fun invoke(): Flow<DataState<RamInfo, Errors>> = flow {
        runCatching {
            memoryRepository.getRamInfo()
        }.onSuccess { ramInfo ->
            emit(DataState.Success(data = ramInfo))
        }.onFailure { throwable ->
            emit(DataState.Error(error = throwable.toError(default = Errors.UseCase.FAILED_TO_GET_RAM_INFO)))
        }
    }
}