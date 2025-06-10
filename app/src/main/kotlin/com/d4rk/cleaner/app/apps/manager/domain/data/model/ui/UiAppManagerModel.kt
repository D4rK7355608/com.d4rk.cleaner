package com.d4rk.cleaner.app.apps.manager.domain.data.model.ui

import android.content.pm.ApplicationInfo
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo

data class UiAppManagerModel(
    val installedApps : List<ApplicationInfo> = emptyList() , val userAppsLoading : Boolean = true , val systemAppsLoading : Boolean = true , val apkFiles : List<ApkInfo> = emptyList() , val apkFilesLoading : Boolean = true
)