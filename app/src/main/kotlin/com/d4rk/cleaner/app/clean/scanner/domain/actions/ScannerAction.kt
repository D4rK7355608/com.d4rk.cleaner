package com.d4rk.cleaner.app.clean.scanner.domain.actions

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed interface ScannerAction : ActionEvent {
    object RequestBackupUri : ScannerAction
}
