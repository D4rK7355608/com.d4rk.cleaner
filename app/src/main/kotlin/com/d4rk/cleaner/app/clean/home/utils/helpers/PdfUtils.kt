package com.d4rk.cleaner.app.clean.home.utils.helpers

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import java.io.File

fun loadPdfThumbnail(file: File): Bitmap? {
    return runCatching {
        val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(descriptor)
        val page = renderer.openPage(0)
        val bitmap = createBitmap(page.width, page.height).apply {
            page.render(this, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        }
        page.close()
        renderer.close()
        descriptor.close()
        bitmap
    }.getOrNull()
}
