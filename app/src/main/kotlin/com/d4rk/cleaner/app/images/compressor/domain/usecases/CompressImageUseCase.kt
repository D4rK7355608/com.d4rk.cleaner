package com.d4rk.cleaner.app.images.compressor.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CompressImageUseCase(private val context: Context) {
    suspend operator fun invoke(
        originalFile: File,
        quality: Int,
        targetWidth: Int,
        targetHeight: Int,
        desiredSizeBytes: Long? = null,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): File = withContext(Dispatchers.IO) {
        if (desiredSizeBytes != null) {
            compressImageToTargetSize(originalFile, targetWidth, targetHeight, desiredSizeBytes, format)
        } else {
            compressImageUsingNative(originalFile, quality, targetWidth, targetHeight, format)
        }
    }

    private fun compressImageToTargetSize(
        originalFile: File,
        targetWidth: Int,
        targetHeight: Int,
        desiredSizeBytes: Long,
        format: Bitmap.CompressFormat
    ): File {
        val originalBitmap = BitmapFactory.decodeFile(originalFile.absolutePath)

        if (originalFile.length() <= desiredSizeBytes) {
            return originalFile
        }

        val scaledBitmap = originalBitmap.scale(targetWidth, targetHeight)

        var minQuality = 10
        var maxQuality = 100
        var finalQuality = maxQuality
        var compressedFile: File

        while (minQuality <= maxQuality) {
            finalQuality = (minQuality + maxQuality) / 2
            compressedFile = File(context.cacheDir, "temp_compressed.jpg")
            compressedFile.outputStream().use { out ->
                scaledBitmap.compress(format, finalQuality, out)
            }
            val fileSize = compressedFile.length()
            if (fileSize in (desiredSizeBytes * 95 / 100)..(desiredSizeBytes * 105 / 100)) {
                break
            }
            if (fileSize > desiredSizeBytes) {
                maxQuality = finalQuality - 1
            } else {
                minQuality = finalQuality + 1
            }
        }
        compressedFile = File(context.cacheDir, "native_compressed_${System.currentTimeMillis()}.jpg")
        compressedFile.outputStream().use { out ->
            scaledBitmap.compress(format, finalQuality, out)
        }
        return compressedFile
    }

    private fun compressImageUsingNative(
        originalFile: File,
        quality: Int,
        targetWidth: Int,
        targetHeight: Int,
        format: Bitmap.CompressFormat
    ): File {
        val originalBitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
        val scaledBitmap = originalBitmap.scale(targetWidth, targetHeight)
        val outputFile = File(context.cacheDir, "native_compressed_${System.currentTimeMillis()}.jpg")
        outputFile.outputStream().use { out ->
            scaledBitmap.compress(format, quality, out)
        }
        return outputFile
    }
}
