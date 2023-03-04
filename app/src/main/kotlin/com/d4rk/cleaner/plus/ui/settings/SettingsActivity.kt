package com.d4rk.cleaner.plus.ui.settings
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.d4rk.cleaner.plus.BuildConfig
import com.d4rk.cleaner.plus.receivers.CleanReceiver.Companion.cancelAlarm
import com.d4rk.cleaner.plus.receivers.CleanReceiver.Companion.scheduleAlarm
import com.d4rk.cleaner.plus.R
import com.d4rk.cleaner.plus.databinding.ActivitySettingsBinding
import com.d4rk.cleaner.plus.dialogs.RequireRestartDialog
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
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
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_settings, rootKey)
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
            val swapButtons = findPreference<SwitchPreferenceCompat>(getString(R.string.key_swap_buttons))
            swapButtons?.setOnPreferenceChangeListener { _, _ ->
                val restartDialog = RequireRestartDialog()
                restartDialog.show(childFragmentManager, RequireRestartDialog::class.java.name)
                true
            }
            val moreApps = findPreference<Preference>(getString(R.string.key_more_apps))
            moreApps?.setOnPreferenceClickListener {
                val view: View = layoutInflater.inflate(R.layout.fragment_dialog, null)
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.more_apps)
                    .setIcon(R.drawable.ic_shop)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setView(view)
                    .create()
                    .show()
                val textViewAndroidStudioTutorials: MaterialTextView = view.findViewById(R.id.text_view_android_studio_tutorials)
                val textViewMusicSleepTimer: MaterialTextView = view.findViewById(R.id.text_view_music_sleep_timer)
                val textViewEnglishWithLidia: MaterialTextView = view.findViewById(R.id.text_view_english_with_lidia)
                val textViewQRCodeScanner: MaterialTextView = view.findViewById(R.id.text_view_qr_code_scanner)
                val textViewLowBrightness: MaterialTextView = view.findViewById(R.id.text_view_low_brightness)
                val urls = mapOf(textViewAndroidStudioTutorials to "https://play.google.com/store/apps/details?id=com.d4rk.androidtutorials", textViewMusicSleepTimer to "https://play.google.com/store/apps/details?id=com.d4rk.musicsleeptimer.plus", textViewEnglishWithLidia to "https://play.google.com/store/apps/details?id=com.d4rk.englishwithlidia.plus", textViewQRCodeScanner to "https://play.google.com/store/apps/details?id=com.d4rk.qrcodescanner.plus", textViewLowBrightness to "https://play.google.com/store/apps/details?id=com.d4rk.lowbrightness")
                urls.forEach { (view, url) -> view.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                }
                true
            }
            val changelogPreference = findPreference<Preference>(getString(R.string.key_changelog))
            changelogPreference?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.changelog_title, BuildConfig.VERSION_NAME))
                    .setIcon(R.drawable.ic_changelog)
                    .setMessage(R.string.changes)
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
            val sharePreference = findPreference<Preference>(getString(R.string.key_share))
            sharePreference?.setOnPreferenceClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                    putExtra(Intent.EXTRA_SUBJECT, R.string.share_subject)
                }
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)))
                true
            }
            val ossPreference = findPreference<Preference>(getString(R.string.key_open_source_licenses))
            ossPreference?.setOnPreferenceClickListener {
                startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
                true
            }
            val deviceInfoPreference = findPreference<Preference>(getString(R.string.key_device_info))
            val version = String.format(
                resources.getString(R.string.app_build),
                "${resources.getString(R.string.manufacturer)} ${Build.MANUFACTURER}",
                "${resources.getString(R.string.device_model)} ${Build.MODEL}",
                "${resources.getString(R.string.android_version)} ${Build.VERSION.RELEASE}",
                "${resources.getString(R.string.api_level)} ${Build.VERSION.SDK_INT}",
                "${resources.getString(R.string.arch)} ${Build.SUPPORTED_ABIS.joinToString()}"
            )
            deviceInfoPreference?.summary = version
            deviceInfoPreference?.setOnPreferenceClickListener {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", version)
                clipboard.setPrimaryClip(clip)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }
}