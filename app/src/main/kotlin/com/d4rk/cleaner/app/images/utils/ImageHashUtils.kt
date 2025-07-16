package com.d4rk.cleaner.app.images.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import androidx.core.graphics.scale

object ImageHashUtils {
    fun perceptualHash(file: File): String? = runCatching {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null
        val resized = bitmap.scale(8, 8)
        val pixels = IntArray(64)
        resized.getPixels(pixels, 0, 8, 0, 0, 8, 8)
        var sum = 0
        val gray = IntArray(64)
        pixels.forEachIndexed { index, pixel ->
            val r = (pixel shr 16) and 0xff
            val g = (pixel shr 8) and 0xff
            val b = pixel and 0xff
            val lum = (r + g + b) / 3
            gray[index] = lum
            sum += lum
        }
        val avg = sum / 64
        var hash = 0L
        gray.forEachIndexed { index, lum ->
            if (lum >= avg) {
                hash = hash or (1L shl (63 - index))
            }
        }
        resized.recycle()
        bitmap.recycle()
        java.lang.Long.toHexString(hash)
    }.getOrNull()
}

