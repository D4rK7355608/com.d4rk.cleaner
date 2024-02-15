package com.d4rk.cleaner.ui.settings.privacy
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityPreferencesBinding
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class PrivacySettingsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        binding.root.isVerticalScrollBarEnabled = false
        binding.toolbar.setTitle(R.string.privacy_and_security)
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
            setPreferencesFromResource(R.xml.preferences_settings_privacy, rootKey)

        }
    }
}