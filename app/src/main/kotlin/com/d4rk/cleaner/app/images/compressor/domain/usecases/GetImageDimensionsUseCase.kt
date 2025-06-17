package com.d4rk.cleaner.app.images.compressor.domain.usecases

import android.graphics.BitmapFactory
import java.io.File

class GetImageDimensionsUseCase {
    operator fun invoke(file: File): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        return Pair(options.outWidth, options.outHeight)
    }
}
