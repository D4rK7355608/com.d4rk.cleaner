package com.d4rk.cleaner.ui.settings.cleaning
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityPreferencesBinding
import com.d4rk.cleaner.receivers.CleanReceiver.Companion.cancelAlarm
import com.d4rk.cleaner.receivers.CleanReceiver.Companion.scheduleAlarm
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class CleaningSettingsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        binding.root.isVerticalScrollBarEnabled = false
        binding.toolbar.setTitle(R.string.cleaning)
        supportFragmentManager.beginTransaction().replace(R.id.preferences, SettingsFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_amoled_mode), false)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                window.navigationBarColor = ContextCompat.getColor(this, android.R.color.black)
            }
        }
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
}