package com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model

/**
 * Holds metadata information for a WhatsApp media directory.
 * Only lightweight properties are kept in memory to avoid
 * loading every file eagerly.
 */
data class DirectorySummary(
    val count: Int = 0,
    val totalBytes: Long = 0L,
    val formattedSize: String = "0 B"
)
