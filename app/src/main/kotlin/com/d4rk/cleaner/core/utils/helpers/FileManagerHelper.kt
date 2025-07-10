package com.d4rk.cleaner.core.utils.helpers

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import com.d4rk.cleaner.R
import java.io.File

object FileManagerHelper {
    fun openFolderOrSettings(context: Context, folder: File) {
        runCatching {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                folder
            )
            val intent = Intent(Intent.ACTION_VIEW).setDataAndType(uri, "*/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
            } else {
                val settingsIntent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
                if (settingsIntent.resolveActivity(packageManager) != null) {
                    context.startActivity(settingsIntent)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.no_application_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.onFailure {
            Toast.makeText(
                context,
                context.getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
