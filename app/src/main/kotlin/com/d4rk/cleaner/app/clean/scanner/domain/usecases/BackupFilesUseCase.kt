package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import android.net.Uri
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class BackupFilesUseCase(
    private val repository: ScannerRepositoryInterface
) {
    operator fun invoke(files: List<File>, destination: Uri): Flow<DataState<Unit, Errors>> = flow {
        runCatching {
            repository.backupFiles(files, destination)
            emit(DataState.Success(Unit))
        }.onFailure {
            // emit(DataState.Error(Errors.Other(it)))
        }
    }
}
