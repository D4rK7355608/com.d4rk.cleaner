package com.d4rk.cleaner.app.clean.scanner.utils.helpers

import java.io.File
import com.d4rk.cleaner.core.utils.extensions.partialMd5

/**
 * Groups duplicate files by their original version.
 * Each returned list starts with the oldest file (considered the original)
 * followed by its duplicates.
 */
fun groupDuplicatesByOriginal(files: List<File>): List<List<File>> {
    val hashMap = mutableMapOf<String, MutableList<File>>()
    files.filter { it.isFile }.forEach { file ->
        val hash = file.partialMd5() ?: return@forEach
        hashMap.getOrPut(hash) { mutableListOf() }.add(file)
    }
    val seenHashes = mutableSetOf<String>()
    val groups = mutableListOf<List<File>>()
    files.forEach { file ->
        val hash = file.partialMd5() ?: return@forEach
        if (seenHashes.add(hash)) {
            val group = hashMap[hash]?.sortedBy { it.lastModified() } ?: listOf(file)
            groups.add(group)
        }
    }
    return groups
}
