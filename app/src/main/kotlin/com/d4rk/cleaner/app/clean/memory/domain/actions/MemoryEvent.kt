package com.d4rk.cleaner.app.clean.memory.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent

sealed class MemoryEvent : UiEvent {
    object LoadMemoryData : MemoryEvent() // Event to trigger initial data loading
    object ToggleListExpanded : MemoryEvent() // Event to toggle the storage breakdown list
    // Add other events as needed (e.g., RefreshData)
}