package com.d4rk.cleaner.app.clean.home.domain.data.model.ui

import java.io.File

data class WhatsAppMediaSummary(
    val images: List<File> = emptyList(),
    val videos: List<File> = emptyList(),
    val documents: List<File> = emptyList()
) {
    val hasData: Boolean
        get() = images.isNotEmpty() || videos.isNotEmpty() || documents.isNotEmpty()
}
