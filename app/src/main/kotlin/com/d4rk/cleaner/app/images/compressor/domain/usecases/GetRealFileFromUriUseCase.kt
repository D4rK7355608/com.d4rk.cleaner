package com.d4rk.cleaner.app.images.compressor.domain.usecases

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GetRealFileFromUriUseCase(private val context: Context) {
    suspend operator fun invoke(uri: Uri): File? = withContext(Dispatchers.IO) {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileName = cursor.getString(nameIndex)
                    val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
                    val file = File(context.cacheDir, sanitizedFileName)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    return@withContext file
                }
            }
        } else if (uri.scheme == "file") {
            return@withContext File(uri.path!!)
        }
        null
    }
}
