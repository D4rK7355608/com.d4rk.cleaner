package com.d4rk.cleaner.ui.screens.help

import android.app.Activity
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.core.data.model.ui.screens.UiHelpScreen
import com.d4rk.cleaner.ui.screens.help.repository.HelpRepository
import com.d4rk.cleaner.core.ui.viewmodel.BaseViewModel
import com.google.android.play.core.review.ReviewInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HelpViewModel(application : Application) : BaseViewModel(application) {

    private val repository : HelpRepository = HelpRepository(application = application)

    private val _uiState : MutableStateFlow<com.d4rk.cleaner.core.data.model.ui.screens.UiHelpScreen> = MutableStateFlow(com.d4rk.cleaner.core.data.model.ui.screens.UiHelpScreen())
    val uiState : StateFlow<com.d4rk.cleaner.core.data.model.ui.screens.UiHelpScreen> = _uiState

    init {
        initializeVisibilityStates()
        getFAQs()
        requestReviewFlow()
    }

    private fun initializeVisibilityStates() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            delay(timeMillis = 100L)
            showFab()
        }
    }

    private fun getFAQs() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getFAQsRepository { faqList ->
                _uiState.value = _uiState.value.copy(questions = faqList)
            }
        }
    }

    fun requestReviewFlow() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.requestReviewFlowRepository(onSuccess = { reviewInfo ->
                _uiState.value = _uiState.value.copy(reviewInfo = reviewInfo)
            } , onFailure = {})
        }
    }

    fun launchReviewFlow(activity : Activity , reviewInfo : ReviewInfo) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.launchReviewFlowRepository(activity = activity , reviewInfo = reviewInfo)
        }
    }
}