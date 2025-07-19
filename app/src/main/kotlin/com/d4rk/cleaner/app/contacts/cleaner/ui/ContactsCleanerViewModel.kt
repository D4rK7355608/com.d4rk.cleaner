package com.d4rk.cleaner.app.contacts.cleaner.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.contacts.cleaner.data.ContactsRepository
import com.d4rk.cleaner.app.contacts.cleaner.domain.actions.ContactsCleanerAction
import com.d4rk.cleaner.app.contacts.cleaner.domain.actions.ContactsCleanerEvent
import com.d4rk.cleaner.app.contacts.cleaner.domain.data.model.RawContactInfo
import com.d4rk.cleaner.app.contacts.cleaner.domain.data.model.UiContactsCleanerModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactsCleanerViewModel(
    private val repository: ContactsRepository,
    private val dispatchers: DispatcherProvider
) : ScreenViewModel<UiContactsCleanerModel, ContactsCleanerEvent, ContactsCleanerAction>(
    initialState = UiStateScreen(data = UiContactsCleanerModel())
) {

    init { onEvent(ContactsCleanerEvent.LoadDuplicates) }

    override fun onEvent(event: ContactsCleanerEvent) {
        when (event) {
            ContactsCleanerEvent.LoadDuplicates -> loadDuplicates()
            is ContactsCleanerEvent.DeleteOlder -> deleteOlder(event.group)
            is ContactsCleanerEvent.MergeAll -> merge(event.group)
        }
    }

    private fun loadDuplicates() {
        launch(context = dispatchers.io) {
            runCatching { repository.findDuplicates() }
                .onSuccess { groups ->
                    _uiState.update {
                        it.copy(
                            screenState = if (groups.isEmpty()) ScreenState.NoData() else ScreenState.Success(),
                            data = UiContactsCleanerModel(groups)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            screenState = ScreenState.Error(),
                            errors = it.errors + UiSnackbar(UiTextHelper.DynamicString(e.message ?: "error"), true)
                        )
                    }
                }
        }
    }

    private fun deleteOlder(group: List<RawContactInfo>) {
        launch(context = dispatchers.io) {
            repository.deleteOlder(group)
            onEvent(ContactsCleanerEvent.LoadDuplicates)
        }
    }

    private fun merge(group: List<RawContactInfo>) {
        launch(context = dispatchers.io) {
            repository.mergeContacts(group)
            onEvent(ContactsCleanerEvent.LoadDuplicates)
        }
    }
}
