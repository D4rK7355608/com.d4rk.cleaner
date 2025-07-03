package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerEvent
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsAppCleanerViewModel
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.DetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WhatsAppDetailsActivity : AppCompatActivity() {

    private val viewModel: WhatsAppCleanerViewModel by viewModel()
    private val detailsViewModel: DetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val type = intent.getStringExtra(EXTRA_TYPE) ?: ""
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailsScreenContent(
                        type = type,
                        viewModel = viewModel,
                        detailsViewModel = detailsViewModel
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_TYPE = "type"
    }
}

@Composable
private fun DetailsScreenContent(
    type: String,
    viewModel: WhatsAppCleanerViewModel,
    detailsViewModel: DetailsViewModel
) {
    val state = viewModel.uiState.collectAsState().value
    val summary = state.data?.mediaSummary ?: WhatsAppMediaSummary()
    val files = when (type) {
        "images" -> summary.images.files
        "videos" -> summary.videos.files
        "documents" -> summary.documents.files
        "audios" -> summary.audios.files
        "statuses" -> summary.statuses.files
        "voice_notes" -> summary.voiceNotes.files
        "video_notes" -> summary.videoNotes.files
        "gifs" -> summary.gifs.files
        "wallpapers" -> summary.wallpapers.files
        "stickers" -> summary.stickers.files
        "profile_photos" -> summary.profilePhotos.files
        else -> emptyList()
    }
    DetailsScreen(
        title = type,
        files = files,
        onDelete = { viewModel.onEvent(WhatsAppCleanerEvent.DeleteSelected(it)) },
        viewModel = detailsViewModel
    )
}

