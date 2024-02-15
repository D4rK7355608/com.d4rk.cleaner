package com.d4rk.cleaner.ui.settings.display
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityPreferencesBinding
import com.d4rk.cleaner.dialogs.RequireRestartDialog
import com.google.android.material.snackbar.Snackbar
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class DisplaySettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding : ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        binding.root.isVerticalScrollBarEnabled = false
        binding.toolbar.setTitle(R.string.display)
        supportFragmentManager.beginTransaction().replace(R.id.preferences, SettingsFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_amoled_mode), false)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                window.navigationBarColor = ContextCompat.getColor(this, android.R.color.black)
            }
        }
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, rootKey: String?) {
        val themeValues = resources.getStringArray(R.array.preference_theme_values)
        when (rootKey) {
            getString(R.string.key_theme) -> sharedPreferences?.let { pref ->
                when (pref.getString(getString(R.string.key_theme), themeValues[0])) {
                    themeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    themeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    themeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    themeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
        val languageCode = sharedPreferences?.getString(getString(R.string.key_language), getString(R.string.default_value_language))
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_settings_display, rootKey)
            val amoledMode = findPreference<SwitchPreferenceCompat>(getString(R.string.key_amoled_mode))
            amoledMode?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                        resetTheme()
                    } else {
                        Snackbar.make(requireView(), getString(R.string.snack_amoled_mode), Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                        resetTheme()
                    }
                }
                true
            }
            val labelVisibilityMode = findPreference<ListPreference>(getString(R.string.key_bottom_navigation_bar_labels))
            labelVisibilityMode?.setOnPreferenceChangeListener { _, _ ->
                val restartDialog = RequireRestartDialog()
                restartDialog.show(childFragmentManager, RequireRestartDialog::class.java.name)
                true
            }
            val defaultTab = findPreference<ListPreference>(getString(R.string.key_default_tab))
            defaultTab?.setOnPreferenceChangeListener { _, _ ->
                val restartDialog = RequireRestartDialog()
                restartDialog.show(childFragmentManager, RequireRestartDialog::class.java.name)
                true
            }
            val swapButtons = findPreference<SwitchPreferenceCompat>(getString(R.string.key_swap_buttons))
            swapButtons?.setOnPreferenceChangeListener { _, _ ->
                val restartDialog = RequireRestartDialog()
                restartDialog.show(childFragmentManager, RequireRestartDialog::class.java.name)
                true
            }
        }
        private fun resetTheme() {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}