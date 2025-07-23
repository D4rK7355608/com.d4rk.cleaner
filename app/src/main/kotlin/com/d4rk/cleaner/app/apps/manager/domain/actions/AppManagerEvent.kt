package com.d4rk.cleaner.app.apps.manager.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import com.d4rk.cleaner.app.apps.manager.domain.data.model.AppManagerItem

sealed class AppManagerEvent : UiEvent {
    object LoadAppData : AppManagerEvent()
    data class ShareItem(val item: AppManagerItem) : AppManagerEvent()
    data object DismissSnackbar : AppManagerEvent()
}