package com.d4rk.cleaner.core.utils.extensions

import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile
import java.security.MessageDigest

private const val PARTIAL_HASH_SIZE = 1 * 1024 * 1024 // 1MB

fun File.md5(): String? = runCatching {
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

fun File.partialMd5(): String? = runCatching {
    if (length() <= PARTIAL_HASH_SIZE * 2) {
        return md5()
    }

    val md = MessageDigest.getInstance("MD5")
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

    FileInputStream(this).use { stream ->
        var remaining = PARTIAL_HASH_SIZE
        while (remaining > 0) {
            val read = stream.read(buffer, 0, minOf(buffer.size, remaining))
            if (read <= 0) break
            md.update(buffer, 0, read)
            remaining -= read
        }
    }

    RandomAccessFile(this, "r").use { raf ->
        raf.seek(length() - PARTIAL_HASH_SIZE)
        var remaining = PARTIAL_HASH_SIZE
        while (remaining > 0) {
            val read = raf.read(buffer, 0, minOf(buffer.size, remaining))
            if (read <= 0) break
            md.update(buffer, 0, read)
            remaining -= read
        }
    }

    md.digest().joinToString("") { "%02x".format(it) }
}.getOrNull()
