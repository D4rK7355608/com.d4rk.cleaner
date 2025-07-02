package com.d4rk.cleaner.app.clean.trash.domain.usecases

import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.flow.first

class GetTrashSizeUseCase(private val dataStore: DataStore) {
    suspend operator fun invoke(): Long {
        return dataStore.trashSize.first()
    }
}
