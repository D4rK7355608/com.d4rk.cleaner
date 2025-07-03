package com.d4rk.cleaner.app.clean.whatsappcleaner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.WhatsAppMediaSummary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WhatsAppCleanerScreen(viewModel: WhatsAppCleanerViewModel = koinViewModel()) {
    val state: UiStateScreen<UiWhatsAppCleanerModel> by viewModel.uiState.collectAsState()
    ScreenStateHandler(state = state) { data ->
        Content(summary = data.mediaSummary, onClean = { viewModel.onEvent(WhatsAppCleanerEvent.CleanAll) })
    }
}

@Composable
private fun Content(summary: WhatsAppMediaSummary, onClean: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.images) + ": ${summary.images.size}")
        Text(text = stringResource(id = R.string.videos) + ": ${summary.videos.size}")
        Text(text = stringResource(id = R.string.documents) + ": ${summary.documents.size}")
        Button(onClick = onClean) {
            Text(text = stringResource(id = R.string.clean_whatsapp))
        }
    }
}
