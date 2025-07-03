package com.d4rk.cleaner.app.clean.whatsappcleaner.domain.interface

import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.WhatsAppMediaSummary
import java.io.File

interface WhatsAppCleanerRepository {
    suspend fun getMediaSummary(): WhatsAppMediaSummary
    suspend fun deleteFiles(files: List<File>)
}
