package com.d4rk.cleaner.app.clean.whatsapp.details.domain.actions

import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed interface WhatsAppDetailsAction : ActionEvent {
    data class ShowSnackbar(val message: UiSnackbar) : WhatsAppDetailsAction
}
