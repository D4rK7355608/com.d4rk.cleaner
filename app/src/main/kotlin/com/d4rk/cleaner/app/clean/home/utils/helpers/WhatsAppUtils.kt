package com.d4rk.cleaner.app.clean.home.utils.helpers

import android.os.Environment
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
    val images = list("WhatsApp Images")
    val videos = list("WhatsApp Video")
    val docs = list("WhatsApp Documents")
    return Triple(images, videos, docs)
}
