package com.d4rk.cleaner.core.utils.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build

fun ClipboardManager.clearClipboardCompat() {
    runCatching {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                clearPrimaryClip()
            }

            else -> {
                val emptyClip = ClipData.newPlainText("", "")
                setPrimaryClip(emptyClip)
            }
        }
    }
}
