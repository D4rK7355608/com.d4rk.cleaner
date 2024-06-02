package com.d4rk.cleaner.dialogs

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.R

@Composable
fun VersionInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        text = { VersionInfoContent() },
        confirmButton = {},
        dismissButton = {})
}

@Composable
fun VersionInfoContent() {
    val context = LocalContext.current
    val appName = context.getString(R.string.app_name)
    val version = String.format(context.getString(R.string.version), BuildConfig.VERSION_NAME)
    val copyright = context.getString(R.string.copyright)

    val appIcon = context.packageManager.getApplicationIcon(context.packageName)
    val bitmapDrawable = convertAdaptiveIconDrawableToBitmap(appIcon)

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            bitmap = bitmapDrawable.bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = version, style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = copyright, style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun convertAdaptiveIconDrawableToBitmap(drawable: Drawable): BitmapDrawable {
    return when (drawable) {
        is BitmapDrawable -> {
            drawable
        }

        is AdaptiveIconDrawable -> {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            BitmapDrawable(Resources.getSystem(), bitmap)
        }

        else -> {
            throw IllegalArgumentException("Unsupported drawable type")
        }
    }
}