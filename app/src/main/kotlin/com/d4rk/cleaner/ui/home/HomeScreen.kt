package com.d4rk.cleaner.ui.home

import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.error.UiErrorModel
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.dialogs.ErrorAlertDialog
import com.d4rk.cleaner.ui.dialogs.RescanAlertDialog
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.cleaning.getFileIcon
import com.d4rk.cleaner.utils.compose.bounceClick
import com.d4rk.cleaner.utils.compose.components.CircularDeterminateIndicator
import com.google.common.io.Files.getFileExtension
import java.io.File

@Composable
fun HomeScreen() {
    val context : Context = LocalContext.current
    val viewModel : HomeViewModel = viewModel()
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()
    val uiErrorModel : UiErrorModel by viewModel.uiErrorModel.collectAsState()

    val imageLoader : ImageLoader = remember {
        ImageLoader.Builder(context).memoryCache {
            MemoryCache.Builder(context).maxSizePercent(percent = 0.24).build()
        }.diskCache {
            DiskCache.Builder().directory(context.cacheDir.resolve(relative = "image_cache"))
                    .maxSizePercent(percent = 0.02).build()
        }.build()
    }
    var showRescanDialog : Boolean by remember { mutableStateOf(value = false) }

    LaunchedEffect(Unit) {
        if (! PermissionsUtils.hasStoragePermissions(context)) {
            PermissionsUtils.requestStoragePermissions(context as Activity)
        }
    }

    LaunchedEffect(uiState.showRescanDialog) {
        showRescanDialog = uiState.showRescanDialog
    }

    if (showRescanDialog) {
        RescanAlertDialog(onYes = {
            viewModel.rescan()
            showRescanDialog = false
        } , onDismiss = {
            showRescanDialog = false
        })
    }

    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage ,
                         onDismiss = { viewModel.dismissErrorDialog() })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
        ) {
            if (! uiState.showCleaningComposable) {
                CircularDeterminateIndicator(
                    progress = uiState.progress ,
                    storageUsed = uiState.storageUsed ,
                    storageTotal = uiState.storageTotal ,
                    modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 98.dp)
                )

            }
            else {
                AnalyzeComposable(imageLoader)
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
                visible = uiState.showCleaningComposable,
                enter = fadeIn(animationSpec = tween(durationMillis = 400)) + expandHorizontally(
                    animationSpec = tween(durationMillis = 400), expandFrom = Alignment.Start
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 400)) + shrinkHorizontally(
                    animationSpec = tween(durationMillis = 400), shrinkTowards = Alignment.Start
                ),
                modifier = Modifier.weight(1f)
            ) {
                val enabled = !uiState.isAnalyzing && uiState.selectedFileCount > 0

                val animateStateButtonColor = animateColorAsState(
                    targetValue = if (enabled) MaterialTheme.colorScheme.secondaryContainer else Color.LightGray,
                    animationSpec = tween(400, 0, LinearEasing),
                    label = ""
                )

                FilledTonalButton(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .animateContentSize()
                            .padding(start = 16.dp, end = 8.dp)
                            .bounceClick(),
                    onClick = {
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
                    .padding(start = if (uiState.showCleaningComposable) 8.dp else 16.dp, end = 16.dp)
                    .bounceClick(), onClick = {
                viewModel.analyze()
            }, shape = MaterialTheme.shapes.medium) {
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

@Composable
fun AnalyzeComposable(imageLoader : ImageLoader) {
    val viewModel : HomeViewModel = viewModel()
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.analyze()
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
            if (uiState.isAnalyzing && uiState.scannedFiles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 3) ,
                    verticalArrangement = Arrangement.spacedBy(8.dp) ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                    modifier = Modifier.padding(8.dp) ,
                ) {
                    items(
                        items = uiState.scannedFiles ,
                        key = { file -> file.absolutePath }) { file ->
                        FileCard(file = file , viewModel = viewModel , imageLoader = imageLoader)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth() ,
            verticalAlignment = Alignment.CenterVertically ,
            horizontalArrangement = Arrangement.SpaceBetween ,
        ) {
            val statusText : String = if (uiState.selectedFileCount > 0) {
                stringResource(id = R.string.status_selected_files , uiState.selectedFileCount)
            }
            else {
                stringResource(id = R.string.status_no_files_selected)
            }
            val statusColor : Color by animateColorAsState(
                targetValue = if (uiState.selectedFileCount > 0) {
                    MaterialTheme.colorScheme.primary
                }
                else {
                    MaterialTheme.colorScheme.secondary
                } , animationSpec = tween() , label = "Selected Files Status Color Animation"
            )

            Text(
                text = statusText , color = statusColor , modifier = Modifier.animateContentSize()
            )
            SelectAllComposable(
                checked = uiState.allFilesSelected ,
                onCheckedChange = { viewModel.selectAllFiles(it) } ,
            )
        }
    }
}

@Composable
fun FileCard(file : File , viewModel : HomeViewModel , imageLoader : ImageLoader) {
    val context : Context = LocalContext.current
    val fileExtension : String = getFileExtension(file.name)

    var thumbnailFile : File? by remember(file.absolutePath) { mutableStateOf(value = null) }

    LaunchedEffect(file.absolutePath) {
        viewModel.getVideoThumbnail(filePath = file.absolutePath , context = context) { file ->
            thumbnailFile = file
        }
    }

    Card(
        modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f)
                .bounceClick() ,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (fileExtension) {
                in context.resources.getStringArray(R.array.image_extensions).toList() -> {
                    AsyncImage(
                        model = remember(file) {
                            ImageRequest.Builder(context).data(file).size(64)
                                    .crossfade(enable = true).build()
                        } ,
                        imageLoader = imageLoader ,
                        contentDescription = file.name ,
                        contentScale = ContentScale.Crop ,
                        modifier = Modifier.fillMaxSize() ,
                    )
                }

                in context.resources.getStringArray(R.array.video_extensions).toList() -> {
                    if (thumbnailFile != null) {
                        AsyncImage(
                            model = thumbnailFile ,
                            contentDescription = file.name ,
                            contentScale = ContentScale.Crop ,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else {
                        Icon(
                            painter = painterResource(R.drawable.ic_video_file) ,
                            contentDescription = null ,
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

            Checkbox(checked = viewModel.uiState.value.fileSelectionStates[file] ?: false ,
                     onCheckedChange = { isChecked ->
                         viewModel.onFileSelectionChange(file , isChecked)
                     } ,
                     modifier = Modifier.align(Alignment.TopEnd))

            Text(
                text = file.name ,
                maxLines = 1 ,
                overflow = TextOverflow.Ellipsis ,
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
    checked : Boolean , onCheckedChange : (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .animateContentSize() ,
        verticalAlignment = Alignment.CenterVertically ,
        horizontalArrangement = Arrangement.End
    ) {
        val interactionSource : MutableInteractionSource = remember { MutableInteractionSource() }
        FilterChip(
            modifier = Modifier.bounceClick() ,
            selected = checked ,
            onClick = {
                onCheckedChange(! checked)
            } ,
            label = { Text(stringResource(id = R.string.select_all)) } ,
            leadingIcon = {
                AnimatedContent(
                    targetState = checked , label = "Checkmark Animation"
                ) { targetChecked ->
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