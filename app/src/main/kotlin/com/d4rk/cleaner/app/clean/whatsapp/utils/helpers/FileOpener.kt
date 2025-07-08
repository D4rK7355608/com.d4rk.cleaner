package com.d4rk.cleaner.app.clean.whatsapp.utils.helpers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.d4rk.cleaner.R
import java.io.File

fun openFile(context: Context, file: File) {
    runCatching {
        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime ?: "*/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> Toast.makeText(context, context.getString(R.string.no_application_found), Toast.LENGTH_SHORT).show()
            is IllegalArgumentException -> Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            else -> throw exception
        }
    }
}