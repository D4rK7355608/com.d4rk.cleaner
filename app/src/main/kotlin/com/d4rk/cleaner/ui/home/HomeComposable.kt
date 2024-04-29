package com.d4rk.cleaner.ui.home

import android.content.Context
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.startup.StartupActivity
import com.d4rk.cleaner.utils.Utils
import com.google.common.io.Files.getFileExtension

@Composable
fun HomeComposable() {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val progress by viewModel.progress.observeAsState(0.3f)
    val storageUsed by viewModel.storageUsed.observeAsState("0")
    val storageTotal by viewModel.storageTotal.observeAsState("0")
    val showCleaningComposable = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
        ) {
            if (!showCleaningComposable.value) {
                CircularDeterminateIndicator(
                    progress = progress,
                    storageUsed = storageUsed,
                    storageTotal = storageTotal,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 98.dp)
                )
                Image(
                    painter = painterResource(R.drawable.ic_clean),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .size(128.dp, 66.dp)
                )
            } else {
                AnalyzeComposable()
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(102.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilledTonalButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 8.dp),
                onClick = {
                    Utils.openActivity(
                        context, StartupActivity::class.java
                    )
                },
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ButtonDefaults.ContentPadding),
                ) {
                    Icon(
                        painterResource(R.drawable.ic_broom),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Text(text = "Clean", style = MaterialTheme.typography.bodyMedium)
                }
            }
            FilledTonalButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 8.dp, end = 16.dp),
                onClick = {
                    viewModel.analyze()
                    showCleaningComposable.value = true
                },
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ButtonDefaults.ContentPadding)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Text(text = "Analyze", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

/**
 * Composable function representing a circular determinate progress indicator with storage information.
 *
 * This composable displays a circular progress indicator representing a determinate progress value.
 * It also shows storage usage information (used/total) in gigabytes (GB).
 *
 * @param progress The progress value as a float, representing the completion percentage of the progress indicator.
 * @param storageUsed The amount of storage used, formatted as a string (e.g., "2.5 GB").
 * @param storageTotal The total amount of storage, formatted as a string (e.g., "10 GB").
 * @param modifier The modifier for styling and layout customization.
 */
@Composable
fun CircularDeterminateIndicator(
    progress: Float, storageUsed: String, storageTotal: String, modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = ""
    )

    Box(
        contentAlignment = Alignment.Center, modifier = modifier.size(240.dp)
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer,
            strokeWidth = 6.dp,
        )
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = "$storageUsed/$storageTotal GB \n Used",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

/**
 * Composable function representing the analyze screen displaying a list of files to clean.
 *
 * This composable displays a list of files within an outlined card, each represented by a cleaning item.
 * The user can view and interact with the list of files for cleaning.
 */
@Composable
fun AnalyzeComposable() {
    val viewModel: HomeViewModel = viewModel()
    val files by viewModel.scannedFiles.asFlow().collectAsState(initial = listOf())
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(files) { file ->
                        FileItemComposable(
                            item = file.name,
                            isChecked = false,
                            onCheckedChange = {

                            },
                            context = LocalContext.current
                        )
                    }
                }
            }
        }
        Text(
            text = "Status",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

/**
 * Composable function representing an item in a cleaning list with an icon, text label, and checkbox.
 *
 * This composable displays a row containing an icon, text label, and checkbox for a given cleaning list item.
 *
 * @param item The text label to display for the cleaning item.
 * @param isChecked The state of the checkbox indicating whether the item is selected or not.
 * @param onCheckedChange A callback that is triggered when the checkbox state changes.
 *                        The new state of the checkbox (`isChecked`) is provided as a parameter to this callback.
 * @param context The Android `Context` used to access resources like file extensions and corresponding icons.
 */
@Composable
fun FileItemComposable(
    item: String = "",
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    context: Context
) {

    val fileIconMap = mutableMapOf<String, Int>()

    context.resources.getStringArray(R.array.apk_extensions).forEach {
        fileIconMap[it] = R.drawable.ic_apk_document
    }

    context.resources.getStringArray(R.array.archive_extensions).forEach {
        fileIconMap[it] = R.drawable.ic_archive_filter
    }

    context.resources.getStringArray(R.array.audio_extensions).forEach {
        fileIconMap[it] = R.drawable.ic_audio_file
    }

    context.resources.getStringArray(R.array.video_extensions).forEach {
        fileIconMap[it] = R.drawable.ic_video_file
    }

    fileIconMap["nomedia"] = R.drawable.ic_draft
    fileIconMap["vcf"] = R.drawable.ic_contact_page

    val fileExtension = getFileExtension(item)
    val iconResource = fileIconMap[fileExtension] ?: R.drawable.ic_file_present

    val rememberedIsChecked = remember { mutableStateOf(isChecked) }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = item,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Checkbox(
            checked = rememberedIsChecked.value,
            onCheckedChange = {
                rememberedIsChecked.value = it
                onCheckedChange(it)
            },
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}