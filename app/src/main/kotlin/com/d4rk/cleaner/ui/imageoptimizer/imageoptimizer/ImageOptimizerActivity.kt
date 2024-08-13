package com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.d4rk.cleaner.ui.settings.display.theme.style.AppTheme
import kotlinx.coroutines.launch

class ImageOptimizerActivity : AppCompatActivity() {
    private val viewModel: ImageOptimizerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val selectedImageUriString: String? = intent.getStringExtra("selectedImageUri")
        if (!selectedImageUriString.isNullOrEmpty()) {
            lifecycleScope.launch {
                viewModel.onImageSelected(Uri.parse(selectedImageUriString))
            }
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ImageOptimizerComposable(activity = this@ImageOptimizerActivity, viewModel)
                }
            }
        }
    }
}