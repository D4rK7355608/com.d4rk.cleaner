package com.d4rk.cleaner.ui.imageoptimizer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ActivityImagePickerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class ImagePickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImagePickerBinding

    @Suppress("PrivatePropertyName")
    private val PICK_IMAGE_REQUEST = 1

    @Suppress("DEPRECATION")
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAnimations()
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        binding.buttonChooseImage.setOnClickListener {
            selectImage()
        }
    }

    private fun setAnimations() {
        binding.buttonChooseImage.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.anim_swipe_up_right
            )
        )
        handler.postDelayed({
            binding.buttonChooseImage.shrink()
        }, 5000)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.select_image))
        @Suppress("DEPRECATION")
        startActivityForResult(chooser, PICK_IMAGE_REQUEST)
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