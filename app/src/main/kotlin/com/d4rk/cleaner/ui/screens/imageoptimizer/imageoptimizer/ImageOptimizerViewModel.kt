package com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer


import android.app.Application
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.core.data.model.ui.imageoptimizer.ImageOptimizerState
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
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageOptimizerViewModel(application : Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(com.d4rk.cleaner.core.data.model.ui.imageoptimizer.ImageOptimizerState())
    val uiState = _uiState.asStateFlow()

    private fun getAppContext() : Context = getApplication<Application>().applicationContext

    suspend fun setQuickCompressValue(value : Int) {
        _uiState.emit(_uiState.value.copy(quickCompressValue = value))
        compressImage()
    }

    suspend fun setFileSize(size : Int) {
        _uiState.emit(_uiState.value.copy(fileSizeKB = size))
        compressImage()
    }

    suspend fun setManualCompressSettings(width : Int , height : Int , quality : Int) {
        _uiState.emit(
            _uiState.value.copy(
                manualWidth = width , manualHeight = height , manualQuality = quality
            )
        )
        compressImage()
    }

    suspend fun onImageSelected(uri : Uri) {
        _uiState.emit(
            _uiState.value.copy(
                selectedImageUri = uri ,
                compressedImageUri = uri ,
            )
        )

    }

    private fun compressImage() = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(isLoading = true))
        val context : Context = getAppContext()
        val originalFile : File? = _uiState.value.selectedImageUri?.let { getRealFileFromUri(context , it) }
        val currentTab : Int = _uiState.value.currentTab
        val compressedFile : File? = originalFile?.let { file ->
            withContext(Dispatchers.IO) {
                try {
                    Compressor.compress(context , file) {
                        when (currentTab) {
                            0 -> {
                                quality(_uiState.value.quickCompressValue)
                                format(Bitmap.CompressFormat.JPEG)
                            }

                            1 -> {
                                size(maxFileSize = _uiState.value.fileSizeKB * 1024L)
                                format(Bitmap.CompressFormat.JPEG)
                            }

                            2 -> {
                                resolution(
                                    _uiState.value.manualWidth , _uiState.value.manualHeight
                                )
                                quality(_uiState.value.manualQuality)
                            }
                        }
                    }
                } catch (e : Exception) {
                    null
                }
            }
        }

        _uiState.emit(_uiState.value.copy(
            isLoading = false ,
            compressedImageUri = compressedFile?.let { Uri.fromFile(it) } ?: _uiState.value.selectedImageUri ,
        ))
    }

    private fun getRealFileFromUri(context : Context , uri : Uri) : File? {
        if (uri.scheme == "content") {
            val cursor : Cursor? = context.contentResolver.query(uri , null , null , null , null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex : Int = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileName : String = it.getString(nameIndex)
                    val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]") , "_")
                    val file = File(context.cacheDir , sanitizedFileName)
                    val inputStream : InputStream? = context.contentResolver.openInputStream(uri)
                    inputStream?.use { input ->
                        BufferedOutputStream(FileOutputStream(file)).use { output ->
                            val buffer = ByteArray(4 * 1024)
                            var byteCount : Int
                            while (input.read(buffer).also { bytesRead ->
                                    byteCount = bytesRead
                                } >= 0) {
                                output.write(buffer , 0 , byteCount)
                            }
                        }

                    }
                    return file
                }
            }
        }
        else if (uri.scheme == "file") {
            return File(uri.path !!)
        }
        return null
    }
}