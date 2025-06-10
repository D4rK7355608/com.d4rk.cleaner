package com.d4rk.cleaner.app.clean.home.domain.actions

import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed class HomeAction : ActionEvent {
    data class ShowSnackbar(val snackbar : UiSnackbar) : HomeAction()
}