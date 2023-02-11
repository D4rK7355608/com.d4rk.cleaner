package com.d4rk.cleaner.plus.ui.faq
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.d4rk.cleaner.plus.R
import com.d4rk.cleaner.plus.databinding.ActivityFaqBinding
class FaqActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout_faq, SettingsFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_faq, rootKey)
        }
    }
}