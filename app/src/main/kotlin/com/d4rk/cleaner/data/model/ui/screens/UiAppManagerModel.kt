package com.d4rk.cleaner.data.model.ui.screens

import android.content.pm.ApplicationInfo
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import com.d4rk.cleaner.data.model.ui.error.UiErrorModel

data class UiAppManagerModel(
    val installedApps: List<ApplicationInfo> = emptyList(),
    val apkFiles: List<ApkInfo> = emptyList(),
)