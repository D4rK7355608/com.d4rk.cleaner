package com.d4rk.cleaner.ui.home

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.dialogs.RescanAlertDialog
import com.d4rk.cleaner.utils.cleaning.getFileIcon
import com.d4rk.cleaner.utils.cleaning.getVideoThumbnail
import com.d4rk.cleaner.utils.compose.bounceClick
import com.d4rk.cleaner.utils.compose.components.CircularDeterminateIndicator
import com.d4rk.cleaner.utils.haptic.weakHapticFeedback
import com.google.common.io.Files.getFileExtension
import java.io.File

@Composable
fun HomeComposable() {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val viewModel: HomeViewModel = viewModel()
    val progress: Float by viewModel.progress.observeAsState(initial = 0.3f)
    val storageUsed: String by viewModel.storageUsed.observeAsState(initial = "0")
    val storageTotal: String by viewModel.storageTotal.observeAsState(initial = "0")
    val showCleaningComposable: Boolean by viewModel.showCleaningComposable.observeAsState(initial = false)
    val isAnalyzing: Boolean by viewModel.isAnalyzing.observeAsState(initial = false)
    val selectedFileCount: Int by viewModel.selectedFileCount.collectAsState()

    val imageLoader: ImageLoader = ImageLoader.Builder(context).memoryCache {
        MemoryCache.Builder(context).maxSizePercent(percent = 0.24).build()
    }.diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve(relative = "image_cache"))
            .maxSizePercent(percent = 0.02).build()
    }.build()

    val launchScanningKey: MutableState<Boolean> = remember { mutableStateOf(value = false) }

    if (viewModel.showRescanDialog.value) {
        RescanAlertDialog(onYes = {
            viewModel.rescan(
                context as Activity
            )
            view.weakHapticFeedback()
            viewModel.showRescanDialog.value = false
        }, onDismiss = {
            viewModel.showRescanDialog.value = false
            view.weakHapticFeedback()
        })
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
        ) {
            if (!showCleaningComposable) {
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
                AnalyzeComposable(launchScanningKey, imageLoader)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(102.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedVisibility(
                visible = showCleaningComposable,
                enter = fadeIn(animationSpec = tween(durationMillis = 400)) + expandHorizontally(
                    animationSpec = tween(durationMillis = 400), expandFrom = Alignment.Start
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 400)) + shrinkHorizontally(
                    animationSpec = tween(durationMillis = 400), shrinkTowards = Alignment.Start
                ),
                modifier = Modifier.weight(1f)
            ) {
                val enabled: Boolean = !isAnalyzing && selectedFileCount > 0

                val animateStateButtonColor: State<Color> = animateColorAsState(
                    targetValue = if (enabled) MaterialTheme.colorScheme.secondaryContainer else Color.LightGray,
                    animationSpec = tween(400, 0, LinearEasing),
                    label = "Button Color State Animation"
                )

                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .animateContentSize()
                        .padding(start = 16.dp, end = 8.dp)
                        .bounceClick(),
                    onClick = {
                        view.weakHapticFeedback()
                        viewModel.clean(activity = context as Activity)
                    },
                    shape = MaterialTheme.shapes.medium,
                    enabled = enabled,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = animateStateButtonColor.value,
                    ),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(ButtonDefaults.ContentPadding)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_broom),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Text(
                            text = stringResource(R.string.clean),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            FilledTonalButton(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .animateContentSize()
                .padding(start = if (showCleaningComposable) 8.dp else 16.dp, end = 16.dp)
                .bounceClick(), onClick = {
                view.weakHapticFeedback()
                viewModel.analyze(activity = context as Activity)
            }, shape = MaterialTheme.shapes.medium
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
                    Text(
                        text = stringResource(R.string.analyze),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Composable function representing the analyze screen displaying a list of files to clean.
 *
 * This composable displays a list of files within an outlined card, each represented by a cleaning item.
 * The user can view and interact with the list of files for cleaning, including selecting and deselecting individual files and selecting or deselecting all files.
 *
 * @param viewModel The HomeViewModel instance used to interact with the data and business logic.
 */
@Composable
fun AnalyzeComposable(launchScanningKey: MutableState<Boolean>, imageLoader: ImageLoader) {
    val viewModel: HomeViewModel = viewModel()
    val files: List<File> by viewModel.scannedFiles.asFlow().collectAsState(initial = listOf())
    val isAnalyzing: Boolean by viewModel.isAnalyzing.observeAsState(initial = false)
    val allFilesSelected: Boolean by viewModel.allFilesSelected
    val selectedFileCount: Int by viewModel.selectedFileCount.collectAsState()

    LaunchedEffect(key1 = launchScanningKey.value) {
        viewModel.fileScanner.startScanning()
        launchScanningKey.value = false
    }

    LaunchedEffect(Unit) {
        viewModel.fileScanner.startScanning()
    }

    Column(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (isAnalyzing && files.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp),
                ) {
                    items(items = files, key = { file -> file.absolutePath }) { file ->
                        FileCard(file = file, viewModel = viewModel, imageLoader = imageLoader)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val statusText: String = if (selectedFileCount > 0) {
                stringResource(id = R.string.status_selected_files, selectedFileCount)
            } else {
                stringResource(id = R.string.status_no_files_selected)
            }
            val statusColor: Color by animateColorAsState(
                targetValue = if (selectedFileCount > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }, animationSpec = tween(), label = "Selected Files Status Color Animation"
            )

            Text(
                text = statusText, color = statusColor, modifier = Modifier.animateContentSize()
            )
            SelectAllComposable(
                checked = allFilesSelected,
                onCheckedChange = { viewModel.selectAllFiles(it) },
            )
        }
    }
}

@Composable
fun FileCard(file: File, viewModel: HomeViewModel, imageLoader: ImageLoader) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val fileExtension: String = getFileExtension(file.name)

    var thumbnail: Bitmap? by remember(file.absolutePath) { mutableStateOf(value = null) }

    LaunchedEffect(file.absolutePath) {
        thumbnail = getVideoThumbnail(file.absolutePath)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 1f)
            .bounceClick(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (fileExtension) {
                in context.resources.getStringArray(R.array.image_extensions).toList() -> {
                    AsyncImage(
                        model = remember(file) {
                            ImageRequest.Builder(context).data(file).size(64)
                                .crossfade(enable = true).build()
                        },
                        imageLoader = imageLoader,
                        contentDescription = file.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                in context.resources.getStringArray(R.array.video_extensions).toList() -> {
                    if (thumbnail != null) {
                        Image(
                            bitmap = thumbnail!!.asImageBitmap(),
                            contentDescription = file.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_video_file),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                else -> {
                    Icon(
                        painter = painterResource(getFileIcon(fileExtension, context)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Checkbox(
                checked = viewModel.fileSelectionStates[file] ?: false,
                onCheckedChange = { isChecked ->
                    view.weakHapticFeedback()
                    viewModel.onFileSelectionChange(file, isChecked)
                },
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Text(
                text = file.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Black.copy(alpha = 0.4f)
                    )
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Composable function for selecting or deselecting all items.
 *
 * This composable displays a filter chip labeled "Select All". When tapped, it toggles the
 * selection state and invokes the `onCheckedChange` callback.
 *
 * @param checked A boolean value indicating whether all items are currently selected.
 * @param onCheckedChange A callback function that is invoked when the user taps the chip to change the selection state.
 */
@Composable
fun SelectAllComposable(
    checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    val view: View = LocalView.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
        FilterChip(
            modifier = Modifier.bounceClick(),
            selected = checked,
            onClick = {
                view.weakHapticFeedback()
                onCheckedChange(!checked)
            },
            label = { Text(stringResource(id = R.string.select_all)) },
            leadingIcon = {
                AnimatedContent(
                    targetState = checked,
                    label = "Checkmark Animation"
                ) { targetChecked ->
                    if (targetChecked) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                        )
                    }
                }
            },
            interactionSource = interactionSource,
        )
    }
}