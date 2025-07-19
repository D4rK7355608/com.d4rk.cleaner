package com.d4rk.cleaner.app.contacts.cleaner.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import com.d4rk.cleaner.app.contacts.cleaner.domain.data.model.RawContactInfo

sealed interface ContactsCleanerEvent : UiEvent {
    data object LoadDuplicates : ContactsCleanerEvent
    data class DeleteOlder(val group: List<RawContactInfo>) : ContactsCleanerEvent
    data class MergeAll(val group: List<RawContactInfo>) : ContactsCleanerEvent
}
