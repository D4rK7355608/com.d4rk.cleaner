package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.concurrent.TimeUnit

enum class SortType { NAME, DATE, SIZE }

class DetailsViewModel : ViewModel() {

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType

    private val _files = MutableStateFlow<List<File>>(emptyList())
    val files: StateFlow<List<File>> = _files

    private val _suggested = MutableStateFlow<List<File>>(emptyList())
    val suggested: StateFlow<List<File>> = _suggested

    fun setFiles(list: List<File>) {
        val sorted = sort(list)
        _files.value = sorted
        _suggested.value = sort(getJunkCandidates(sorted))
    }

    fun toggleView() {
        _isGridView.value = !_isGridView.value
    }

    fun applySort(type: SortType) {
        _sortType.value = type
        _files.update { sort(it) }
        _suggested.update { sort(getJunkCandidates(_files.value)) }
    }

    private fun sort(list: List<File>): List<File> {
        val sorted = when (_sortType.value) {
            SortType.NAME -> list.sortedBy { it.name.lowercase() }
            SortType.DATE -> list.sortedBy { it.lastModified() }
            SortType.SIZE -> list.sortedBy { it.length() }
        }
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
