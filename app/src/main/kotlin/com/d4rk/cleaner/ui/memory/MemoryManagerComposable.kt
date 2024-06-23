package com.d4rk.cleaner.ui.memory

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

val StorageIcons = mapOf(
    "Installed Apps" to Icons.Outlined.Apps ,
    "System" to Icons.Outlined.Android ,
    "Music" to Icons.Outlined.MusicNote ,
    "Images" to Icons.Outlined.Image ,
    "Documents" to Icons.Outlined.FolderOpen ,
    "Downloads" to Icons.Outlined.Download ,
    "Other Files" to Icons.Outlined.FolderOpen
)

@Composable
fun MemoryManagerComposable() {
    val viewModel = viewModel<MemoryManagerViewModel>()
    val storageInfo by viewModel.storageInfo.collectAsState()
    val context = LocalContext.current

    var listExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.updateStorageInfo(context)
    }

    Column {
        StorageInfoCard(storageInfo = storageInfo)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.animateContentSize()) {
                    if (listExpanded) {
                        LazyColumn {
                            items(storageInfo.storageBreakdown.entries.toList()) { entry ->
                                StorageBreakdownItem(icon = entry.key , size = entry.value)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { listExpanded = ! listExpanded }) {
                Icon(
                    imageVector = if (listExpanded) Icons.Outlined.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft ,
                    contentDescription = if (listExpanded) "Collapse" else "Expand"
                )
            }
        }
    }
}

@Composable
fun StorageInfoCard(storageInfo : StorageInfo) {
    Card(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp) ,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Storage Information" ,
                style = MaterialTheme.typography.headlineSmall ,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { storageInfo.usedStorage.toFloat() / storageInfo.totalStorage.toFloat() } ,
                modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp) ,
                color = MaterialTheme.colorScheme.primary ,
            )
            Spacer(modifier = Modifier.height(8.dp))
            StorageInfoText(label = "Used:" , size = storageInfo.usedStorage)
            StorageInfoText(label = "Free:" , size = storageInfo.freeStorage)
            StorageInfoText(label = "Total:" , size = storageInfo.totalStorage)
        }
    }
}

@Composable
fun StorageBreakdownItem(icon : String , size : Long) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = StorageIcons[icon] ?: Icons.Filled.Info ,
            contentDescription = icon ,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Column {
            Text(
                text = icon ,
                style = MaterialTheme.typography.bodyMedium ,
                fontWeight = FontWeight.Bold
            )
            Text(text = formatSize(size) , style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun StorageInfoText(label : String , size : Long) {
    Text(text = "$label ${formatSize(size)}" , style = MaterialTheme.typography.bodyMedium)
}