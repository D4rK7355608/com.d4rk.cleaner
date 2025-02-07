package com.d4rk.cleaner.ui.screens.analyze.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.core.ui.components.dialogs.ConfirmationAlertDialog
import com.d4rk.cleaner.ui.screens.home.HomeViewModel

@Composable
fun DeleteOrTrashConfirmation(data : com.d4rk.cleaner.core.data.model.ui.screens.UiHomeModel , viewModel : HomeViewModel) {
    val isDeleteDialog : Boolean = data.analyzeState.isDeleteForeverConfirmationDialogVisible

    val titleRes : Int = if (isDeleteDialog) R.string.delete_forever_title else R.string.move_to_trash_title
    val messageRes : Int = if (isDeleteDialog) R.string.delete_forever_message else R.string.move_to_trash_message

    ConfirmationAlertDialog(confirmationTitle = stringResource(id = titleRes) ,
                            confirmationMessage = stringResource(id = messageRes) ,
                            confirmationConfirmButtonText = stringResource(id = android.R.string.ok) ,
                            confirmationDismissButtonText = stringResource(id = android.R.string.cancel) ,
                            onConfirm = {
                                if (isDeleteDialog) {
                                    viewModel.clean()
                                    viewModel.setDeleteForeverConfirmationDialogVisibility(false)
                                }
                                else {
                                    viewModel.moveToTrash()
                                    viewModel.setMoveToTrashConfirmationDialogVisibility(false)
                                }
                            } ,
                            onDismiss = {
                                if (isDeleteDialog) {
                                    viewModel.setDeleteForeverConfirmationDialogVisibility(false)
                                }
                                else {
                                    viewModel.setMoveToTrashConfirmationDialogVisibility(false)
                                }
                            })
}