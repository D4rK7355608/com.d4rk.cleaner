package com.d4rk.cleaner.app.images.picker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.app.images.picker.domain.data.model.ui.UiImagePickerModel
import com.d4rk.cleaner.app.images.picker.domain.usecases.ShowFabUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.Uri

class ImagePickerViewModel(
    private val showFabUseCase: ShowFabUseCase = ShowFabUseCase()
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiImagePickerModel> = MutableStateFlow(UiImagePickerModel())
    val uiState: StateFlow<UiImagePickerModel> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            showFabUseCase()
            _uiState.value = _uiState.value.copy(isFabVisible = true)
        }
    }

    fun setSelectedImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }
}
