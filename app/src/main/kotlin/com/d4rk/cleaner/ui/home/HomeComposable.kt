package com.d4rk.cleaner.ui.home

import android.app.Activity
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.d4rk.cleaner.R
import com.d4rk.cleaner.utils.CircularDeterminateIndicator
import com.d4rk.cleaner.utils.bounceClick
import com.d4rk.cleaner.utils.getFileIcon
import com.d4rk.cleaner.utils.getVideoThumbnail
import com.google.common.io.Files.getFileExtension
import java.io.File

@Composable
fun HomeComposable() {
    val context = LocalContext.current
    val viewModel : HomeViewModel = viewModel()
    val progress by viewModel.progress.observeAsState(0.3f)
    val storageUsed by viewModel.storageUsed.observeAsState("0")
    val storageTotal by viewModel.storageTotal.observeAsState("0")
    val showCleaningComposable by viewModel.showCleaningComposable.observeAsState(false)
    val isAnalyzing by viewModel.isAnalyzing.observeAsState(false)
    val selectedFileCount by viewModel.selectedFileCount.collectAsState()

    val launchScanningKey = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
        ) {
            if (! showCleaningComposable) {
                CircularDeterminateIndicator(
                    progress = progress ,
                    storageUsed = storageUsed ,
                    storageTotal = storageTotal ,
                    modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 98.dp)
                )
                Image(
                    painter = painterResource(R.drawable.ic_clean) ,
                    contentDescription = null ,
                    modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(24.dp)
                            .size(128.dp , 66.dp)
                )
            }
            else {
                AnalyzeComposable(launchScanningKey)
            }
        }
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp)
                    .padding(bottom = 16.dp) ,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedVisibility(
                visible = showCleaningComposable ,
                enter = fadeIn(animationSpec = tween(durationMillis = 400)) + expandHorizontally(
                    animationSpec = tween(durationMillis = 400) , expandFrom = Alignment.Start
                ) ,
                exit = fadeOut(animationSpec = tween(durationMillis = 400)) + shrinkHorizontally(
                    animationSpec = tween(durationMillis = 400) , shrinkTowards = Alignment.Start
                ) ,
                modifier = Modifier.weight(1f)
            ) {
                val enabled = ! isAnalyzing && selectedFileCount > 0

                val animateStateButtonColor = animateColorAsState(
                    targetValue = if (enabled) MaterialTheme.colorScheme.secondaryContainer else Color.LightGray ,
                    animationSpec = tween(400 , 0 , LinearEasing) ,
                    label = ""
                )

                FilledTonalButton(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .animateContentSize()
                            .padding(start = 16.dp , end = 8.dp)
                            .bounceClick() ,
                    onClick = {
                        viewModel.clean(activity = context as Activity)
                    } ,
                    shape = MaterialTheme.shapes.medium ,
                    enabled = enabled ,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = animateStateButtonColor.value ,
                    ) ,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally ,
                        verticalArrangement = Arrangement.Center ,
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(ButtonDefaults.ContentPadding)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_broom) ,
                            contentDescription = null ,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Text(text = "Clean" , style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            FilledTonalButton(
                modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .animateContentSize()
                        .padding(start = if (showCleaningComposable) 8.dp else 16.dp , end = 16.dp)
                        .bounceClick() , onClick = {
                    if (! showCleaningComposable) {
                        viewModel.analyze(activity = context as Activity)
                    }
                } , shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally ,
                    verticalArrangement = Arrangement.Center ,
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(ButtonDefaults.ContentPadding)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_search) ,
                        contentDescription = null ,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Text(text = "Analyze" , style = MaterialTheme.typography.bodyMedium)
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
fun AnalyzeComposable(launchScanningKey: MutableState<Boolean>) {
    val viewModel : HomeViewModel = viewModel()
    val files by viewModel.scannedFiles.asFlow().collectAsState(initial = listOf())

    val allFilesSelected by viewModel.allFilesSelected
    val selectedFileCount by viewModel.selectedFileCount.collectAsState()

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
                .padding(16.dp) ,
        horizontalAlignment = Alignment.End
    ) {
        OutlinedCard(
            modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth() ,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3) ,
                verticalArrangement = Arrangement.spacedBy(8.dp) ,
                horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                modifier = Modifier.padding(8.dp)
            ) {
                items(files) { file ->
                    FileCard(file = file , viewModel = viewModel) // FIXME: Type mismatch: inferred type is Int but File was expected
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth() ,
            verticalAlignment = Alignment.CenterVertically ,
            horizontalArrangement = Arrangement.SpaceBetween ,
        ) {
            val statusText = if (selectedFileCount > 0) {
                "Status: Selected $selectedFileCount files"
            }
            else {
                "Status: No files selected"
            }
            val statusColor by animateColorAsState(
                targetValue = if (selectedFileCount > 0) {
                    MaterialTheme.colorScheme.primary
                }
                else {
                    MaterialTheme.colorScheme.secondary
                } , animationSpec = tween() , label = ""
            )

            Text(
                text = statusText , color = statusColor , modifier = Modifier.animateContentSize()
            )
            SelectAllComposable(
                checked = allFilesSelected ,
                onCheckedChange = { viewModel.selectAllFiles(it) } ,
            )
        }
    }
}

@Composable
fun FileCard(file: File, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val fileExtension = getFileExtension(file.name)
    val thumbnail = remember {
        getVideoThumbnail(file.absolutePath, thumbnailWidth = 64, thumbnailHeight = 64)
    }
    Card(
        modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .bounceClick() ,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (fileExtension) {
                in context.resources.getStringArray(R.array.image_extensions).toList() -> {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(file)
                                .crossfade(true).build() ,
                        contentDescription = file.name ,
                        contentScale = ContentScale.Crop ,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                in context.resources.getStringArray(R.array.video_extensions).toList() -> {
                    if (thumbnail != null) {
                        Image(
                            bitmap = thumbnail.asImageBitmap(),
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
                        painter = painterResource(getFileIcon(fileExtension , context)) ,
                        contentDescription = null ,
                        modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                    )
                }
            }

            Checkbox(
                checked = viewModel.fileSelectionStates[file] ?: false ,
                onCheckedChange = { isChecked ->
                    viewModel.fileSelectionStates[file] = isChecked
                    viewModel._selectedFileCount.value =
                            viewModel.fileSelectionStates.values.count { it }
                    viewModel.allFilesSelected.value =
                            viewModel.fileSelectionStates.values.all { it }
                } ,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Text(
                text = file.name ,
                maxLines = 1 ,
                overflow = TextOverflow.Ellipsis ,
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Black.copy(alpha = 0.5f)
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
    checked : Boolean , onCheckedChange : (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .animateContentSize() ,
        verticalAlignment = Alignment.CenterVertically ,
        horizontalArrangement = Arrangement.End
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        FilterChip(
            modifier = Modifier.bounceClick() ,
            selected = checked ,
            onClick = {
                onCheckedChange(! checked)
            } ,
            label = { Text("Select All") } ,
            leadingIcon = {
                AnimatedContent(targetState = checked , label = "") { targetChecked ->
                    if (targetChecked) {
                        Icon(
                            imageVector = Icons.Filled.Check ,
                            contentDescription = null ,
                        )
                    }
                }
            } ,
            interactionSource = interactionSource ,
        )
    }
}