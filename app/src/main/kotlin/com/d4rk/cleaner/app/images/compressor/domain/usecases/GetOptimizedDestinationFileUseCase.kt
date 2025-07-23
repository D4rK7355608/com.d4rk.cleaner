package com.d4rk.cleaner.app.images.compressor.domain.usecases

import android.os.Environment
import java.io.File

class GetOptimizedDestinationFileUseCase {
    operator fun invoke(originalFile: File): File {
        val picturesDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val optimizedDir = File(picturesDir, "Optimized images")
        if (!optimizedDir.exists()) {
            optimizedDir.mkdirs()
        }
        val fileExtension: String = originalFile.extension.ifEmpty { "jpg" }
        val fileName = "optimized_${System.currentTimeMillis()}.$fileExtension"
        return File(optimizedDir, fileName)
    }
}
