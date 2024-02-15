package com.d4rk.cleaner.ui.imageoptimizer
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.d4rk.cleaner.R
import com.d4rk.cleaner.adapters.ImageOptimizationPagerAdapter
import com.d4rk.cleaner.databinding.ActivityImageOptimizerBinding
import com.d4rk.cleaner.ui.viewmodel.ImageOptimizerViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
class ImageOptimizerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageOptimizerBinding
    private var actualImageFile: File? = null
    private lateinit var viewModel: ImageOptimizerViewModel
    private var compressedImageFile: File? = null
    private var isCompressing = false
    private val optimizedPicturesDirectory = File(Environment.getExternalStorageDirectory(), "Pictures/Optimized Pictures").apply {
        if (!exists()) {
            mkdirs()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageOptimizerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_amoled_mode), false)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                binding.tabLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                window.navigationBarColor = ContextCompat.getColor(this, android.R.color.black)
            }
        }
        val adapter = ImageOptimizationPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.progressBar.alpha = 0f
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        val imageUri = Uri.parse(intent.getStringExtra("imageUri"))
        Glide.with(this).load(imageUri).into(binding.imageView)
        actualImageFile = getPath(this@ImageOptimizerActivity, imageUri)?.let { File(it) }
        viewModel = ViewModelProvider(this)[ImageOptimizerViewModel::class.java]
        viewModel.compressionLevelLiveData.observe(this) { compressionLevel ->
            compressImageQuickCompress(actualImageFile, compressionLevel)
        }
        binding.buttonCompressImage.setOnClickListener {
            when (binding.viewPager.currentItem) {
                0 -> {
                    val quickCompressFragment = supportFragmentManager.findFragmentByTag("f0") as? QuickCompressFragment
                    val compressionLevel = quickCompressFragment?.getCurrentCompressionLevel() ?: 50
                    compressImageQuickCompress(actualImageFile, compressionLevel)
                }
                1 -> {
                    val fileSizeFragment = supportFragmentManager.findFragmentByTag("f1") as? FileSizeFragment
                    val targetSizeKB = fileSizeFragment?.getCurrentFileSizeKB() ?: -1
                    if (targetSizeKB > 0) {
                        compressImageByFileSize(actualImageFile, targetSizeKB)
                    } else {
                        val snackbar = Snackbar.make(binding.root, getString(R.string.snack_validate_file), Snackbar.LENGTH_LONG)
                        snackbar.setAction(android.R.string.ok) {
                            snackbar.dismiss()
                        }
                        snackbar.show()
                    }
                }
                2 -> {
                    val manualModeFragment = supportFragmentManager.findFragmentByTag("f2") as? ManualModeFragment
                    val (width, height, quality) = manualModeFragment?.getCurrentCompressionSettings() ?: Triple(0, 0, 0)
                    compressImageManualMode(width, height, quality)
                }
            }
        }
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.quick_compress)
                1 -> tab.text = getString(R.string.file_size)
                2 -> tab.text = getString(R.string.manual)
            }
        }.attach()
    }
    private fun compressImageManualMode(width: Int, height: Int, quality: Int) {
        if (compressedImageFile != null || isCompressing) {
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val compressedImageFile = withContext(Dispatchers.IO) {
                if (actualImageFile != null) {
                    Compressor.compress(this@ImageOptimizerActivity, actualImageFile!!) {
                        resolution(width, height)
                        quality(quality)
                    }
                } else {
                    null
                }
            }
            withContext(Dispatchers.Main) {
                updateImageView(compressedImageFile)
                binding.progressBar.visibility = View.GONE
                compressedImageFile?.let { saveImage(it) }
                isCompressing = false
            }
        }
    }
    private fun compressImageByFileSize(imageFile: File?, targetSizeKB: Int) {
        if (compressedImageFile != null || isCompressing) {
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val compressedImageFile = withContext(Dispatchers.IO) {
                if (imageFile != null) {
                    Compressor.compress(this@ImageOptimizerActivity, imageFile) {
                        format(Bitmap.CompressFormat.JPEG)
                        size((targetSizeKB * 1024).toLong())
                    }
                } else {
                    null
                }
            }
            withContext(Dispatchers.Main) {
                updateImageView(compressedImageFile)
                binding.progressBar.visibility = View.GONE
                compressedImageFile?.let { saveImage(it) }
                isCompressing = false
            }
        }
    }
    private fun compressImageQuickCompress(actualImageFile: File?, compressionLevel: Int) {
        if (compressedImageFile != null || isCompressing) {
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val compressedImageFile = withContext(Dispatchers.IO) {
                if (actualImageFile != null) {
                    Compressor.compress(this@ImageOptimizerActivity, actualImageFile) {
                        format(Bitmap.CompressFormat.JPEG)
                        quality(compressionLevel)
                    }
                } else {
                    null
                }
            }
            withContext(Dispatchers.Main) {
                updateImageView(compressedImageFile)
                binding.progressBar.visibility = View.GONE
                compressedImageFile?.let { saveImage(it) }
                isCompressing = false
            }
        }
    }
    private fun updateImageView(compressedImageFile: File?) {
        compressedImageFile?.let {
            val uri = Uri.fromFile(it)
            Glide.with(this).load(uri).into(binding.imageView)
        }
    }
    private fun saveImage(file: File) {
        lifecycleScope.launch(Dispatchers.Main) {
            optimizedPicturesDirectory.mkdirs()
            val savedFile = withContext(Dispatchers.IO) {
                val newFile = File(optimizedPicturesDirectory, "${System.currentTimeMillis()}.jpg")
                file.copyTo(newFile, overwrite = true)
                newFile
            }
            MediaScannerConnection.scanFile(applicationContext, arrayOf(savedFile.path), arrayOf("image/jpeg"), null)
            val snackbar = Snackbar.make(binding.root, getString(R.string.image_saved) + savedFile.path, Snackbar.LENGTH_LONG)
            snackbar.setAction(android.R.string.ok) {
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }
    private fun getPath(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                it.moveToFirst()
                val path = it.getString(columnIndex)
                it.close()
                return path
            }
        }
        return null
    }
}