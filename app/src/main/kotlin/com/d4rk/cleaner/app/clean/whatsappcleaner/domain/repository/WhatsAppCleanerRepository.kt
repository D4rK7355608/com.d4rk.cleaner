package com.d4rk.cleaner.app.clean.whatsappcleaner.domain.repository

import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.model.WhatsAppMediaSummary
import java.io.File

interface WhatsAppCleanerRepository {
    suspend fun getMediaSummary(): WhatsAppMediaSummary
    suspend fun deleteFiles(files: List<File>)
}
