package com.d4rk.cleaner.app.clean.trash.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class RestoreFromTrashUseCase(
    private val repository: ScannerRepositoryInterface
) {
    operator fun invoke(filesToRestore: Set<File>): Flow<DataState<Unit, Errors>> = flow {
        runCatching {
            repository.restoreFromTrash(filesToRestore)
            emit(DataState.Success(Unit))
        }
    }
}