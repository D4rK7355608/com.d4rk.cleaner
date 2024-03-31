package com.d4rk.cleaner.ui.settings.privacy.permissions
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityPreferencesBinding
import com.d4rk.cleaner.ui.settings.display.theme.AppTheme
import com.d4rk.cleaner.ui.settings.privacy.PrivacySettingsComposable
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class PermissionsSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                    PermissionsSettingsComposable(this@PermissionsSettingsActivity)
                }
            }
        }
    }
}