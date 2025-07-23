package com.d4rk.cleaner.app.clean.whatsapp.summary.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerAction
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerEvent
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.UiWhatsAppCleanerModel
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.usecases.DeleteWhatsAppMediaUseCase
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.usecases.GetWhatsAppMediaFilesUseCase
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.usecases.GetWhatsAppMediaSummaryUseCase
import com.d4rk.cleaner.app.clean.whatsapp.utils.constants.WhatsAppMediaConstants
import com.d4rk.cleaner.core.utils.helpers.CleaningEventBus
import com.d4rk.cleaner.core.utils.helpers.FileSizeFormatter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.io.File

class WhatsappCleanerSummaryViewModel(
    private val getSummaryUseCase: GetWhatsAppMediaSummaryUseCase,
    private val deleteUseCase: DeleteWhatsAppMediaUseCase,
    private val getFilesUseCase: GetWhatsAppMediaFilesUseCase,
    private val dispatchers: DispatcherProvider
) : ScreenViewModel<UiWhatsAppCleanerModel, WhatsAppCleanerEvent, WhatsAppCleanerAction>(
    initialState = UiStateScreen(data = UiWhatsAppCleanerModel())
) {

    init { onEvent(WhatsAppCleanerEvent.LoadMedia) }

    override fun onEvent(event: WhatsAppCleanerEvent) {
        when (event) {
            WhatsAppCleanerEvent.LoadMedia -> loadSummary()
            WhatsAppCleanerEvent.CleanAll -> cleanAll()
            is WhatsAppCleanerEvent.DeleteSelected -> deleteSelected(event.files)
        }
    }

    private fun loadSummary() {
        launch(context = dispatchers.io) {
            getSummaryUseCase().collectLatest { result ->
                _uiState.update { current ->
                    when (result) {
                        is DataState.Loading -> current.copy(screenState = ScreenState.IsLoading())
                        is DataState.Success -> current.copy(
                            screenState = if (result.data.totalBytes != 0L) ScreenState.Success() else ScreenState.NoData(),
                            data = current.data?.copy(
                                mediaSummary = result.data,
                                totalSize = result.data.formattedTotalSize
                            ) ?: UiWhatsAppCleanerModel(
                                mediaSummary = result.data,
                                totalSize = result.data.formattedTotalSize
                            )
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
        launch(context = dispatchers.io) {
            val types = WhatsAppMediaConstants.DIRECTORIES.keys
            val files = mutableListOf<File>()
            for (type in types) {
                getFilesUseCase(type, 0, Int.MAX_VALUE).collectLatest { res ->
                    if (res is DataState.Success) files.addAll(res.data)
                }
            }

            deleteUseCase(files).collectLatest { result ->
                val freed = files.sumOf { it.length() }
                if (result is DataState.Success) {
                    sendAction(
                        WhatsAppCleanerAction.ShowSnackbar(
                            UiSnackbar(
                                message = UiTextHelper.DynamicString(
                                    "Cleaned ${FileSizeFormatter.format(freed)}"
                                )
                            )
                        )
                    )
                }
                onEvent(WhatsAppCleanerEvent.LoadMedia)
                CleaningEventBus.notifyCleaned()
            }
        }
    }

    private fun deleteSelected(files: List<File>) {
        if (files.isEmpty()) return
        launch(context = dispatchers.io) {
            deleteUseCase(files).collectLatest { result ->
                val freed = files.sumOf { it.length() }
                if (result is DataState.Success) {
                    sendAction(
                        WhatsAppCleanerAction.ShowSnackbar(
                            UiSnackbar(
                                message = UiTextHelper.DynamicString(
                                    "Cleaned ${FileSizeFormatter.format(freed)}"
                                )
                            )
                        )
                    )
                }
                onEvent(WhatsAppCleanerEvent.LoadMedia)
                CleaningEventBus.notifyCleaned()
            }
        }
    }

}
