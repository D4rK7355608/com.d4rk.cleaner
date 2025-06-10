package com.d4rk.cleaner.app.clean.trash.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import java.io.File

sealed interface TrashEvent : UiEvent {
    data object LoadTrashItems : TrashEvent
    data class OnFileSelectionChange(val file : File , val isChecked : Boolean) : TrashEvent
    data object RestoreSelectedFiles : TrashEvent
    data object DeleteSelectedFilesPermanently : TrashEvent
}