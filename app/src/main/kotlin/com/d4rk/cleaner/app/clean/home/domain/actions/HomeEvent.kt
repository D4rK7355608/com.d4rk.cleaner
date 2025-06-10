package com.d4rk.cleaner.app.clean.home.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import java.io.File

sealed class HomeEvent : UiEvent {
    object LoadInitialData : HomeEvent()
    object AnalyzeFiles : HomeEvent()
    object RefreshData : HomeEvent()
    data class DeleteFiles(val files : Set<File>) : HomeEvent()
    data class MoveToTrash(val files : List<File>) : HomeEvent()
    data class ToggleAnalyzeScreen(val visible : Boolean) : HomeEvent()
    data class OnFileSelectionChange(val file : File , val isChecked : Boolean) : HomeEvent()
    object ToggleSelectAllFiles : HomeEvent()
    data class ToggleSelectFilesForCategory(val category : String) : HomeEvent()
    object CleanFiles : HomeEvent()
    object MoveSelectedToTrash : HomeEvent()
    data class SetDeleteForeverConfirmationDialogVisibility(val isVisible : Boolean) : HomeEvent()
    data class SetMoveToTrashConfirmationDialogVisibility(val isVisible : Boolean) : HomeEvent()
}