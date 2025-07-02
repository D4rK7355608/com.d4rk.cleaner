package com.d4rk.cleaner.app.clean.scanner.utils.helpers

import java.io.File
import java.security.MessageDigest

/**
 * Groups duplicate files by their original version.
 * Each returned list starts with the oldest file (considered the original)
 * followed by its duplicates.
 */
fun groupDuplicatesByOriginal(files: List<File>): List<List<File>> {
    val hashMap = mutableMapOf<String, MutableList<File>>()
    files.filter { it.isFile }.forEach { file ->
        val hash = file.md5() ?: return@forEach
        hashMap.getOrPut(hash) { mutableListOf() }.add(file)
    }
    val seenHashes = mutableSetOf<String>()
    val groups = mutableListOf<List<File>>()
    files.forEach { file ->
        val hash = file.md5() ?: return@forEach
        if (seenHashes.add(hash)) {
            val group = hashMap[hash]?.sortedBy { it.lastModified() } ?: listOf(file)
            groups.add(group)
        }
    }
    return groups
}

private fun File.md5(): String? = runCatching {
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    val md = MessageDigest.getInstance("MD5")
    inputStream().use { stream ->
        var read = stream.read(buffer)
        while (read > 0) {
            md.update(buffer, 0, read)
            read = stream.read(buffer)
        }
    }
    md.digest().joinToString("") { "%02x".format(it) }
}.getOrNull()
