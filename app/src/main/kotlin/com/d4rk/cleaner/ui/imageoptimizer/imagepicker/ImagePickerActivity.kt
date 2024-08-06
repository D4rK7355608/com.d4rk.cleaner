package com.d4rk.cleaner.ui.imageoptimizer.imagepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.settings.display.theme.style.AppTheme

class ImagePickerActivity : AppCompatActivity() {
    private val viewModel : ImagePickerViewModel by viewModels()
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background
                ) {
                    ImagePickerComposable(activity = this@ImagePickerActivity , viewModel)
                }
            }
        }
    }

    private val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val selectedImageUri : Uri? = result.data?.data
                    viewModel.setSelectedImageUri(selectedImageUri)
                }
            }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val chooser : Intent = Intent.createChooser(intent , getString(R.string.select_image))
        launcher.launch(chooser)
    }
}