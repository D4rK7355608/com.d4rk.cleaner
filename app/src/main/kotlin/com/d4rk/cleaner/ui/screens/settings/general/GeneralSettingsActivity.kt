package com.d4rk.cleaner.ui.screens.settings.general

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.ui.screens.settings.display.theme.style.AppTheme

class GeneralSettingsActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_CONTENT = "extra_content"

        fun start(context: Context , title: String , content: SettingsContent) {
            val intent = Intent(context, GeneralSettingsActivity::class.java).apply {
                putExtra(EXTRA_TITLE , title)
                putExtra(EXTRA_CONTENT , content.name)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(com.d4rk.android.libs.apptoolkit.R.string.settings)
        val content = intent.getStringExtra(EXTRA_CONTENT)?.let { SettingsContent.valueOf(it) }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize() ,
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeneralSettingsScreen(
                        title = title,
                        content = content,
                        onBackClicked = { finish() }
                    )
                }
            }
        }
    }
}