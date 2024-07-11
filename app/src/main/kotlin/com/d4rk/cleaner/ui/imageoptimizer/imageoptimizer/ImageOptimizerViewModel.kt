package com.d4rk.cleaner.ui.imageoptimizer.imageoptimizer


import android.app.Application
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.model.ui.imageoptimizer.ImageOptimizerState
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageOptimizerViewModel(application: Application) : AndroidViewModel(application) {

    val _uiState = MutableStateFlow(ImageOptimizerState())
    val uiState = _uiState.asStateFlow()
    private val compressionLevelLiveData = MutableLiveData<Int>()

    private fun getAppContext(): Context = getApplication<Application>().applicationContext


    fun setCompressionLevel(compressionLevel: Int) {
        compressionLevelLiveData.value = compressionLevel
    }

    suspend fun setQuickCompressValue(value: Int) {
        _uiState.emit(_uiState.value.copy(quickCompressValue = value))
        compressImage()
    }

    suspend fun setFileSize(size: Int) {
        _uiState.emit(_uiState.value.copy(fileSizeKB = size))
        compressImage()
    }

    suspend fun setManualCompressSettings(width: Int, height: Int, quality: Int) {
        _uiState.emit(
            _uiState.value.copy(manualWidth = width, manualHeight = height, manualQuality = quality)
        )
        compressImage()
    }

    suspend fun onImageSelected(uri: Uri) {
        _uiState.emit(
            _uiState.value.copy(
                selectedImageUri = uri,
                compressedImageUri = uri, // Initially show original image
            )
        )

    }


    private fun compressImage() = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(isLoading = true))
        val context = getAppContext()
        val originalFile = _uiState.value.selectedImageUri?.let { getRealFileFromUri(context, it) }

        // Fetch the current tab index BEFORE the suspension point
        val currentTab = _uiState.value.currentTab // Access _uiState.value here
        val compressedFile = originalFile?.let { file ->
            withContext(Dispatchers.IO) {
                try {
                    Compressor.compress(context, file) {
                        when (currentTab) {  // Use the fetched currentTab value
                            0 -> {
                                quality(_uiState.value.quickCompressValue)
                                format(Bitmap.CompressFormat.JPEG)
                            }

                            1 -> {
                                size(_uiState.value.fileSizeKB * 1024L)
                                format(Bitmap.CompressFormat.JPEG)
                            }

                            2 -> {
                                resolution(
                                    _uiState.value.manualWidth,
                                    _uiState.value.manualHeight
                                )
                                quality(_uiState.value.manualQuality)
                            }
                        }
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }

        _uiState.emit(
            _uiState.value.copy(
                isLoading = false,
                compressedImageUri = compressedFile?.let { Uri.fromFile(it) }
                    ?: _uiState.value.selectedImageUri
            )
        )
    }

    // Now a member function of the ViewModel
    private fun getRealFileFromUri(context: Context, uri: Uri): File? {
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex: Int = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileName: String = it.getString(nameIndex)
                    val file = File(context.cacheDir, fileName)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    inputStream?.use { stream ->
                        val outputStream = FileOutputStream(file)
                        stream.copyTo(outputStream)
                    }
                    return file
                }
            }
        } else if (uri.scheme == "file") {
            return File(uri.path!!)
        }
        return null
    }
}