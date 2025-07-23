package com.d4rk.cleaner.app.clean.whatsapp.summary.domain.repository

import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary
import java.io.File

interface WhatsAppCleanerRepository {
    suspend fun getMediaSummary(): WhatsAppMediaSummary
    suspend fun listMediaFiles(type: String, offset: Int, limit: Int): List<File>
    suspend fun deleteFiles(files: List<File>)
}
