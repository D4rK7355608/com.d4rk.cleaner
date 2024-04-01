package com.d4rk.cleaner.ui.appmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import com.d4rk.cleaner.ui.appmanager.apps.ApksComposable
import com.d4rk.cleaner.ui.appmanager.apps.InstalledAppsComposable
import com.d4rk.cleaner.ui.appmanager.apps.SystemAppsComposable

@Composable
fun AppManagerComposable() {
    val tabs = listOf("Installed Apps", "System Apps", "App Install Files")
    val selectedIndex = remember { mutableIntStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedIndex.intValue) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedIndex.intValue == index,
                    onClick = { selectedIndex.intValue = index }
                )
            }
        }
        when (selectedIndex.intValue) {
            0 -> InstalledAppsComposable()
            1 -> SystemAppsComposable()
            2 -> ApksComposable()
        }
    }
}