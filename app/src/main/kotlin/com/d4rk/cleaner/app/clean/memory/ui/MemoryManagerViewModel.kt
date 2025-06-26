package com.d4rk.cleaner.app.clean.memory.ui

import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.app.clean.memory.domain.actions.MemoryAction
import com.d4rk.cleaner.app.clean.memory.domain.actions.MemoryEvent
import com.d4rk.cleaner.app.clean.memory.domain.data.model.ui.UiMemoryManagerScreen
import com.d4rk.cleaner.app.clean.memory.domain.usecases.GetRamInfoUseCase
import com.d4rk.cleaner.app.clean.memory.domain.usecases.GetStorageInfoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.supervisorScope

class MemoryManagerViewModel(private val getStorageInfoUseCase : GetStorageInfoUseCase , private val getRamInfoUseCase : GetRamInfoUseCase , private val dispatchers : DispatcherProvider) :
    ScreenViewModel<UiMemoryManagerScreen , MemoryEvent , MemoryAction>(initialState = UiStateScreen(data = UiMemoryManagerScreen())) {

    private var ramUpdateJob : Job? = null

    init {
        onEvent(MemoryEvent.LoadMemoryData)
    }

    override fun onEvent(event : MemoryEvent) {
        when (event) {
            MemoryEvent.LoadMemoryData -> loadMemoryData()
            MemoryEvent.ToggleListExpanded -> toggleListExpanded()
        }
    }

    private fun loadMemoryData() {
        launch {
            supervisorScope {
                val storageJob = launch(dispatchers.default) {
                    getStorageInfoUseCase().collectLatest { result ->
                        _uiState.update { current ->
                            val updatedErrors = if (result is DataState.Error) {
                                current.errors + listOf(UiSnackbar(message = UiTextHelper.DynamicString("Failed to load storage info: ${result.error}") , isError = true))
                            }
                            else current.errors

                            current.copy(
                                data = current.data?.copy(
                                    storageInfo = (result as? DataState.Success)?.data ?: current.data?.storageInfo
                                ) , screenState = when (result) {
                                    is DataState.Loading -> ScreenState.IsLoading()
                                    is DataState.Error -> ScreenState.Error()
                                    is DataState.Success -> current.screenState
                                } , errors = updatedErrors
                            )
                        }
                    }
                }

                val ramJob = launch(dispatchers.default) {
                    getRamInfoUseCase().collectLatest { result ->
                        _uiState.update { current ->
                            val updatedErrors = if (result is DataState.Error) {
                                current.errors + listOf(
                                    UiSnackbar(message = UiTextHelper.DynamicString("Failed to load RAM info: ${result.error}") , isError = true)
                                )
                            }
                            else current.errors

                            val newRamInfo = (result as? DataState.Success)?.data
                            val newState = when (result) {
                                is DataState.Loading -> ScreenState.IsLoading()
                                is DataState.Error -> ScreenState.Error()
                                is DataState.Success -> if (current.data?.storageInfo != null) ScreenState.Success()
                                else current.screenState
                            }

                            current.copy(
                                data = current.data?.copy(ramInfo = newRamInfo ?: current.data?.ramInfo) , screenState = newState , errors = updatedErrors
                            )
                        }

                        if (result is DataState.Success) {
                            startPeriodicRamUpdate()
                        }
                    }
                }

                joinAll(storageJob , ramJob)
            }
        }
    }

    private fun startPeriodicRamUpdate() {
        ramUpdateJob?.cancel()
        ramUpdateJob = launch(dispatchers.default) {
            while (isActive) {
                delay(5000)

                runCatching {
                    getRamInfoUseCase().collectLatest { result ->
                        when (result) {
                            is DataState.Success -> {
                                _uiState.successData {
                                    copy(ramInfo = result.data)
                                }
                            }

                            is DataState.Error -> {
                                coroutineContext.ensureActive()
                                _uiState.showSnackbar(
                                    UiSnackbar(
                                        message = UiTextHelper.DynamicString("Error updating RAM info: ${result.error}"),
                                        isError = true
                                    )
                                )
                            }

                            else -> Unit
                        }
                    }
                }.onFailure { e ->
                    coroutineContext.ensureActive()
                    _uiState.showSnackbar(
                        UiSnackbar(
                            message = UiTextHelper.DynamicString("Exception during RAM update: ${e.message}"),
                            isError = true
                        )
                    )
                    cancel()
                }
            }
        }
    }


    private fun toggleListExpanded() {
        _uiState.updateData(ScreenState.Success()) {
            it.copy(listExpanded = ! it.listExpanded)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ramUpdateJob?.cancel()
    }
}