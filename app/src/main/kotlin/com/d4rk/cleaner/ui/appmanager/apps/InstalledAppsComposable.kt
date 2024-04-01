package com.d4rk.cleaner.ui.appmanager.apps

import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun InstalledAppsComposable() {
    val apps = remember { mutableStateOf(listOf<ApplicationInfo>()) }
    LaunchedEffect(Unit) {
     //   apps.value = getInstalledApplications(PackageManager.GET_META_DATA).filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
    }
    //AppList(apps.value)
}