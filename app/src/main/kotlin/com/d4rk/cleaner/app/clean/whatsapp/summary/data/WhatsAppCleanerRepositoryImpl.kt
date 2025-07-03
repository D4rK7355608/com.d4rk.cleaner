package com.d4rk.cleaner.app.clean.whatsapp.summary.data

import android.app.Application
import android.os.Environment
import android.text.format.Formatter
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.DirectorySummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.WhatsAppMediaSummary
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.repository.WhatsAppCleanerRepository
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

        fun collect(name: String): DirectorySummary {
            val dir = File(base, name)
            if (!dir.exists()) return DirectorySummary()
            val files = dir.walkTopDown()
                .filter { it.isFile && it.name != ".nomedia" }
                .toList()
            val size = files.sumOf { it.length() }
            val formatted = Formatter.formatFileSize(application, size)
            return DirectorySummary(files, size, formatted)
        }

        val images = collect("WhatsApp Images")
        val videos = collect("WhatsApp Video")
        val docs = collect("WhatsApp Documents")
        val audios = collect("WhatsApp Audio")
        val statuses = collect(".Statuses")
        val voiceNotes = collect("WhatsApp Voice Notes")
        val videoNotes = collect("WhatsApp Video Notes")
        val gifs = collect("WhatsApp Animated Gifs")
        val wallpapers = collect("WallPaper")
        val stickers = collect("WhatsApp Stickers")
        val profile = collect("WhatsApp Profile Photos")

        val totalSize = listOf(
            images,
            videos,
            docs,
            audios,
            statuses,
            voiceNotes,
            videoNotes,
            gifs,
            wallpapers,
            stickers,
            profile,
        ).sumOf { it.totalBytes }
        val totalFormatted = Formatter.formatFileSize(application, totalSize)

        WhatsAppMediaSummary(
            images = images,
            videos = videos,
            documents = docs,
            audios = audios,
            statuses = statuses,
            voiceNotes = voiceNotes,
            videoNotes = videoNotes,
            gifs = gifs,
            wallpapers = wallpapers,
            stickers = stickers,
            profilePhotos = profile,
            formattedTotalSize = totalFormatted,
        )
    }

    override suspend fun deleteFiles(files: List<File>) = withContext(Dispatchers.IO) {
        files.forEach { file ->
            if (file.exists()) file.deleteRecursively()
        }
    }
}
