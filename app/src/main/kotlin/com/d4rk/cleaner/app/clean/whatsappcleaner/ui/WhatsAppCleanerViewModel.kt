package com.d4rk.cleaner.app.clean.whatsappcleaner.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.data.model.ui.UiWhatsAppCleanerModel
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.usecases.DeleteWhatsAppMediaUseCase
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.usecases.GetWhatsAppMediaSummaryUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.io.File

sealed interface WhatsAppCleanerEvent : UiEvent {
    data object LoadMedia : WhatsAppCleanerEvent
    data object CleanAll : WhatsAppCleanerEvent
}

sealed interface WhatsAppCleanerAction : ActionEvent

class WhatsAppCleanerViewModel(
    private val getSummaryUseCase: GetWhatsAppMediaSummaryUseCase,
    private val deleteUseCase: DeleteWhatsAppMediaUseCase,
    private val dispatchers: DispatcherProvider
) : ScreenViewModel<UiWhatsAppCleanerModel, WhatsAppCleanerEvent, WhatsAppCleanerAction>(
    initialState = UiStateScreen(data = UiWhatsAppCleanerModel())
) {

    init { onEvent(WhatsAppCleanerEvent.LoadMedia) }

    override fun onEvent(event: WhatsAppCleanerEvent) {
        when (event) {
            WhatsAppCleanerEvent.LoadMedia -> loadSummary()
            WhatsAppCleanerEvent.CleanAll -> cleanAll()
        }
    }

    private fun loadSummary() {
        launch(context = dispatchers.io) {
            getSummaryUseCase().collectLatest { result ->
                _uiState.update { current ->
                    when (result) {
                        is DataState.Loading -> current.copy(screenState = ScreenState.IsLoading())
                        is DataState.Success -> current.copy(
                            screenState = ScreenState.Success(),
                            data = current.data?.copy(mediaSummary = result.data)
                                ?: UiWhatsAppCleanerModel(result.data)
                        )
                        is DataState.Error -> current.copy(
                            screenState = ScreenState.Error(),
                            errors = current.errors + UiSnackbar(
                                message = UiTextHelper.DynamicString("${result.error}"),
                                isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun cleanAll() {
        val files = _uiState.value.data?.mediaSummary?.let { summary ->
            summary.images + summary.videos + summary.documents
        } ?: emptyList()
        launch(context = dispatchers.io) {
            deleteUseCase(files).collectLatest {
                onEvent(WhatsAppCleanerEvent.LoadMedia)
            }
        }
    }
}
