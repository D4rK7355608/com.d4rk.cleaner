package com.d4rk.cleaner.ui.memory

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    val ramInfo by viewModel.ramInfo.collectAsState()
    val context = LocalContext.current

    var listExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.updateStorageInfo(context)
        viewModel.updateRamInfo(context)
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


        RamInfoCard(ramInfo)

        Spacer(modifier = Modifier.height(16.dp))
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
fun StorageBreakdownItem(icon: String, size: Long) {
    Card(
        modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), // Adjusted padding
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Increased padding for better visual balance
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filled Card for the Icon
            Card(
                modifier = Modifier.size(48.dp), // Adjust size as needed
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = StorageIcons[icon] ?: Icons.Filled.Info,
                        contentDescription = icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer // Use contrasting color
                    )
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 16.dp)) // Increased spacing

            Column {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = formatSize(size), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun StorageInfoText(label : String , size : Long) {
    Text(text = "$label ${formatSize(size)}" , style = MaterialTheme.typography.bodyMedium)
}


@Composable
fun RamInfoCard(ramInfo: RamInfo) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("RAM Information", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            StorageProgressBar(
                StorageInfo(
                    totalStorage = ramInfo.totalRam,
                    usedStorage = ramInfo.usedRam,
                    freeStorage = ramInfo.availableRam
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            StorageInfoText(label = "Used RAM:", size = ramInfo.usedRam)
            StorageInfoText(label = "Free RAM:", size = ramInfo.availableRam)
            StorageInfoText(label = "Total RAM:", size = ramInfo.totalRam)
        }
    }
}

@Composable
fun StorageProgressBar(storageInfo: StorageInfo) {
    val progress = (storageInfo.usedStorage.toFloat() / storageInfo.totalStorage.toFloat()).coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { progress } ,
        modifier = Modifier
                .fillMaxWidth()
                .height(8.dp) ,
        color = MaterialTheme.colorScheme.primary ,
    )
}