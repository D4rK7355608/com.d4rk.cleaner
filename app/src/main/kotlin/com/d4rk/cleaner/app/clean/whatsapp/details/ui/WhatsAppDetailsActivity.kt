package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerEvent
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsappCleanerSummaryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WhatsAppDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "type"
    }

    private val viewModel: WhatsappCleanerSummaryViewModel by viewModel()
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
                    DetailsScreen(
                        viewModel = viewModel,
                        detailsViewModel = detailsViewModel,
                        title = type,
                        onDelete = { viewModel.onEvent(WhatsAppCleanerEvent.DeleteSelected(it)) },
                        activity = this
                    )
                }
            }
        }
    }
}