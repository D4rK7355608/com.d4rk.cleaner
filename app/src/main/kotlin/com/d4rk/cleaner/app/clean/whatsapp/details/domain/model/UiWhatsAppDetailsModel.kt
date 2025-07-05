package com.d4rk.cleaner.app.clean.whatsapp.details.domain.model

import com.d4rk.cleaner.app.clean.whatsapp.details.ui.SortType
import java.io.File

data class UiWhatsAppDetailsModel(
    val isGridView: Boolean = true,
    val descending: Boolean = false,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val sortType: SortType = SortType.DATE,
    val files: List<File> = emptyList(),
    val suggested: List<File> = emptyList(),
)
