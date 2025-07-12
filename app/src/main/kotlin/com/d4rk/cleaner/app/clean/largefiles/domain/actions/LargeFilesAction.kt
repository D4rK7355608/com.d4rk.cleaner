package com.d4rk.cleaner.app.clean.largefiles.domain.actions

import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed interface LargeFilesAction : ActionEvent {
    data class ShowSnackbar(val message: UiSnackbar) : LargeFilesAction
}
