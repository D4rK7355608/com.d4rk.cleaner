package com.d4rk.cleaner.app.clean.largefiles.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import java.io.File

sealed interface LargeFilesEvent : UiEvent {
    data object LoadLargeFiles : LargeFilesEvent
    data class OnFileSelectionChange(val file: File, val isChecked: Boolean) : LargeFilesEvent
    data object DeleteSelectedFiles : LargeFilesEvent
}
