package com.d4rk.cleaner.app.clean.contacts.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.contacts.data.ContactsRepository
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.RawContactInfo
import com.d4rk.cleaner.core.domain.model.network.Errors
import com.d4rk.cleaner.core.utils.extensions.toError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteContactsUseCase(private val repository: ContactsRepository) {
    operator fun invoke(contacts: List<RawContactInfo>): Flow<DataState<Unit, Errors>> = flow {
        runCatching { repository.deleteContacts(contacts) }
            .onSuccess { emit(DataState.Success(Unit)) }
            .onFailure { emit(DataState.Error(error = it.toError())) }
    }
}
