package com.d4rk.cleaner.app.clean.whatsappcleaner.data

import android.app.Application
import android.os.Environment
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.interface.WhatsAppCleanerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class WhatsAppCleanerRepositoryImpl(private val application: Application) : WhatsAppCleanerRepository {

    private fun getWhatsAppMediaDir(): File {
        val scoped = File(Environment.getExternalStorageDirectory(),
            "Android/media/com.whatsapp/WhatsApp/Media")
        val legacy = File(Environment.getExternalStorageDirectory(),
            "WhatsApp/Media")
        return when {
            scoped.exists() -> scoped
            legacy.exists() -> legacy
            else -> scoped
        }
    }

    override suspend fun getMediaSummary(): WhatsAppMediaSummary = withContext(Dispatchers.IO) {
        val base = getWhatsAppMediaDir()
        val images = File(base, "WhatsApp Images").listFiles()?.filter { it.isFile } ?: emptyList()
        val videos = File(base, "WhatsApp Video").listFiles()?.filter { it.isFile } ?: emptyList()
        val docs = File(base, "WhatsApp Documents").listFiles()?.filter { it.isFile } ?: emptyList()
        WhatsAppMediaSummary(images = images, videos = videos, documents = docs)
    }

    override suspend fun deleteFiles(files: List<File>) = withContext(Dispatchers.IO) {
        files.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }
    }
}
