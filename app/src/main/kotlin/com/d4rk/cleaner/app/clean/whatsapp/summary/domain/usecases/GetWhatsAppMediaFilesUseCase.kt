package com.d4rk.cleaner.app.clean.whatsapp.summary.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.repository.WhatsAppCleanerRepository
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class GetWhatsAppMediaFilesUseCase(private val repository: WhatsAppCleanerRepository) {
    operator fun invoke(type: String, offset: Int, limit: Int): Flow<DataState<List<File>, Errors>> = flow {
        emit(DataState.Loading())
        runCatching { repository.listMediaFiles(type, offset, limit) }
            .onSuccess { emit(DataState.Success(it)) }
            .onFailure { emit(DataState.Error(error = it.toError())) }
    }
}
