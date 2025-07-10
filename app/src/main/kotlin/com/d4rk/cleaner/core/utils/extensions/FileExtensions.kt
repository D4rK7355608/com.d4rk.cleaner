package com.d4rk.cleaner.core.utils.extensions

import java.io.File
import java.security.MessageDigest

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
