package com.d4rk.cleaner.app.clean.home.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.home.domain.`interface`.HomeRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class DeleteFilesUseCase(
    private val homeRepository : HomeRepositoryInterface
) {
    operator fun invoke(filesToDelete : Set<File>) : Flow<DataState<Unit , Errors>> = flow {
        runCatching {
            homeRepository.deleteFiles(filesToDelete)
            emit(DataState.Success(Unit))
        }.onFailure {
            // e -> emit(DataState.Error(e as Errors))
        }
    }
}