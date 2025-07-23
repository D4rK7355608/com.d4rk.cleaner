package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.cleaner.app.clean.whatsapp.details.domain.actions.WhatsAppDetailsAction
import com.d4rk.cleaner.app.clean.whatsapp.details.domain.actions.WhatsAppDetailsEvent
import com.d4rk.cleaner.app.clean.whatsapp.details.domain.model.UiWhatsAppDetailsModel
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.usecases.GetWhatsAppMediaFilesUseCase
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.concurrent.TimeUnit

enum class SortType { NAME, DATE, SIZE }

private const val PAGE_SIZE = 100

class DetailsViewModel(
    private val dataStore: DataStore,
    private val dispatchers: DispatcherProvider,
    private val getFilesUseCase: GetWhatsAppMediaFilesUseCase,
) : ScreenViewModel<UiWhatsAppDetailsModel, WhatsAppDetailsEvent, WhatsAppDetailsAction>(
    initialState = UiStateScreen(data = UiWhatsAppDetailsModel())
) {

    init {
        launch {
            dataStore.whatsappGridView.collectLatest { isGrid ->
                _uiState.updateData(newState = _uiState.value.screenState) { current ->
                    current.copy(isGridView = isGrid)
                }
            }
        }
    }

    override fun onEvent(event: WhatsAppDetailsEvent) {
        when (event) {
            is WhatsAppDetailsEvent.LoadFiles -> loadFiles(event.type, event.page)
            WhatsAppDetailsEvent.ToggleView -> toggleView()
            is WhatsAppDetailsEvent.ApplySort -> applySort(
                type = event.type,
                descending = event.descending,
                start = event.startDate,
                end = event.endDate
            )
        }
    }

    private fun loadFiles(type: String, page: Int) {
        launch(dispatchers.io) {
            getFilesUseCase(type, page * PAGE_SIZE, PAGE_SIZE).collectLatest { result ->
                when (result) {
                    is DataState.Success -> setFiles(result.data)
                    is DataState.Error -> _uiState.update { it.copy(screenState = ScreenState.Error()) }
                    else -> {}
                }
            }
        }
    }

    private fun setFiles(list: List<File>) {
        launch(dispatchers.default) {
            val sorted = sort(list)
            _uiState.update { current ->
                val data = current.data ?: UiWhatsAppDetailsModel()
                current.copy(
                    data = data.copy(
                        files = sorted,
                        suggested = sort(getJunkCandidates(sorted))
                    ),
                    screenState = if (sorted.isEmpty()) ScreenState.NoData() else ScreenState.Success()
                )
            }
        }
    }

    private fun toggleView() {
        val new = !(_uiState.value.data?.isGridView ?: true)
        launch { dataStore.saveWhatsAppGridView(new) }
        _uiState.updateData(ScreenState.Success()) { it.copy(isGridView = new) }
    }

    private fun applySort(type: SortType, descending: Boolean, start: Long?, end: Long?) {
        _uiState.update { current ->
            val data = current.data ?: UiWhatsAppDetailsModel()
            current.copy(
                data = data.copy(
                    sortType = type,
                    descending = descending,
                    startDate = start,
                    endDate = end
                )
            )
        }
        launch(dispatchers.default) {
            _uiState.update { current ->
                val sorted = sort(current.data?.files ?: emptyList())
                current.copy(
                    data = current.data?.copy(
                        files = sorted,
                        suggested = sort(getJunkCandidates(sorted))
                    )
                )
            }
        }
    }

    private fun sort(list: List<File>): List<File> {
        val state = _uiState.value.data ?: UiWhatsAppDetailsModel()
        var sorted = when (state.sortType) {
            SortType.NAME -> list.sortedBy { it.name.lowercase() }
            SortType.DATE -> list.sortedBy { it.lastModified() }
            SortType.SIZE -> list.sortedBy { it.length() }
        }
        if (state.sortType == SortType.DATE && state.startDate != null && state.endDate != null) {
            sorted = sorted.filter { it.lastModified() in state.startDate..state.endDate }
        }
        if (state.descending) sorted = sorted.reversed()
        return sorted
    }

    private fun getJunkCandidates(list: List<File>): List<File> {
        val threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(60)
        return list.filter { file ->
            (
                    file.absolutePath.contains("WhatsApp Images${File.separator}Sent") ||
                            file.absolutePath.contains("WhatsApp Video${File.separator}Sent")
                    ) && file.lastModified() < threshold && file.length() > 1_000_000
        }
    }
}
