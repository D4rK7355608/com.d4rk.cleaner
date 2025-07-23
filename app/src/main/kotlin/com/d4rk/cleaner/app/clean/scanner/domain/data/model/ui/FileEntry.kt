package com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui

import java.io.File

/** Lightweight representation of a discovered file. */
data class FileEntry(
    val path: String,
    val size: Long = 0L,
    val modified: Long = 0L
) {
    fun toFile(): File = File(path)
}
