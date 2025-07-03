package com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model

import java.io.File

/**
 * Holds file list and size info for a WhatsApp media directory.
 */
data class DirectorySummary(
    val files: List<File> = emptyList(),
    val totalBytes: Long = 0L,
    val formattedSize: String = "0 B"
)
