package com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model

import java.io.File

data class WhatsAppMediaSummary(
    val images: List<File> = emptyList(),
    val videos: List<File> = emptyList(),
    val documents: List<File> = emptyList()
) {
    val hasData: Boolean
        get() = images.isNotEmpty() || videos.isNotEmpty() || documents.isNotEmpty()
}

data class UiWhatsAppCleanerModel(
    val mediaSummary: WhatsAppMediaSummary = WhatsAppMediaSummary()
)