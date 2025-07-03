package com.d4rk.cleaner.app.clean.whatsapp.summary.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.WhatsAppDetailsActivity
import com.d4rk.cleaner.app.clean.whatsapp.permission.ui.WhatsAppPermissionActivity
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper

class WhatsAppCleanerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionsHelper.hasStoragePermissions(this)) {
            startActivity(Intent(this, WhatsAppPermissionActivity::class.java))
            finish()
            return
        }
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WhatsappScreenContent(activity = this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WhatsappScreenContent(activity: Activity) {
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.whatsapp_cleaner),
        onBackClicked = {
            activity.finish()
        },
        scrollBehavior = scrollBehavior,
    ) { paddingValues ->
        WhatsAppCleanerScreen(
            paddingValues = paddingValues,
            onOpenDetails = { type ->
                val intent = Intent(activity, WhatsAppDetailsActivity::class.java)
                intent.putExtra(WhatsAppDetailsActivity.EXTRA_TYPE, type)
                activity.startActivity(intent)
            }
        )
    }
}
