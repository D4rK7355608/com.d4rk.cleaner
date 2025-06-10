package com.d4rk.cleaner.app.main.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent

sealed interface MainEvent : UiEvent {
    data object LoadNavigation : MainEvent
    data object CheckForUpdates : MainEvent
}