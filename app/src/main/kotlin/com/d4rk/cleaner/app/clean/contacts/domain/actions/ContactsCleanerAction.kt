package com.d4rk.cleaner.app.clean.contacts.domain.actions

import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed interface ContactsCleanerAction : ActionEvent {
    data class ShowMessage(val snackbar: UiSnackbar) : ContactsCleanerAction
}
