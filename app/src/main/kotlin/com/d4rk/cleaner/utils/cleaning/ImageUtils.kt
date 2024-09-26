package com.d4rk.cleaner.utils.cleaning

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.d4rk.cleaner.R

fun getFileIcon(extension: String, context: Context): Int {
    val lowercaseExtension: String = extension.lowercase()
    val resources: Resources = context.resources
    return when (lowercaseExtension) {
        in resources.getStringArray(R.array.apk_extensions) -> R.drawable.ic_apk_document
        in resources.getStringArray(R.array.image_extensions) -> R.drawable.ic_image
        in resources.getStringArray(R.array.video_extensions) -> R.drawable.ic_video_file
        in resources.getStringArray(R.array.audio_extensions) -> R.drawable.ic_audio_file
        in resources.getStringArray(R.array.archive_extensions) -> R.drawable.ic_archive_filter
        else -> R.drawable.ic_file_present
    }
}

fun Drawable.toBitmapDrawable(resources: Resources = Resources.getSystem()): BitmapDrawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        when (this) {
            is BitmapDrawable -> this
            is AdaptiveIconDrawable -> {
                val bitmap =
                    Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
                val bitmapDrawable = BitmapDrawable(resources, bitmap)
                return bitmapDrawable
            }

            else -> {
                BitmapDrawable(resources, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
            }
        }
    } else {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        BitmapDrawable(resources, bitmap)
    }
}