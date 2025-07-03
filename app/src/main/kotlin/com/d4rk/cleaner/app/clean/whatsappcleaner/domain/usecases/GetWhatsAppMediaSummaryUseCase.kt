package com.d4rk.cleaner.app.clean.whatsappcleaner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.interface.WhatsAppCleanerRepository
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWhatsAppMediaSummaryUseCase(private val repository: WhatsAppCleanerRepository) {
    operator fun invoke(): Flow<DataState<WhatsAppMediaSummary, Errors>> = flow {
        emit(DataState.Loading())
        runCatching { repository.getMediaSummary() }
            .onSuccess { emit(DataState.Success(it)) }
            .onFailure { emit(DataState.Error(it.toError())) }
    }
}
