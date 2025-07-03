package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File

enum class SortType { NAME, DATE, SIZE }

class DetailsViewModel : ViewModel() {

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType

    private val _files = MutableStateFlow<List<File>>(emptyList())
    val files: StateFlow<List<File>> = _files

    fun setFiles(list: List<File>) {
        _files.value = sort(list)
    }

    fun toggleView() {
        _isGridView.value = !_isGridView.value
    }

    fun applySort(type: SortType) {
        _sortType.value = type
        _files.update { sort(it) }
    }

    private fun sort(list: List<File>): List<File> {
        val sorted = when (_sortType.value) {
            SortType.NAME -> list.sortedBy { it.name.lowercase() }
            SortType.DATE -> list.sortedBy { it.lastModified() }
            SortType.SIZE -> list.sortedBy { it.length() }
        }
        return sorted
    }
}
