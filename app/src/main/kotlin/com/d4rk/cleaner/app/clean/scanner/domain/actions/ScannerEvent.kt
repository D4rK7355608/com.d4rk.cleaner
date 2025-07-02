package com.d4rk.cleaner.app.clean.scanner.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import java.io.File

sealed class ScannerEvent : UiEvent {
    object LoadInitialData : ScannerEvent()
    object AnalyzeFiles : ScannerEvent()
    object RefreshData : ScannerEvent()
    data class DeleteFiles(val files : Set<File>) : ScannerEvent()
    data class MoveToTrash(val files : List<File>) : ScannerEvent()
    data class ToggleAnalyzeScreen(val visible : Boolean) : ScannerEvent()
    data class OnFileSelectionChange(val file : File , val isChecked : Boolean) : ScannerEvent()
    object ToggleSelectAllFiles : ScannerEvent()
    data class ToggleSelectFilesForCategory(val category : String) : ScannerEvent()
    data class ToggleSelectFilesForDate(val files: List<File>, val isChecked: Boolean) : ScannerEvent()
    object CleanFiles : ScannerEvent()
    object CleanWhatsAppFiles : ScannerEvent()
    object CleanCache : ScannerEvent()
    object MoveSelectedToTrash : ScannerEvent()
    data class SetDeleteForeverConfirmationDialogVisibility(val isVisible : Boolean) : ScannerEvent()
    data class SetMoveToTrashConfirmationDialogVisibility(val isVisible : Boolean) : ScannerEvent()
    data object DismissSnackbar : ScannerEvent()
}