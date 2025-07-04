package com.d4rk.cleaner.app.clean.scanner.utils.helpers

import android.os.Environment
import com.d4rk.cleaner.app.clean.whatsapp.utils.constants.WhatsAppMediaConstants
import java.io.File

fun getWhatsAppMediaDirs(): File? {
    val legacy = File(Environment.getExternalStorageDirectory(), "WhatsApp/Media")
    if (legacy.exists()) return legacy
    val scoped = File(Environment.getExternalStorageDirectory(), "Android/media/com.whatsapp/WhatsApp/Media")
    return scoped.takeIf { it.exists() }
}

fun getWhatsAppMediaSummary(): Triple<List<File>, List<File>, List<File>> {
    val mediaDir = getWhatsAppMediaDirs() ?: return Triple(emptyList(), emptyList(), emptyList())
    fun list(dirName: String): List<File> {
        val dir = File(mediaDir, dirName)
        return dir.listFiles()?.filter { it.isFile && !it.name.startsWith(".") }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    val images = list(WhatsAppMediaConstants.DIRECTORIES[WhatsAppMediaConstants.IMAGES]!!)
    val videos = list(WhatsAppMediaConstants.DIRECTORIES[WhatsAppMediaConstants.VIDEOS]!!)
    val docs = list(WhatsAppMediaConstants.DIRECTORIES[WhatsAppMediaConstants.DOCUMENTS]!!)
    return Triple(images, videos, docs)
}
