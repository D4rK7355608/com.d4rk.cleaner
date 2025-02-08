package com.d4rk.cleaner.ui.screens.imageoptimizer.imageoptimizer

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.data.model.ui.imageoptimizer.ImageOptimizerState
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageOptimizerViewModel(application : Application) : BaseViewModel(application) {

    private val _uiState : MutableStateFlow<ImageOptimizerState> = MutableStateFlow(value = ImageOptimizerState())
    val uiState : StateFlow<ImageOptimizerState> = _uiState.asStateFlow()

    fun setCurrentTab(tab : Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.value = _uiState.value.copy(currentTab = tab)
        }
    }

    fun optimizeImage() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(_uiState.value.copy(isLoading = true))
            val context : Context = getApplication<Application>().applicationContext
            val originalFile : File? = _uiState.value.selectedImageUri?.let { uri ->
                getRealFileFromUri(context = context , uri = uri)
            }
            val currentTab : Int = _uiState.value.currentTab
            val quality : Int = when (currentTab) {
                0 -> _uiState.value.quickCompressValue
                1 -> _uiState.value.quickCompressValue
                2 -> _uiState.value.manualQuality
                else -> 50
            }

            val (targetWidth : Int , targetHeight : Int) = when (currentTab) {
                0 -> originalFile?.let { getImageDimensions(file = it) } ?: Pair(first = 640 , second = 480)
                2 -> if (_uiState.value.manualWidth > 0 && _uiState.value.manualHeight > 0)
                    Pair(_uiState.value.manualWidth, _uiState.value.manualHeight)
                else Pair(640, 480)
                else -> Pair(640, 480)
            }
            val destinationFile = originalFile?.let { orig ->
                getOptimizedDestinationFile(originalFile = orig)
            }

            val compressedFile : File? = originalFile?.let { file ->
                withContext(context = Dispatchers.IO) {
                    try {
                        val tempFile : File = compressImageUsingNative(context = context , originalFile = file , quality = quality , targetWidth = targetWidth , targetHeight = targetHeight)
                        destinationFile?.apply {
                            tempFile.copyTo(target = this , overwrite = true)
                        }
                        destinationFile
                    } catch (e : Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            _uiState.emit(value = _uiState.value.copy(isLoading = false , compressedImageUri = compressedFile?.let { Uri.fromFile(it) } ?: _uiState.value.selectedImageUri , showSaveSnackbar = true))
        }
    }

    private fun getImageDimensions(file: File): Pair<Int, Int> {
        val options : BitmapFactory.Options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        return Pair(first = options.outWidth , second = options.outHeight)
    }

    private fun compressImageUsingNative(
        context : Context , originalFile : File , quality : Int , targetWidth : Int , targetHeight : Int , format : Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ) : File {
        val originalBitmap : Bitmap = BitmapFactory.decodeFile(originalFile.absolutePath) ?: throw Exception("Image Optimizer - Nu s-a putut decoda imaginea cu BitmapFactory.decodeFile")
        val scaledBitmap : Bitmap = Bitmap.createScaledBitmap(originalBitmap , targetWidth , targetHeight , true)
        val outputFile = File(context.cacheDir , "native_compressed_${System.currentTimeMillis()}.jpg")
        outputFile.outputStream().use { out ->
            scaledBitmap.compress(format , quality , out)
        }
        return outputFile
    }

    fun setQuickCompressValue(value : Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(value = _uiState.value.copy(quickCompressValue = value))
            previewCompressImage()
        }
    }

    fun setFileSize(size : Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(value = _uiState.value.copy(fileSizeKB = size))
            previewCompressImage()
        }
    }

    fun setManualCompressSettings(width : Int , height : Int , quality : Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(
                value = _uiState.value.copy(
                    manualWidth = width , manualHeight = height , manualQuality = quality
                )
            )
            previewCompressImage()
        }
    }

    fun onImageSelected(uri : Uri) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(
                value = _uiState.value.copy(
                    selectedImageUri = uri ,
                    compressedImageUri = uri ,
                )
            )
        }
    }

    private fun previewCompressImage() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(value = _uiState.value.copy(isLoading = true))
            val context : Context = getApplication<Application>().applicationContext
            val originalFile : File? = _uiState.value.selectedImageUri?.let { uri ->
                getRealFileFromUri(context = context , uri = uri)
            }
            val currentTab : Int = _uiState.value.currentTab
            val quality : Int = when (currentTab) {
                0 -> _uiState.value.quickCompressValue
                1 -> _uiState.value.quickCompressValue
                2 -> _uiState.value.manualQuality
                else -> 50
            }

            val (targetWidth, targetHeight) = when (currentTab) {
                0 -> originalFile?.let { getImageDimensions(it) } ?: Pair(640, 480)
                2 -> if (_uiState.value.manualWidth > 0 && _uiState.value.manualHeight > 0)
                    Pair(_uiState.value.manualWidth, _uiState.value.manualHeight)
                else Pair(640, 480)
                else -> Pair(640, 480)
            }

            val previewFile : File? = originalFile?.let { file ->
                withContext(Dispatchers.IO) {
                    try {
                        compressImageUsingNative(context = context , originalFile = file , quality = quality , targetWidth = targetWidth , targetHeight = targetHeight)
                    } catch (e : Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            _uiState.emit(value = _uiState.value.copy(isLoading = false , compressedImageUri = previewFile?.let { Uri.fromFile(it) } ?: _uiState.value.selectedImageUri))
        }
    }

    private suspend fun getRealFileFromUri(context : Context , uri : Uri) : File? = withContext(Dispatchers.IO) {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri , null , null , null , null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex : Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileName : String = cursor.getString(nameIndex)
                    val sanitizedFileName : String = fileName.replace(regex = Regex(pattern = "[^a-zA-Z0-9._-]") , replacement = "_")
                    val file = File(context.cacheDir , sanitizedFileName)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    return@withContext file
                }
            }
        }
        else if (uri.scheme == "file") {
            return@withContext File(uri.path !!)
        }
        return@withContext null
    }

    private fun getOptimizedDestinationFile(originalFile : File) : File {
        val picturesDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val optimizedDir = File(picturesDir , "Optimized images")
        if (! optimizedDir.exists()) {
            optimizedDir.mkdirs()
        }
        val fileExtension : String = originalFile.extension.ifEmpty { "jpg" }
        val fileName = "optimized_${System.currentTimeMillis()}.$fileExtension"
        return File(optimizedDir , fileName)
    }

    fun updateShowSaveSnackbar(show : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(value = _uiState.value.copy(showSaveSnackbar = show))
        }
    }
}