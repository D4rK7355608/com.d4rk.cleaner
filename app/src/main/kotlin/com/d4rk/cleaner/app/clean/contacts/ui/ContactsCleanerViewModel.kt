package com.d4rk.cleaner.app.clean.contacts.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.contacts.domain.usecases.DeleteOlderContactsUseCase
import com.d4rk.cleaner.app.clean.contacts.domain.usecases.GetDuplicateContactsUseCase
import com.d4rk.cleaner.app.clean.contacts.domain.usecases.MergeContactsUseCase
import com.d4rk.cleaner.app.clean.contacts.domain.actions.ContactsCleanerAction
import com.d4rk.cleaner.app.clean.contacts.domain.actions.ContactsCleanerEvent
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.RawContactInfo
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.UiContactsCleanerModel
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.DuplicateContactGroup
import com.d4rk.cleaner.core.utils.extensions.asUiText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

class ContactsCleanerViewModel(
    private val getDuplicatesUseCase: GetDuplicateContactsUseCase,
    private val deleteOlderUseCase: DeleteOlderContactsUseCase,
    private val mergeContactsUseCase: MergeContactsUseCase,
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
            is ContactsCleanerEvent.ToggleGroupSelection -> handleToggleGroupSelection(event.group)
            is ContactsCleanerEvent.ToggleContactSelection -> handleToggleContactSelection(event.contact)
            ContactsCleanerEvent.MergeSelectedContacts -> handleMergeSelectedContacts()
            ContactsCleanerEvent.DeleteSelectedContacts -> handleDeleteSelectedContacts()
        }
    }

    private fun loadDuplicates() {
        launch(context = dispatchers.io) {
            getDuplicatesUseCase().collectLatest { result ->
                _uiState.update { current ->

                    println("result: $result")


                    println("error: ${(result as? DataState.Error)?.error}")
                    println("message error: ${current.errors}")


                    when (result) {
                        is DataState.Loading -> current.copy(screenState = ScreenState.IsLoading())
                        is DataState.Success -> current.copy(
                            screenState = if (result.data.isEmpty()) ScreenState.NoData() else ScreenState.Success(),
                            data = UiContactsCleanerModel(
                                result.data.map { group ->
                                    DuplicateContactGroup(
                                        contacts = group.map { it.copy(isSelected = false) },
                                        isSelected = false
                                    )
                                }
                            )
                        )
                        is DataState.Error -> current.copy(
                            screenState = ScreenState.Error(),
                            errors = current.errors + UiSnackbar(
                                message = result.error.asUiText(),
                                isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun deleteOlder(group: List<RawContactInfo>) {
        launch(context = dispatchers.io) {
            deleteOlderUseCase(group).collectLatest { result ->
                if (result is DataState.Error) {
                    _uiState.update { current ->
                        current.copy(
                            errors = current.errors + UiSnackbar(
                                message = result.error.asUiText(),
                                isError = true
                            )
                        )
                    }
                }
                onEvent(ContactsCleanerEvent.LoadDuplicates)
            }
        }
    }

    private fun merge(group: List<RawContactInfo>) {
        launch(context = dispatchers.io) {
            mergeContactsUseCase(group).collectLatest { result ->
                if (result is DataState.Error) {
                    _uiState.update { current ->
                        current.copy(
                            errors = current.errors + UiSnackbar(
                                message = result.error.asUiText(),
                                isError = true
                            )
                        )
                    }
                }
                onEvent(ContactsCleanerEvent.LoadDuplicates)
            }
        }
    }

    private fun handleToggleGroupSelection(group: List<RawContactInfo>) {
        _uiState.update { current ->
            val updated = current.data?.duplicates?.map { duplicateGroup ->
                if (duplicateGroup.contacts == group) {
                    val shouldSelect = duplicateGroup.contacts.any { !it.isSelected }
                    val updatedContacts = duplicateGroup.contacts.map { it.copy(isSelected = shouldSelect) }
                    duplicateGroup.copy(contacts = updatedContacts, isSelected = shouldSelect)
                } else {
                    duplicateGroup
                }
            }
            current.copy(data = current.data?.copy(duplicates = updated ?: emptyList()))
        }
    }

    private fun handleToggleContactSelection(contact: RawContactInfo) {
        _uiState.update { current ->
            val updatedGroups = current.data?.duplicates?.map { group ->
                val updatedContacts = group.contacts.map { raw ->
                    if (raw.rawContactId == contact.rawContactId) raw.copy(isSelected = !raw.isSelected) else raw
                }
                val groupSelected = updatedContacts.all { it.isSelected }
                group.copy(contacts = updatedContacts, isSelected = groupSelected)
            }
            current.copy(data = current.data?.copy(duplicates = updatedGroups ?: emptyList()))
        }
    }

    private fun handleMergeSelectedContacts() {
        val selected = _uiState.value.data?.duplicates?.flatMap { it.contacts.filter { contact -> contact.isSelected } }
        if (selected?.size?.let { it >= 2 } == true) merge(selected)
    }

    private fun handleDeleteSelectedContacts() {
        val selected = _uiState.value.data?.duplicates?.flatMap { it.contacts.filter { contact -> contact.isSelected } }
        if (selected?.isNotEmpty() == true) deleteOlder(selected)
    }
}
