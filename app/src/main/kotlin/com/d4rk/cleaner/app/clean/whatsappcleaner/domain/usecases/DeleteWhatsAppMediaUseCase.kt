package com.d4rk.cleaner.app.clean.whatsappcleaner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.interfaces.WhatsAppCleanerRepository
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class DeleteWhatsAppMediaUseCase(private val repository: WhatsAppCleanerRepository) {
    operator fun invoke(files: List<File>): Flow<DataState<Unit, Errors>> = flow {
        runCatching { repository.deleteFiles(files) }
            .onSuccess { emit(DataState.Success(Unit)) }
            .onFailure { emit(DataState.Error(error = it.toError())) }
    }
}
