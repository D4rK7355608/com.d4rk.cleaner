package com.d4rk.cleaner.plus
import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.children
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.plus.databinding.ActivityMainBinding
import com.d4rk.cleaner.plus.ui.about.AboutActivity
import com.d4rk.cleaner.plus.ui.imageoptimizer.ImageOptimizerActivity
import com.d4rk.cleaner.plus.ui.settings.SettingsActivity
import com.d4rk.cleaner.plus.ui.startup.StartupActivity
import com.d4rk.cleaner.plus.ui.whitelist.WhitelistActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.io.File
import java.text.DecimalFormat
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences
    private var currentPreferenceButtonPositions: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        WhitelistActivity.getWhiteList(preferences)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        val drawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0)
        drawerToggle.syncState()
        FastScrollerBuilder(binding.scrollViewFiles).useMd2Style().build()
        binding.buttonClean.setOnClickListener {
            clean()
        }
        binding.buttonAnalyze.setOnClickListener {
            analyze()
        }
        val themeKey = getString(R.string.key_theme)
        val themeValues = resources.getStringArray(R.array.preference_theme_values)
        val themeDefaultValue = getString(R.string.default_value_theme)
        val nightMode = when (preferences.getString(themeKey, themeDefaultValue)) {
            themeValues[0] -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            themeValues[1] -> AppCompatDelegate.MODE_NIGHT_NO
            themeValues[2] -> AppCompatDelegate.MODE_NIGHT_YES
            themeValues[3] -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        binding.navigationView.setNavigationItemSelectedListener { MenuItem: MenuItem ->
            when (MenuItem.itemId) {
                R.id.nav_drawer_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_drawer_whitelist -> startActivity(Intent(this, WhitelistActivity::class.java))
                R.id.nav_image_optimizer -> startActivity(Intent(this, ImageOptimizerActivity::class.java))
                R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            }
            binding.drawerLayout.closeDrawers()
            true
        }
        val lastUsedTimestamp = preferences.getLong("last_used", 0)
        val currentTimestamp = System.currentTimeMillis()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (currentTimestamp - lastUsedTimestamp > 3 * 24 * 60 * 60 * 1000) {
            val channelId = "app_usage_channel"
            val channel = NotificationChannel(channelId, "App Usage Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            val builder = NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.ic_notification).setContentTitle(getString(R.string.notification_last_time_used_title)).setContentText(getString(R.string.summary_notification_last_time_used)).setAutoCancel(true)
            notificationManager.notify(0, builder.build())
        }
        preferences.edit().putLong("last_used", currentTimestamp).apply()
    }
    override fun onStart() {
        super.onStart()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val appUpdateInfoTask = AppUpdateManagerFactory.create(this).appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                val updateChannelId = "update_channel"
                val updateChannel = NotificationChannel(updateChannelId, "Update Notifications", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(updateChannel)
                val updateBuilder = NotificationCompat.Builder(this, updateChannelId).setSmallIcon(R.drawable.ic_notification_update).setContentTitle(getString(R.string.notification_update_title)).setContentText(getString(R.string.summary_notification_update)).setAutoCancel(true).setContentIntent(PendingIntent.getActivity(this, 0, Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")), PendingIntent.FLAG_IMMUTABLE))
                notificationManager.notify(0, updateBuilder.build())
            }
        }
    }
    override fun onResume() {
        super.onResume()
        binding.navigationView.setCheckedItem(R.id.nav_home)
        if (preferences.getBoolean("value", true)) {
            preferences.edit().putBoolean("value", false).apply()
            startActivity(Intent(this, StartupActivity::class.java))
        }
        swapButtonsPositions(preferences.getBoolean(getString(R.string.key_swap_buttons), false))
        preferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == getString(R.string.key_swap_buttons)) {
                swapButtonsPositions(preferences.getBoolean(key, false))
            }
        }
        val preferenceFirebase = preferences.getBoolean(getString(R.string.key_firebase), true)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(preferenceFirebase)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(preferenceFirebase)
    }
    private fun swapButtonsPositions(preferenceButtonPositions: Boolean) {
        if (currentPreferenceButtonPositions == preferenceButtonPositions) return
        currentPreferenceButtonPositions = preferenceButtonPositions
            val parentLayout = binding.gridLayoutButtons
            val children = parentLayout.children.toList()
            parentLayout.removeAllViews()
            for (child in if (preferenceButtonPositions) children.asReversed() else children) {
                parentLayout.addView(child)
            }
    }
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.close)
            .setMessage(R.string.summary_close)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                super.onBackPressed()
                moveTaskToBack(true)
            }
            .setNegativeButton(android.R.string.no, null)
            .create()
            .show()
    }
    private fun analyze() {
        requestStoragePermissions()
        if (!FileScanner.isRunning) {
            Thread {
                scan(false)
            }.start()
        }
    }
    private fun arrangeViews(isDelete: Boolean) {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isDelete) {
                binding.frameLayoutMain.visibility = View.VISIBLE
                binding.scrollViewFiles.visibility = View.GONE
            } else {
                binding.frameLayoutMain.visibility = View.GONE
                binding.scrollViewFiles.visibility = View.VISIBLE
            }
        }
    }
    private fun clean() {
        requestStoragePermissions()
        if (!FileScanner.isRunning) {
            val oneClickCleanEnabled = preferences.getBoolean(getString(R.string.key_one_click_clean), false)
            if (oneClickCleanEnabled) {
                Thread {
                    scan(true)
                }.start()
            } else {
                val mDialog = MaterialDialog.Builder(this)
                    .setTitle(getString(R.string.clean_confirm_title))
                    .setAnimation(R.raw.delete)
                    .setMessage(getString(R.string.summary_dialog_button_clean))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.clean)) { dialogInterface, _ ->
                        Thread {
                            scan(true)
                        }.start()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(android.R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                mDialog.animationView.scaleType = ImageView.ScaleType.FIT_CENTER
                mDialog.show()
            }
        }
    }
    private fun clearClipboard() {
        try {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                clipboardManager.clearPrimaryClip()
            } else {
                val clipData = ClipData.newPlainText("", "")
                clipboardManager.setPrimaryClip(clipData)
            }
        } catch (e: NullPointerException) {
            runOnUiThread {
                Toast.makeText(this, R.string.clipboard_clean_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun scan(delete: Boolean) {
        Looper.prepare()
        runOnUiThread {
            binding.buttonClean.isEnabled = !FileScanner.isRunning
            binding.buttonAnalyze.isEnabled = !FileScanner.isRunning
        }
        reset()
        if (preferences.getBoolean(getString(R.string.key_clipboard), false)) clearClipboard()
        runOnUiThread {
            arrangeViews(delete)
            binding.textViewStatus.text = getString(R.string.status_running)
        }
        val path = Environment.getExternalStorageDirectory()
        val fileScanner = FileScanner(path, this)
            .setEmptyDir(preferences.getBoolean(getString(R.string.key_filter_empty), false))
            .setAutoWhite(preferences.getBoolean(getString(R.string.key_auto_whitelist), true))
            .setInvalid(preferences.getBoolean(getString(R.string.key_invalid_media_cleaner), false))
            .setDelete(delete)
            .setCorpse(preferences.getBoolean(getString(R.string.key_filter_corpse), false))
            .setGUI(binding)
            .setContext(this)
            .setUpFilters(
                preferences.getBoolean(getString(R.string.key_filter_generic), true),
                preferences.getBoolean(getString(R.string.key_filter_aggressive), false),
                preferences.getBoolean(getString(R.string.key_filter_apk), false),
                preferences.getBoolean(getString(R.string.key_filter_archive), false))
        if (path.listFiles() == null) {
            val textView = printTextView(getString(R.string.clipboard_clean_failed), Color.RED)
            runOnUiThread {
                binding.linearLayoutFiles.addView(textView)
            }
        }
        val kilobytesTotal = fileScanner.startScan()
        runOnUiThread {
            if (delete) binding.textViewStatus.text = getString(R.string.freed) + " " + convertSize(kilobytesTotal) else binding.textViewStatus.text = getString(R.string.found) + " " + convertSize(kilobytesTotal)
            binding.progressBarScan.progress = binding.progressBarScan.max
            binding.textViewPercentage.text = "100%"
        }
        binding.scrollViewFiles.post { binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN) }
        runOnUiThread {
            binding.buttonClean.isEnabled = !FileScanner.isRunning
            binding.buttonAnalyze.isEnabled = !FileScanner.isRunning
        }
        Looper.loop()
    }
    private fun printTextView(text: String, color: Int): TextView {
        val textView = TextView(this)
        textView.setTextColor(color)
        textView.text = text
        textView.setPadding(3, 3, 3, 3)
        return textView
    }
    fun displayDeletion(file: File): TextView {
        val textView = printTextView(file.absolutePath, resources.getColor(R.color.colorPrimary, resources.newTheme()))
        runOnUiThread {
            binding.linearLayoutFiles.addView(textView)
        }
        binding.scrollViewFiles.post {
            binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN)
        }
        return textView
    }
    fun displayText(text: String) {
        val textColor = resources.getColor(R.color.colorSecondary, theme)
        val textView = printTextView(text, textColor)
        runOnUiThread {
            binding.linearLayoutFiles.addView(textView)
        }
        binding.scrollViewFiles.post {
            binding.scrollViewFiles.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
    private fun reset() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        runOnUiThread {
            binding.linearLayoutFiles.removeAllViews()
            binding.progressBarScan.progress = 0
            binding.progressBarScan.max = 1
        }
    }
    private val isAccessGranted: Boolean
        get() = try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            val mode: Int = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    private fun requestStoragePermissions() {
        val requiredPermissions = mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requiredPermissions += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO + Manifest.permission.READ_MEDIA_IMAGES + Manifest.permission.READ_MEDIA_VIDEO
            } else {
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            }
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            if (!isAccessGranted) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
        ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), 1)
    }
    companion object {
        @JvmField
        var preferences: SharedPreferences? = null
        @JvmStatic
        fun convertSize(length: Long): String {
            val format = DecimalFormat("#.##")
            val mib = (1024 * 1024).toLong()
            val kib: Long = 1024
            return when {
                length > mib -> "${format.format(length / mib)} MB"
                length > kib -> "${format.format(length / kib)} KB"
                else -> "${format.format(length)} B"
            }
        }
    }
}