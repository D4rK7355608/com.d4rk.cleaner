package com.d4rk.cleaner.app.clean.contacts.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.clean.contacts.data.ContactsRepository
import com.d4rk.cleaner.app.clean.contacts.domain.actions.ContactsCleanerAction
import com.d4rk.cleaner.app.clean.contacts.domain.actions.ContactsCleanerEvent
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.RawContactInfo
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.UiContactsCleanerModel
import kotlinx.coroutines.flow.update

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
            _uiState.update { it.copy(screenState = ScreenState.IsLoading()) }

            runCatching { repository.findDuplicates() }
                .onSuccess { groups ->
                    println("groups.isEmpty() = ${groups.isEmpty()}")
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
                            errors = it.errors + UiSnackbar(
                                message = UiTextHelper.DynamicString(e.message ?: "error"),
                                isError = true
                            )
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
