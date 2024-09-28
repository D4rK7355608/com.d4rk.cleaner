package com.d4rk.cleaner.ui.screens.home

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.d4rk.cleaner.ui.components.CircularDeterminateIndicator
import com.d4rk.cleaner.ui.components.NonLazyGrid
import com.d4rk.cleaner.ui.components.animations.bounceClick
import com.d4rk.cleaner.ui.components.animations.hapticPagerSwipe
import com.d4rk.cleaner.ui.components.dialogs.ErrorAlertDialog
import com.d4rk.cleaner.ui.components.dialogs.RescanAlertDialog
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.TimeHelper
import com.d4rk.cleaner.utils.cleaning.getFileIcon
import com.google.common.io.Files.getFileExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                CircularDeterminateIndicator(progress = uiState.progress ,
                                             modifier = Modifier
                                                     .align(Alignment.TopCenter)
                                                     .offset(y = 98.dp) ,
                                             onClick = { viewModel.analyze() })
            }
            else {
                AnalyzeComposable(imageLoader)
            }
        }
    }
}

@Composable
fun AnalyzeComposable(imageLoader : ImageLoader) {
    val viewModel : HomeViewModel = viewModel()
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val coroutineScope : CoroutineScope = rememberCoroutineScope()

    val apkExtensions = remember { context.resources.getStringArray(R.array.apk_extensions) }
    val imageExtensions = remember { context.resources.getStringArray(R.array.image_extensions) }
    val videoExtensions = remember { context.resources.getStringArray(R.array.video_extensions) }
    val audioExtensions = remember { context.resources.getStringArray(R.array.audio_extensions) }
    val archiveExtensions =
            remember { context.resources.getStringArray(R.array.archive_extensions) }

    val groupedFiles = remember(
        uiState.scannedFiles ,
        apkExtensions ,
        imageExtensions ,
        videoExtensions ,
        audioExtensions ,
        archiveExtensions
    ) {
        uiState.scannedFiles.groupBy { file ->
            when (file.extension.lowercase()) {
                in apkExtensions -> {
                    return@groupBy "APKs"
                }

                in imageExtensions -> {
                    return@groupBy "Images"
                }

                in videoExtensions -> {
                    return@groupBy "Videos"
                }

                in audioExtensions -> {
                    return@groupBy "Audios"
                }

                in archiveExtensions -> {
                    return@groupBy "Archives"
                }

                else -> {
                    return@groupBy "Others"
                }
            }
        }
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
                    .fillMaxWidth(),
        ) {
            when {
                uiState.isAnalyzing && uiState.scannedFiles.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.scannedFiles.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.FolderOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(id = R.string.no_files_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedButton (
                                modifier = Modifier.bounceClick(),
                                onClick = {
                                    // TODO: Add close action
                                }
                            ) {
                                Icon(modifier = Modifier.size(ButtonDefaults.IconSize) , imageVector = Icons.Outlined.Refresh , contentDescription = "Close")
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Try again")
                            }
                        }
                    }
                }

                else -> {
                    val tabs = groupedFiles.keys.toList()
                    val pagerState: PagerState = rememberPagerState(pageCount = { tabs.size })

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScrollableTabRow(
                            selectedTabIndex = pagerState.currentPage,
                            modifier = Modifier.weight(1f),
                            edgePadding = 0.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.PrimaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                    shape = RoundedCornerShape(
                                        topStart = 3.dp,
                                        topEnd = 3.dp,
                                        bottomEnd = 0.dp,
                                        bottomStart = 0.dp,
                                    ),
                                )
                            },
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    modifier = Modifier.bounceClick(),
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    text = { Text(text = title) }
                                )
                            }
                        }

                        IconButton(
                            modifier = Modifier.bounceClick(),
                            onClick = {
                                // TODO: Add close action
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier.hapticPagerSwipe(pagerState),
                        state = pagerState,
                    ) { page ->
                        val filesForCurrentPage = groupedFiles[tabs[page]] ?: emptyList()

                        val filesByDate = filesForCurrentPage.groupBy { file ->
                            SimpleDateFormat(
                                "yyyy-MM-dd", Locale.getDefault()
                            ).format(Date(file.lastModified()))
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            filesByDate.forEach { (date, files) ->
                                item(key = date) {
                                    Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp , vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(start = 8.dp),
                                            text = TimeHelper.formatDate(Date(files[0].lastModified()))
                                        )
                                        val allFilesForDateSelected =
                                                files.all { uiState.fileSelectionStates[it] == true }
                                        Checkbox(
                                            modifier = Modifier.bounceClick(),
                                            checked = allFilesForDateSelected,
                                            onCheckedChange = { isChecked ->
                                                files.forEach { file ->
                                                    viewModel.onFileSelectionChange(
                                                        file,
                                                        isChecked
                                                    )
                                                }
                                            })
                                    }
                                }

                                item(key = "$date-grid") {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        NonLazyGrid(
                                            columns = 3,
                                            itemCount = files.size,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) { index ->
                                            FileCard(
                                                file = files[index],
                                                viewModel = viewModel,
                                                imageLoader = imageLoader
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (uiState.scannedFiles.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val statusText: String = if (uiState.selectedFileCount > 0) {
                    stringResource(id = R.string.status_selected_files, uiState.selectedFileCount)
                } else {
                    stringResource(id = R.string.status_no_files_selected)
                }
                val statusColor: Color by animateColorAsState(
                    targetValue = if (uiState.selectedFileCount > 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    animationSpec = tween(),
                    label = "Selected Files Status Color Animation"
                )

                Text(
                    text = statusText, color = statusColor, modifier = Modifier.animateContentSize()
                )
                SelectAllComposable(viewModel)
            }

            Row(
                modifier = Modifier
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    onClick = {
                        // TODO: add trash
                    },
                    modifier = Modifier.weight(1f).bounceClick(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Move to trash",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Move to trash")
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.clean(activity)
                    },
                    modifier = Modifier.weight(1f).bounceClick(),
                    colors = ButtonDefaults.buttonColors(contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever,
                        contentDescription = "Delete forever",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Delete forever")
                }
            }
        }
    }
}

@Composable
fun FileCard(file : File , viewModel : HomeViewModel , imageLoader : ImageLoader) {
    val context : Context = LocalContext.current
    val fileExtension : String = remember(file.name) { getFileExtension(file.name) }

    var thumbnailFile : File? by remember(file.absolutePath) { mutableStateOf(value = null) }

    LaunchedEffect(file.absolutePath) {
        viewModel.getVideoThumbnail(filePath = file.absolutePath , context = context) { file ->
            thumbnailFile = file
        }
    }

    val imageExtensions =
            remember { context.resources.getStringArray(R.array.image_extensions).toList() }
    val videoExtensions =
            remember { context.resources.getStringArray(R.array.video_extensions).toList() }

    Card(
        modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f)
                .bounceClick() ,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (fileExtension) {
                in imageExtensions -> {
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

                in videoExtensions -> {
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
                    val fileIcon = remember(fileExtension) {
                        getFileIcon(
                            fileExtension , context
                        )
                    }
                    Icon(
                        painter = painterResource(fileIcon) ,
                        contentDescription = null ,
                        modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                    )
                }
            }

            Checkbox(
                checked = viewModel.uiState.value.fileSelectionStates[file] ?: false ,
                onCheckedChange = { isChecked ->
                    viewModel.onFileSelectionChange(file , isChecked)
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
    viewModel : HomeViewModel ,
) {
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()

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
            selected = uiState.allFilesSelected ,
            onClick = {
                viewModel.toggleSelectAllFiles()
            } ,
            label = { Text(stringResource(id = R.string.select_all)) } ,
            leadingIcon = {
                AnimatedContent(
                    targetState = uiState.allFilesSelected , label = "Checkmark Animation"
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