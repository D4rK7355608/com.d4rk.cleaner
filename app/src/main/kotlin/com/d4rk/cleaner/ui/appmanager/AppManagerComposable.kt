package com.d4rk.cleaner.ui.appmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.ui.appmanager.apps.ApksComposable
import com.d4rk.cleaner.ui.appmanager.apps.InstalledAppsComposable
import com.d4rk.cleaner.ui.appmanager.apps.SystemAppsComposable

@Composable
fun AppManagerComposable() {
    val tabs = listOf("Installed Apps", "System Apps", "App Install Files")
    var selectedIndex by remember { mutableIntStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = selectedIndex,
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
                    )
                }
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(text = title , maxLines = 1 , overflow = TextOverflow.Ellipsis , color = MaterialTheme.colorScheme.onSurface) } ,
                    selected = selectedIndex == index ,
                    onClick = { selectedIndex = index }
                )
            }
        }
        when (selectedIndex) {
            0 -> InstalledAppsComposable()
            1 -> SystemAppsComposable()
            2 -> ApksComposable()
        }
    }
}