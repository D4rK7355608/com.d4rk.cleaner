package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

enum class SortType { NAME, DATE, SIZE }

class DetailsViewModel(private val dataStore: DataStore) : ViewModel() {

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView

    private val _descending = MutableStateFlow(false)
    val descending: StateFlow<Boolean> = _descending

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate: StateFlow<Long?> = _startDate

    private val _endDate = MutableStateFlow<Long?>(null)
    val endDate: StateFlow<Long?> = _endDate

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType

    private val _files = MutableStateFlow<List<File>>(emptyList())
    val files: StateFlow<List<File>> = _files

    private val _suggested = MutableStateFlow<List<File>>(emptyList())
    val suggested: StateFlow<List<File>> = _suggested

    init {
        viewModelScope.launch {
            dataStore.whatsappGridView.collect { _isGridView.value = it }
        }
    }

    fun setFiles(list: List<File>) {
        val sorted = sort(list)
        _files.value = sorted
        _suggested.value = sort(getJunkCandidates(sorted))
    }

    fun toggleView() {
        viewModelScope.launch {
            val new = !_isGridView.value
            dataStore.saveWhatsAppGridView(new)
            _isGridView.value = new
        }
    }

    fun applySort(type: SortType, descending: Boolean, start: Long?, end: Long?) {
        _sortType.value = type
        _descending.value = descending
        _startDate.value = start
        _endDate.value = end
        _files.update { sort(it) }
        _suggested.update { sort(getJunkCandidates(_files.value)) }
    }

    private fun sort(list: List<File>): List<File> {
        var sorted = when (_sortType.value) {
            SortType.NAME -> list.sortedBy { it.name.lowercase() }
            SortType.DATE -> list.sortedBy { it.lastModified() }
            SortType.SIZE -> list.sortedBy { it.length() }
        }
        if (_sortType.value == SortType.DATE && _startDate.value != null && _endDate.value != null) {
            sorted = sorted.filter { it.lastModified() in _startDate.value!!.._endDate.value!! }
        }
        if (_descending.value) sorted = sorted.reversed()
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
