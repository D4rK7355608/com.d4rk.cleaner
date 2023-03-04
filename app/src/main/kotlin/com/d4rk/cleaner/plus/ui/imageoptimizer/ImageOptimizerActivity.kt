@file:Suppress("DEPRECATION")
package com.d4rk.cleaner.plus.ui.imageoptimizer
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.cleaner.plus.R
import com.d4rk.cleaner.plus.databinding.ActivityImageOptimizerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
class ImageOptimizerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageOptimizerBinding
    private var imageUri: Uri? = null
    private val optimizedPicturesDirectory = File(Environment.getExternalStorageDirectory(), "Pictures/Optimized Pictures").apply {
        if (!exists()) {
            mkdirs()
        }
    }
    private lateinit var pickSingleMediaLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageOptimizerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
        pickSingleMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.failed_picking_media), Toast.LENGTH_SHORT).show()
            } else {
                result.data?.data?.let {
                    imageUri = it
                    binding.imageViewOriginalImage.setImageURI(it)
                }
            }
        }
        binding.buttonChooseImage.setOnClickListener {
            pickSingleMediaLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
        binding.buttonOptimizeImage.setOnClickListener {
            imageUri?.let {
                optimizeImage(it)
            }
        }
    }
    private fun optimizeImage(imageUri: Uri) {
        GlobalScope.launch(Dispatchers.Main) {
            binding.buttonOptimizeImage.isEnabled = false
            val filePath = getPath(this@ImageOptimizerActivity, imageUri)
            val file = File(filePath!!)
            if (file.exists() && file.extension in listOf("jpg", "jpeg", "png")) {
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
            val message = getString(R.string.image_saved) + savedFile.path
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
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