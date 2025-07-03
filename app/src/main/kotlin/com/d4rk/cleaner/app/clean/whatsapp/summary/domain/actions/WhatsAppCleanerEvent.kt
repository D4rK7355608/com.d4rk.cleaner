package com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import java.io.File

sealed interface WhatsAppCleanerEvent : UiEvent {
    data object LoadMedia : WhatsAppCleanerEvent
    data object CleanAll : WhatsAppCleanerEvent
    data class DeleteSelected(val files: List<File>) : WhatsAppCleanerEvent
}
