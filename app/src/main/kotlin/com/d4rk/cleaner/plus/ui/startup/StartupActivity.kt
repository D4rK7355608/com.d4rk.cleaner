package com.d4rk.cleaner.plus.ui.startup
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.cleaner.plus.MainActivity
import com.d4rk.cleaner.plus.databinding.ActivityStartupBinding
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class StartupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        binding.buttonBrowsePrivacyPolicyAndTermsOfService.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy")))
        }
        binding.floatingButtonAgree.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }
}