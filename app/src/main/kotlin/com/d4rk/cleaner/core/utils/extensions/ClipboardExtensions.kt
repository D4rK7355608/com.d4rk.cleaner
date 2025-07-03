package com.d4rk.cleaner.core.utils.extensions

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Build
import android.os.PersistableBundle

fun ClipboardManager.clearClipboardCompat() {
    runCatching {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                clearPrimaryClip()
            }
            else -> {
                val emptyClip = ClipData.newPlainText("", "")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    emptyClip.description.extras = PersistableBundle().apply {
                        putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                    }
                }
                setPrimaryClip(emptyClip)
            }
        }
    }
}
