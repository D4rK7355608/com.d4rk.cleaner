package com.d4rk.cleaner.app.clean.whatsapp.utils.helpers

import android.content.Context
import com.d4rk.cleaner.core.utils.helpers.FileManagerHelper
import java.io.File

fun openFile(context: Context, file: File) {
    FileManagerHelper.openFile(context, file)
}