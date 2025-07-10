package com.d4rk.cleaner.app.clean.clipboard.ui

import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.utils.extensions.clearClipboardCompat

class ClipboardCleanerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        manager?.clearClipboardCompat()
        Toast.makeText(this, R.string.clipboard_cleaned, Toast.LENGTH_SHORT).show()
        finishAndRemoveTask()
    }
}