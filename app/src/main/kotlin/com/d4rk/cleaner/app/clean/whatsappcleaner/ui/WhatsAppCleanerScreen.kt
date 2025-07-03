package com.d4rk.cleaner.app.clean.whatsappcleaner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.navigation.WhatsAppRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WhatsAppCleanerScreen(
    navController: NavHostController,
    viewModel: WhatsAppCleanerViewModel = koinViewModel(),
    paddingValues: PaddingValues
) {
    val state: UiStateScreen<UiWhatsAppCleanerModel> by viewModel.uiState.collectAsState()
    ScreenStateHandler(
        screenState = state,
        onLoading = { LoadingScreen() },
        onEmpty = { NoDataScreen() },
        onSuccess = { data ->
            Content(
                paddingValues = paddingValues,
                summary = data.mediaSummary,
                onClean = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) },
                onOpenDetails = { type ->
                    navController.navigate(WhatsAppRoute.Details.create(type))
                }
            )
        }
    )
}

@Composable
private fun Content(
    summary: com.d4rk.cleaner.app.clean.whatsappcleaner.domain.model.WhatsAppMediaSummary,
    onClean: () -> Unit,
    paddingValues: PaddingValues,
    onOpenDetails: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues = paddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { onOpenDetails("images") }) {
            Text(text = stringResource(id = R.string.images) + ": ${summary.images.size}")
        }
        Button(onClick = { onOpenDetails("videos") }) {
            Text(text = stringResource(id = R.string.videos) + ": ${summary.videos.size}")
        }
        Button(onClick = { onOpenDetails("documents") }) {
            Text(text = stringResource(id = R.string.documents) + ": ${summary.documents.size}")
        }
        Button(onClick = onClean) {
            Text(text = stringResource(id = R.string.clean_whatsapp))
        }
    }
}
