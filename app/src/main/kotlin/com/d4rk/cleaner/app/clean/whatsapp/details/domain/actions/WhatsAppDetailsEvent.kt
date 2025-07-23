package com.d4rk.cleaner.app.clean.whatsapp.details.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.SortType

sealed interface WhatsAppDetailsEvent : UiEvent {
    data class LoadFiles(val type: String, val page: Int = 0) : WhatsAppDetailsEvent
    data object ToggleView : WhatsAppDetailsEvent
    data class ApplySort(
        val type: SortType,
        val descending: Boolean,
        val startDate: Long?,
        val endDate: Long?,
    ) : WhatsAppDetailsEvent
}
