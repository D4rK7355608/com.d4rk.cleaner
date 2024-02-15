package com.d4rk.cleaner.ui.imageoptimizer
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityImagePickerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class ImagePickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImagePickerBinding
    @Suppress("PrivatePropertyName")
    private val PICK_IMAGE_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        binding.buttonChooseImage.setOnClickListener {
            selectImage()
        }
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_custom_animations), true)) {
            setAnimations()
        }
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_amoled_mode), false)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                window.navigationBarColor = ContextCompat.getColor(this, android.R.color.black)
            }
        }
    }
    private fun setAnimations() {
        binding.root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_entry))
        binding.buttonChooseImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_swipe_up_right))
        binding.lottieAnimationView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_in))
    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        @Suppress("DEPRECATION")
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    val intent = Intent(this, ImageOptimizerActivity::class.java)
                    intent.putExtra("imageUri", selectedImageUri.toString())
                    startActivity(intent)
                }
            }
        }
    }
}