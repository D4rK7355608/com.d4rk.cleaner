package com.d4rk.cleaner.plus.ui.permissions
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.d4rk.cleaner.plus.R
import com.d4rk.cleaner.plus.databinding.ActivityPermissionsBinding
class PermissionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPermissionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout_permissions, SettingsFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_permissions, rootKey)
        }
    }
}