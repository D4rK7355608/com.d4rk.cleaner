package com.d4rk.cleaner.app.images.compressor.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.app.images.compressor.domain.data.model.ui.UiImageOptimizerState
import com.d4rk.cleaner.app.images.compressor.domain.usecases.CompressImageUseCase
import com.d4rk.cleaner.app.images.compressor.domain.usecases.GetImageDimensionsUseCase
import com.d4rk.cleaner.app.images.compressor.domain.usecases.GetOptimizedDestinationFileUseCase
import com.d4rk.cleaner.app.images.compressor.domain.usecases.GetRealFileFromUriUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.lang.String.format
import java.util.Locale

class ImageOptimizerViewModel(
    application: Application,
    private val compressImageUseCase: CompressImageUseCase = CompressImageUseCase(application.applicationContext),
    private val getRealFileFromUriUseCase: GetRealFileFromUriUseCase = GetRealFileFromUriUseCase(
        application.applicationContext
    ),
    private val getImageDimensionsUseCase: GetImageDimensionsUseCase = GetImageDimensionsUseCase(),
    private val getDestinationFileUseCase: GetOptimizedDestinationFileUseCase = GetOptimizedDestinationFileUseCase()
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiImageOptimizerState> =
        MutableStateFlow(UiImageOptimizerState())
    val uiState: StateFlow<UiImageOptimizerState> = _uiState.asStateFlow()

    fun setCurrentTab(tab: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val newState = when (tab) {
                1 -> currentState.copy(
                    currentTab = tab,
                    compressedImageUri = currentState.selectedImageUri,
                    fileSizeKB = 0
                )

                2 -> {
                    val file = currentState.selectedImageUri?.let { uri ->
                        getRealFileFromUriUseCase(uri)
                    }
                    val (w, h) = file?.getOrNull()?.let { getImageDimensionsUseCase(it) } ?: Pair(
                        640,
                        480
                    )
                    currentState.copy(
                        currentTab = tab,
                        compressedImageUri = currentState.selectedImageUri,
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
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(isLoading = true))
            val originalFile = _uiState.value.selectedImageUri?.let { uri ->
                getRealFileFromUriUseCase(uri)
            }?.getOrNull()
            val currentTab = _uiState.value.currentTab
            val quality = when (currentTab) {
                0 -> _uiState.value.quickCompressValue
                1 -> 100
                2 -> _uiState.value.manualQuality
                else -> 50
            }
            val (targetWidth, targetHeight) = when (currentTab) {
                0, 1 -> originalFile?.let { getImageDimensionsUseCase(it) } ?: Pair(640, 480)
                2 -> if (_uiState.value.manualWidth > 0 && _uiState.value.manualHeight > 0) Pair(
                    _uiState.value.manualWidth,
                    _uiState.value.manualHeight
                ) else Pair(640, 480)

                else -> Pair(640, 480)
            }
            val destinationFile = originalFile?.let { getDestinationFileUseCase(it) }
            val compressedFile = originalFile?.let { file ->
                runCatching {
                    val desiredBytes =
                        if (currentTab == 1 && _uiState.value.fileSizeKB > 0) _uiState.value.fileSizeKB * 1024L else null
                    val tempFile =
                        compressImageUseCase(file, quality, targetWidth, targetHeight, desiredBytes)
                    destinationFile?.apply { tempFile.copyTo(this, overwrite = true) }
                    destinationFile
                }.getOrElse { e ->
                    e.printStackTrace()
                    null
                }
            }
            _uiState.emit(
                _uiState.value.copy(
                    isLoading = false,
                    compressedImageUri = compressedFile?.let { Uri.fromFile(it) }
                        ?: _uiState.value.selectedImageUri,
                    showSaveSnackbar = true)
            )
        }
    }

    fun setQuickCompressValue(value: Int) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(quickCompressValue = value))
            previewCompressImage()
        }
    }

    fun setFileSize(size: Int) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(fileSizeKB = size))
            previewCompressImage()
        }
    }

    fun setManualCompressSettings(width: Int, height: Int, quality: Int) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            val file = getRealFileFromUriUseCase(uri)
            val (w, h) = file.getOrNull()?.let { getImageDimensionsUseCase(it) } ?: Pair(640, 480)
            val originalSizeKB: Double =
                file.getOrNull()?.let { format(Locale.US, "%.2f", it.length() / 1024.0).toDouble() }
                    ?: 0.0
            _uiState.emit(
                _uiState.value.copy(
                    selectedImageUri = uri,
                    compressedImageUri = uri,
                    manualWidth = if (_uiState.value.manualWidth == 0) w else _uiState.value.manualWidth,
                    manualHeight = if (_uiState.value.manualHeight == 0) h else _uiState.value.manualHeight,
                    compressedSizeKB = originalSizeKB
                )
            )
        }
    }

    private fun previewCompressImage() {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(isLoading = true))
            val originalFile = _uiState.value.selectedImageUri?.let { uri ->
                getRealFileFromUriUseCase(uri)
            }?.getOrNull()
            val currentTab = _uiState.value.currentTab
            val quality = when (currentTab) {
                0 -> _uiState.value.quickCompressValue
                1 -> 100
                2 -> _uiState.value.manualQuality
                else -> 50
            }
            val (targetWidth, targetHeight) = when (currentTab) {
                0, 1 -> originalFile?.let { getImageDimensionsUseCase(it) } ?: Pair(640, 480)
                2 -> if (_uiState.value.manualWidth > 0 && _uiState.value.manualHeight > 0) Pair(
                    _uiState.value.manualWidth,
                    _uiState.value.manualHeight
                ) else Pair(640, 480)

                else -> Pair(640, 480)
            }
            val previewFile = originalFile?.let { file ->
                runCatching {
                    val desiredBytes =
                        if (currentTab == 1 && _uiState.value.fileSizeKB > 0) _uiState.value.fileSizeKB * 1024L else null
                    compressImageUseCase(file, quality, targetWidth, targetHeight, desiredBytes)
                }.getOrElse { e ->
                    e.printStackTrace()
                    null
                }
            }
            val newSizeKB: Double = previewFile?.let { it.length() / 1024.0 } ?: 0.0
            val roundedSizeKB: Double = format(Locale.US, "%.2f", newSizeKB).toDouble()
            _uiState.emit(
                _uiState.value.copy(
                    isLoading = false,
                    compressedImageUri = previewFile?.let { Uri.fromFile(it) }
                        ?: _uiState.value.selectedImageUri,
                    compressedSizeKB = roundedSizeKB)
            )
        }
    }

    suspend fun getOriginalSizeInKB(uri: Uri): Int {
        val file: File? = getRealFileFromUriUseCase(uri).getOrNull()
        return file?.length()?.div(1024)?.toInt() ?: 0
    }

    fun generateDynamicPresets(originalSizeKB: Int): List<String> {
        val presets: MutableList<String> = mutableListOf()
        for (p in 90 downTo 10 step 10) {
            var suggested: Int = (originalSizeKB * p) / 100
            if (suggested < originalSizeKB) {
                if (suggested % 2 != 0) {
                    suggested--
                }
                presets.add(suggested.toString())
            }
        }
        return presets
    }

    fun updateShowSaveSnackbar(show: Boolean) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(showSaveSnackbar = show))
        }
    }
}
