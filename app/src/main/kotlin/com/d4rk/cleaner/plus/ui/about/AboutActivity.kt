package com.d4rk.cleaner.plus.ui.about
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.d4rk.cleaner.plus.BuildConfig
import com.d4rk.cleaner.plus.R
import com.d4rk.cleaner.plus.databinding.ActivityAboutBinding
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        val appVersion = resources.getString(R.string.app_version)
        val version = String.format(appVersion, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        binding.textViewAppVersion.text = version
        val simpleDateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val dateText = simpleDateFormat.format(Calendar.getInstance().time)
        val copyrightText = getString(R.string.copyright, dateText)
        binding.textViewCopyright.text = copyrightText
        binding.textViewAppVersion.setOnLongClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Label", binding.textViewAppVersion.text))
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }
            true
        }
        binding.imageViewAppIcon.setOnClickListener {
            openUrl("https://sites.google.com/view/d4rk7355608")
        }
        binding.chipGoogleDev.setOnClickListener {
            openUrl("https://developers.google.com/profile/u/D4rK7355608")
        }
        binding.chipYoutube.setOnClickListener {
            openUrl("https://www.youtube.com/c/D4rK7355608")
        }
        binding.chipGithub.setOnClickListener {
            openUrl("https://github.com/D4rK7355608/${BuildConfig.APPLICATION_ID}")
        }
        binding.chipTwitter.setOnClickListener {
            openUrl("https://twitter.com/D4rK7355608")
        }
        binding.chipXda.setOnClickListener {
            openUrl("https://forum.xda-developers.com/m/d4rk7355608.10095012")
        }
    }
    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}