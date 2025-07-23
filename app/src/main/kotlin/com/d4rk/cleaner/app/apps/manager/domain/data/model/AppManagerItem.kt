package com.d4rk.cleaner.app.apps.manager.domain.data.model

sealed class AppManagerItem {
    data class InstalledApp(val packageName: String) : AppManagerItem()
    data class ApkFile(val path: String) : AppManagerItem()
}