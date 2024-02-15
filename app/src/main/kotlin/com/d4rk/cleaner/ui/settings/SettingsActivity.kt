package com.d4rk.cleaner.ui.settings
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityPreferencesBinding
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        binding.root.isVerticalScrollBarEnabled = false
        binding.toolbar.setTitle(R.string.settings)
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
            setPreferencesFromResource(R.xml.preferences_settings, rootKey)
            val notificationsSettings = findPreference<Preference>(getString(R.string.key_notifications_settings))
            notificationsSettings?.setOnPreferenceClickListener {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                startActivity(intent)
                true
            }
        }
    }
}