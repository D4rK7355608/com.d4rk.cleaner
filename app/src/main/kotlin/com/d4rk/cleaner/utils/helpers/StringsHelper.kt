package com.d4rk.cleaner.utils.helpers

import androidx.annotation.StringRes
import com.d4rk.cleaner.data.core.AppCoreManager

fun getStringResource(@StringRes id: Int): String {
    return AppCoreManager.instance.getString(id)
}