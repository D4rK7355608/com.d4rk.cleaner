package com.d4rk.cleaner.ui.startup
import android.Manifest
import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.MainActivity
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityStartupBinding
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class StartupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartupBinding
    private lateinit var consentInformation: ConsentInformation
    private lateinit var consentForm: ConsentForm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params, {
            if (consentInformation.isConsentFormAvailable) {
                loadForm()
            }
        }, {
        })
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        binding.buttonBrowsePrivacyPolicyAndTermsOfService.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy")))
        }
        binding.floatingButtonAgree.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        requestPermissions()
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_custom_animations), true)) {
            setAnimations()
        }
    }
    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            { consentForm ->
                this.consentForm = consentForm
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(this) {
                        loadForm()
                    }
                }
            },
            {
            }
        )
    }
    @Suppress("DEPRECATION")
    private val isAccessGranted: Boolean
        get() = try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            @Suppress("DEPRECATION") val mode: Int = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    private fun requestPermissions() {
        val requiredPermissions = mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO), 1)
        }
        ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), 1)
    }
    private fun setAnimations() {
        binding.root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_in))
        binding.buttonBrowsePrivacyPolicyAndTermsOfService.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_entry))
        binding.imageViewWelcome.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_entry))
        binding.imageViewAbout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_entry))
        binding.floatingButtonAgree.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_entry))
    }
}