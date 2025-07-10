package com.d4rk.cleaner.core.utils.helpers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import com.d4rk.cleaner.R
import java.io.File

object FileManagerHelper {
    fun openFile(context: Context, file: File) {
        runCatching {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                file
            )
            val mime = context.contentResolver.getType(uri) ?: "*/*"
            val intent = Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }.onFailure { exception ->
            when (exception) {
                is ActivityNotFoundException -> Toast.makeText(
                    context,
                    context.getString(R.string.no_application_found),
                    Toast.LENGTH_SHORT
                ).show()
                is IllegalArgumentException -> Toast.makeText(
                    context,
                    context.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
                else -> throw exception
            }
        }
    }
    fun openFolderOrSettings(context: Context, folder: File) {
        runCatching {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                folder
            )

            val baseIntent = Intent(Intent.ACTION_VIEW).setDataAndType(uri, "*/*")
            baseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val pm = context.packageManager

            // Try common file explorer packages first (Files by Google, Samsung, Xiaomi)
            val explorerPackages = listOf(
                "com.google.android.apps.nbu.files", // Files by Google
                "com.android.documentsui", // AOSP/Pixel
                "com.sec.android.app.myfiles", // Samsung
                "com.mi.android.fileexplorer" // Xiaomi
            )

            var started = false
            for (pkg in explorerPackages) {
                val intent = Intent(baseIntent).setPackage(pkg)
                if (intent.resolveActivity(pm) != null) {
                    context.startActivity(intent)
                    started = true
                    break
                }
            }

            if (!started && baseIntent.resolveActivity(pm) != null) {
                context.startActivity(baseIntent)
                started = true
            }

            if (!started) {
                val settingsIntent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
                if (settingsIntent.resolveActivity(pm) != null) {
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
