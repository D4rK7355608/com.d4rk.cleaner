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

    fun setCurrentTab(tab: Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val currentState = _uiState.value
            val newState = when (tab) {
                1 -> {
                    // Pentru File Size, resetăm preview-ul și fileSizeKB
                    currentState.copy(
                        currentTab = tab,
                        compressedImageUri = currentState.selectedImageUri,
                        fileSizeKB = 0
                    )
                }
                2 -> {
                    // Pentru Manual, dorim să resetăm valorile la cele originale (dacă imaginea e selectată)
                    val file = currentState.selectedImageUri?.let { uri ->
                        getRealFileFromUri(getApplication(), uri)
                    }
                    val (w, h) = file?.let { getImageDimensions(it) } ?: Pair(640, 480)
                    currentState.copy(
                        currentTab = tab,
                        compressedImageUri = currentState.selectedImageUri,
                        // Resetați valorile manuale la dimensiunile originale și o calitate default (ex. 50)
                        manualWidth = w,
                        manualHeight = h,
                        manualQuality = 50
                    )
                }
                else -> currentState.copy(currentTab = tab)
            }
            _uiState.value = newState
        }
    }


    fun optimizeImage() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(value = _uiState.value.copy(isLoading = true))
            val context : Context = getApplication<Application>().applicationContext
            val originalFile : File? = _uiState.value.selectedImageUri?.let { uri ->
                getRealFileFromUri(context , uri)
            }
            val currentTab : Int = _uiState.value.currentTab
            val quality : Int = when (currentTab) {
                0 -> _uiState.value.quickCompressValue
                1 -> 100
                2 -> _uiState.value.manualQuality
                else -> 50
            }

            val (targetWidth : Int , targetHeight : Int) = when (currentTab) {
                0 , 1 -> originalFile?.let { getImageDimensions(it) } ?: Pair(first = 640 , second = 480)
                2 -> if (_uiState.value.manualWidth > 0 && _uiState.value.manualHeight > 0) Pair(first = _uiState.value.manualWidth , second = _uiState.value.manualHeight)
                else Pair(first = 640 , second = 480)

                else -> Pair(first = 640 , second = 480)
            }

            val destinationFile : File? = originalFile?.let { getOptimizedDestinationFile(it) }
            val compressedFile : File? = originalFile?.let { file ->
                withContext(Dispatchers.IO) {
                    try {
                        if (currentTab == 1 && _uiState.value.fileSizeKB > 0) {
                            val desiredSizeBytes : Long = _uiState.value.fileSizeKB * 1024L
                            val tempFile = compressImageToTargetSize(context = context , originalFile = file , targetWidth = targetWidth , targetHeight = targetHeight , desiredSizeBytes = desiredSizeBytes)
                            destinationFile?.apply {
                                tempFile.copyTo(this , overwrite = true)
                            }
                            destinationFile
                        }
                        else {
                            val tempFile : File = compressImageUsingNative(context = context , originalFile = file , quality = quality , targetWidth = targetWidth , targetHeight = targetHeight)
                            destinationFile?.apply {
                                tempFile.copyTo(target = this , overwrite = true)
                            }
                            destinationFile
                        }
                    } catch (e : Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            _uiState.emit(value = _uiState.value.copy(isLoading = false , compressedImageUri = compressedFile?.let { Uri.fromFile(it) } ?: _uiState.value.selectedImageUri , showSaveSnackbar = true))
        }
    }

    private fun compressImageToTargetSize(
        context : Context , originalFile : File , targetWidth : Int , targetHeight : Int , desiredSizeBytes : Long , format : Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ) : File {
        val originalBitmap : Bitmap = BitmapFactory.decodeFile(originalFile.absolutePath) ?: throw Exception("Nu s-a putut decoda imaginea.")

        if (originalFile.length() <= desiredSizeBytes) {
            return originalFile
        }

        val scaledBitmap : Bitmap = Bitmap.createScaledBitmap(originalBitmap , targetWidth , targetHeight , true)

        var minQuality = 10
        var maxQuality = 100
        var finalQuality : Int = maxQuality
        var compressedFile : File

        while (minQuality <= maxQuality) {
            finalQuality = (minQuality + maxQuality) / 2

            compressedFile = File(context.cacheDir , "temp_compressed.jpg")
            compressedFile.outputStream().use { out ->
                scaledBitmap.compress(format , finalQuality , out)
            }
            val fileSize : Long = compressedFile.length()
            if (fileSize in (desiredSizeBytes * 95 / 100)..(desiredSizeBytes * 105 / 100)) {
                break
            }
            if (fileSize > desiredSizeBytes) {
                maxQuality = finalQuality - 1
            }
            else {
                minQuality = finalQuality + 1
            }
        }
        compressedFile = File(context.cacheDir , "native_compressed_${System.currentTimeMillis()}.jpg")
        compressedFile.outputStream().use { out ->
            scaledBitmap.compress(format , finalQuality , out)
        }
        return compressedFile
    }

    private fun getImageDimensions(file : File) : Pair<Int , Int> {
        val options : BitmapFactory.Options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath , options)
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

    fun setManualCompressSettings(width: Int, height: Int, quality: Int) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(
                _uiState.value.copy(
                    manualWidth = width,
                    manualHeight = height,
                    manualQuality = quality
                )
            )
            previewCompressImage()
        }
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val file = getRealFileFromUri(getApplication(), uri)
            val (w, h) = file?.let { getImageDimensions(it) } ?: Pair(640, 480)
            _uiState.emit(
                _uiState.value.copy(
                    selectedImageUri = uri,
                    compressedImageUri = uri,
                    // Dacă nu s-au setat valorile manual, le setăm cu dimensiunile originale
                    manualWidth = if (_uiState.value.manualWidth == 0) w else _uiState.value.manualWidth,
                    manualHeight = if (_uiState.value.manualHeight == 0) h else _uiState.value.manualHeight
                )
            )
        }
    }

    private fun previewCompressImage() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(_uiState.value.copy(isLoading = true))
            val context = getApplication<Application>().applicationContext
            val originalFile = _uiState.value.selectedImageUri?.let { uri -> getRealFileFromUri(context, uri) }
            val currentTab = _uiState.value.currentTab
            val quality = when (currentTab) {
                0 -> _uiState.value.quickCompressValue
                1 -> 100
                2 -> _uiState.value.manualQuality
                else -> 50
            }
            val (targetWidth, targetHeight) = when (currentTab) {
                0, 1 -> originalFile?.let { getImageDimensions(it) } ?: Pair(640, 480)
                2 -> if (_uiState.value.manualWidth > 0 && _uiState.value.manualHeight > 0)
                    Pair(_uiState.value.manualWidth, _uiState.value.manualHeight)
                else Pair(640, 480)
                else -> Pair(640, 480)
            }
            val previewFile = originalFile?.let { file ->
                withContext(Dispatchers.IO) {
                    try {
                        if (currentTab == 1 && _uiState.value.fileSizeKB > 0) {
                            val desiredSizeBytes = _uiState.value.fileSizeKB * 1024L
                            compressImageToTargetSize(context, file, targetWidth, targetHeight, desiredSizeBytes)
                        } else {
                            compressImageUsingNative(context, file, quality, targetWidth, targetHeight)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
            _uiState.emit(_uiState.value.copy(isLoading = false, compressedImageUri = previewFile?.let { Uri.fromFile(it) } ?: _uiState.value.selectedImageUri))
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

    suspend fun getOriginalSizeInKB(uri : Uri) : Int {
        val file : File? = getRealFileFromUri(context = getApplication() , uri = uri)
        return file?.length()?.div(other = 1024)?.toInt() ?: 0
    }

    fun generateDynamicPresets(originalSizeKB : Int) : List<String> {
        val presets : MutableList<String> = mutableListOf()
        for (p in 90 downTo 10 step 10) {
            var suggested : Int = (originalSizeKB * p) / 100
            if (suggested < originalSizeKB) {
                if (suggested % 2 != 0) {
                    suggested --
                }
                presets.add(element = suggested.toString())
            }
        }
        return presets
    }


    fun updateShowSaveSnackbar(show : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.emit(value = _uiState.value.copy(showSaveSnackbar = show))
        }
    }
}