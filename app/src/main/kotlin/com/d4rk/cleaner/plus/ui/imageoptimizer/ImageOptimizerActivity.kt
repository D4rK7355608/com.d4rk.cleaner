package com.d4rk.cleaner.plus.ui.imageoptimizer
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.cleaner.plus.databinding.ActivityImageOptimizerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import java.io.File
@Suppress("DEPRECATION")
@DelicateCoroutinesApi
class ImageOptimizerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageOptimizerBinding
    private var imageUri: Uri? = null
    private val optimizedPicturesDirectory = File(Environment.getExternalStorageDirectory(), "Pictures/Optimized Pictures")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageOptimizerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        binding.buttonChooseImage.setOnClickListener {
            chooseImage()
        }
        binding.buttonOptimizeImage.setOnClickListener {
            imageUri?.let {
                optimizeImage(it)
            }
        }
    }
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE, null)
    }
    private fun optimizeImage(imageUri: Uri) {
        GlobalScope.launch(Dispatchers.Main) {
            binding.buttonOptimizeImage.isEnabled = false
            val file = withContext(Dispatchers.IO) {
                val filePath = getPath(this@ImageOptimizerActivity, imageUri)
                val file = File(filePath.toString())
                Compressor.compress(this@ImageOptimizerActivity, file)
            }
            binding.imageViewOptimizedImage.setImageURI(Uri.fromFile(file))
            saveImage(file)
            binding.buttonOptimizeImage.isEnabled = true
        }
    }
    private fun saveImage(file: File) {
        GlobalScope.launch(Dispatchers.Main) {
            optimizedPicturesDirectory.mkdirs()
            val savedFile = withContext(Dispatchers.IO) {
                val newFile = File(optimizedPicturesDirectory, "${System.currentTimeMillis()}.jpg")
                file.copyTo(newFile, overwrite = true)
                newFile
            }
            val snackbar = Snackbar.make(binding.root, "Image saved to: ${savedFile.path}", Snackbar.LENGTH_LONG)
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
            return uri.path
        }
        return null
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                imageUri = it
                binding.imageViewOriginalImage.setImageURI(it)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    companion object {
        private const val READ_REQUEST_CODE = 42
    }
}