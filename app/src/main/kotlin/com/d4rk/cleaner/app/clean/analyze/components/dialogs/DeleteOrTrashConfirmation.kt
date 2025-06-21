package com.d4rk.cleaner.app.clean.analyze.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.home.domain.actions.HomeEvent
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.UiHomeModel
import com.d4rk.cleaner.app.clean.home.ui.HomeViewModel

@Composable
fun DeleteOrTrashConfirmation(data: UiHomeModel, viewModel: HomeViewModel) {
    val isDeleteDialog = data.analyzeState.isDeleteForeverConfirmationDialogVisible

    val titleRes =
        if (isDeleteDialog) R.string.delete_forever_title else R.string.move_to_trash_title
    val messageRes =
        if (isDeleteDialog) R.string.delete_forever_message else R.string.move_to_trash_message

    BasicAlertDialog(
        title = stringResource(id = titleRes), content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.delete_anim))
                LottieAnimation(
                    modifier = Modifier.fillMaxWidth(),
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
                Text(text = stringResource(id = messageRes))
            }
        }, onConfirm = {
            if (isDeleteDialog) {
                viewModel.onEvent(HomeEvent.CleanFiles)
                viewModel.onEvent(HomeEvent.SetDeleteForeverConfirmationDialogVisibility(false))
            } else {
                viewModel.onEvent(HomeEvent.MoveSelectedToTrash)
                viewModel.onEvent(HomeEvent.SetMoveToTrashConfirmationDialogVisibility(false))
            }
        }, onDismiss = {
            if (isDeleteDialog) {
                viewModel.onEvent(HomeEvent.SetDeleteForeverConfirmationDialogVisibility(false))
            } else {
                viewModel.onEvent(HomeEvent.SetMoveToTrashConfirmationDialogVisibility(false))
            }
        })
}