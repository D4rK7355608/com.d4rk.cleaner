package com.d4rk.cleaner.app.clean.scanner.utils.helpers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CleaningProgressBus {
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    fun update(value: Int) {
        _progress.value = value
    }
}
