package com.d4rk.cleaner.ui.settings.cleaning

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.ui.settings.display.theme.style.AppTheme

class CleaningSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    CleaningSettingsComposable(this@CleaningSettingsActivity)
                }
            }
        }
    }
}

/*: AppCompatActivity() {
    private lateinit var binding : ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_settings_cleaning, rootKey)
            val filterAggressivePreference = findPreference<SwitchPreferenceCompat>(getString(R.string.key_filter_aggressive))!!
            filterAggressivePreference.setOnPreferenceChangeListener { _, _ ->
                val filtersFiles = resources.getStringArray(R.array.aggressive_filter_folders)
                if (!filterAggressivePreference.isChecked) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.warning)
                        .setMessage(getString(R.string.adds_the_following) + " " + filtersFiles.contentToString())
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show()
                }
                true
            }
            val dailyCleaner = findPreference<SwitchPreferenceCompat>(getString(R.string.key_daily_clean))
            dailyCleaner?.setOnPreferenceChangeListener { preference, _ ->
                val checked = (preference as SwitchPreferenceCompat).isChecked
                if (!checked) {
                    scheduleAlarm(requireContext().applicationContext)
                } else {
                    cancelAlarm(requireContext().applicationContext)
                }
                true
            }
        }
    }
}*/