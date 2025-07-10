package com.d4rk.cleaner.app.clean.whatsapp.details.ui

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedButtonDirection
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.scanner.ui.components.FileListItem
import com.d4rk.cleaner.app.clean.scanner.ui.components.FilePreviewCard
import com.d4rk.cleaner.app.clean.whatsapp.details.domain.actions.WhatsAppDetailsEvent
import com.d4rk.cleaner.app.clean.whatsapp.details.domain.model.UiWhatsAppDetailsModel
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.components.CustomTabLayout
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.components.DetailsStatusRow
import com.d4rk.cleaner.app.clean.whatsapp.details.ui.components.dialogs.SortAlertDialog
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.actions.WhatsAppCleanerEvent
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.model.UiWhatsAppCleanerModel
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsappCleanerSummaryViewModel
import com.d4rk.cleaner.app.clean.whatsapp.utils.constants.WhatsAppMediaConstants
import com.d4rk.cleaner.app.clean.whatsapp.utils.helpers.openFile
import com.google.common.io.Files.getFileExtension
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    title: String,
    onDelete: (List<File>) -> Unit,
    detailsViewModel: DetailsViewModel,
    viewModel: WhatsappCleanerSummaryViewModel,
    activity: Activity
) {

    val state: UiStateScreen<UiWhatsAppCleanerModel> by viewModel.uiState.collectAsState()
    val detailsState: UiStateScreen<UiWhatsAppDetailsModel> by detailsViewModel.uiState.collectAsState()
    val localizedTitle = when (title) {
        WhatsAppMediaConstants.IMAGES -> stringResource(id = R.string.images)
        WhatsAppMediaConstants.VIDEOS -> stringResource(id = R.string.videos)
        WhatsAppMediaConstants.DOCUMENTS -> stringResource(id = R.string.documents)
        WhatsAppMediaConstants.AUDIOS -> stringResource(id = R.string.audios)
        WhatsAppMediaConstants.STATUSES -> stringResource(id = R.string.statuses)
        WhatsAppMediaConstants.VOICE_NOTES -> stringResource(id = R.string.voice_notes)
        WhatsAppMediaConstants.VIDEO_NOTES -> stringResource(id = R.string.video_notes)
        WhatsAppMediaConstants.GIFS -> stringResource(id = R.string.gifs)
        WhatsAppMediaConstants.WALLPAPERS -> stringResource(id = R.string.wallpapers)
        WhatsAppMediaConstants.STICKERS -> stringResource(id = R.string.stickers)
        WhatsAppMediaConstants.PROFILE_PHOTOS -> stringResource(id = R.string.profile_photos)
        else -> title
    }
    val selected = remember { mutableStateListOf<File>() }
    val isGrid = detailsState.data?.isGridView ?: true
    var showSort by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val sortedFiles = detailsState.data?.files ?: emptyList()
    val suggested = detailsState.data?.suggested ?: emptyList()

    val view: View = LocalView.current

    val hasFiles = sortedFiles.isNotEmpty()

    LargeTopAppBarWithScaffold(
        actions = {
            AnimatedButtonDirection(
                visible = hasFiles,
                icon = if (isGrid) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView , contentDescription = null , onClick = {
                detailsViewModel.onEvent(WhatsAppDetailsEvent.ToggleView)
            } , fromRight = true)

            AnimatedButtonDirection(  visible = true,icon = Icons.AutoMirrored.Filled.Sort , contentDescription = null , onClick = {
                showSort = hasFiles
            } , durationMillis = 400 , fromRight = true)

        },
        title = localizedTitle,
        onBackClicked = { activity.finish() },
        scrollBehavior = scrollBehavior,
    ) { paddingValues ->
        ScreenStateHandler(
            screenState = state,
            onLoading = { LoadingScreen() },
            onEmpty = {
                NoDataScreen(
                    icon = Icons.Outlined.FolderOff,
                    showRetry = true,
                    onRetry = { viewModel.onEvent(WhatsAppCleanerEvent.LoadMedia) }
                )
            },
            onSuccess = { data ->
                val summary = data.mediaSummary
                val files = when (title) {
                    WhatsAppMediaConstants.IMAGES -> summary.images.files
                    WhatsAppMediaConstants.VIDEOS -> summary.videos.files
                    WhatsAppMediaConstants.DOCUMENTS -> summary.documents.files
                    WhatsAppMediaConstants.AUDIOS -> summary.audios.files
                    WhatsAppMediaConstants.STATUSES -> summary.statuses.files
                    WhatsAppMediaConstants.VOICE_NOTES -> summary.voiceNotes.files
                    WhatsAppMediaConstants.VIDEO_NOTES -> summary.videoNotes.files
                    WhatsAppMediaConstants.GIFS -> summary.gifs.files
                    WhatsAppMediaConstants.WALLPAPERS -> summary.wallpapers.files
                    WhatsAppMediaConstants.STICKERS -> summary.stickers.files
                    WhatsAppMediaConstants.PROFILE_PHOTOS -> summary.profilePhotos.files
                    else -> emptyList()
                }

                LaunchedEffect(files) { detailsViewModel.onEvent(WhatsAppDetailsEvent.SetFiles(files)) }

                val receivedFiles = remember(sortedFiles) {
                    sortedFiles.filterNot {
                        it.path.contains("${File.separator}Sent") ||
                                it.path.contains("${File.separator}Private")
                    }
                }
                val sentFiles = remember(sortedFiles) {
                    sortedFiles.filter { it.path.contains("${File.separator}Sent") }
                }
                val privateFiles = remember(sortedFiles) {
                    sortedFiles.filter { it.path.contains("${File.separator}Private") }
                }

                val hasReceived = receivedFiles.isNotEmpty()
                val hasSent = sentFiles.isNotEmpty()
                val hasPrivate = privateFiles.isNotEmpty()

                val tabs = buildList {
                    if (hasReceived) add(stringResource(id = R.string.received))
                    if (hasSent) add(stringResource(id = R.string.sent))
                    if (hasPrivate) add(stringResource(id = R.string.private_tab))
                }

                val tabFiles = buildList {
                    if (hasReceived) add(receivedFiles)
                    if (hasSent) add(sentFiles)
                    if (hasPrivate) add(privateFiles)
                }

                if (tabFiles.isEmpty()) {
                    Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        NoDataScreen(
                            icon = Icons.Outlined.FolderOff,
                            showRetry = true,
                            onRetry = { viewModel.onEvent(WhatsAppCleanerEvent.LoadMedia) }
                        )
                    }
                } else {

                    val pagerState = rememberPagerState { tabs.size }
                    var selectedTabIndex by remember { mutableIntStateOf(0) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(pagerState.currentPage) { selectedTabIndex = pagerState.currentPage }

                    Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                    ) {
                        if (suggested.isNotEmpty()) {
                            SmartSuggestionsCard(
                                selected = selected,
                                suggested = suggested,
                                onShowConfirmChange = { showConfirm = it }
                            )
                        }

                        if (tabs.size > 1) {
                            CustomTabLayout(
                                modifier = Modifier
                                        .fillMaxWidth(),
                                selectedItemIndex = selectedTabIndex,
                                items = tabs,
                                filesPerTab = tabFiles,
                                selectedFiles = selected,
                                onTabSelected = { index ->
                                    selectedTabIndex = index
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                },
                                onTabCheckedChange = { index, checked ->
                                    val listFiles = tabFiles.getOrNull(index) ?: emptyList()
                                    if (checked) {
                                        listFiles.filterNot { it in selected }.forEach { selected.add(it) }
                                    } else {
                                        selected.removeAll(listFiles)
                                    }
                                }
                            )
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                        ) { page ->
                            val list = tabFiles.getOrNull(page) ?: emptyList()

                            DetailsScreenContent(
                                selected = selected,
                                isGrid = isGrid,
                                files = list
                            )
                        }

                        if (tabs.size <= 1) {
                            DetailsStatusRow(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                selectedCount = selected.size,
                                allSelected = selected.size == sortedFiles.size && sortedFiles.isNotEmpty(),
                                view = view,
                                onClickSelectAll = {
                                    if (selected.size == sortedFiles.size && sortedFiles.isNotEmpty()) {
                                        selected.clear()
                                    } else {
                                        selected.clear()
                                        selected.addAll(sortedFiles)
                                    }
                                }
                            )
                        }

                        Button(
                            onClick = { showConfirm = true },
                            enabled = selected.isNotEmpty(),
                            modifier = Modifier
                                    .bounceClick()
                                    .align(Alignment.CenterHorizontally)
                                    .padding(8.dp),
                        ) {
                            Icon(
                                modifier = Modifier.size(size = SizeConstants.ButtonIconSize) ,
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null)
                            ButtonIconSpacer()
                            Text(text = stringResource(id = R.string.delete_selected))
                        }
                    }
                }
            }
        )
    }

    if (showSort) {
        SortAlertDialog(
            current = detailsState.data?.sortType ?: SortType.DATE,
            descending = detailsState.data?.descending ?: false,
            startDate = detailsState.data?.startDate,
            endDate = detailsState.data?.endDate,
            onDismiss = { showSort = false },
            onApply = { type, desc, start, end ->
                detailsViewModel.onEvent(
                    WhatsAppDetailsEvent.ApplySort(type, desc, start, end)
                )
            }
        )
    }

    if (showConfirm) {
        BasicAlertDialog(
            onDismiss = { showConfirm = false },
            onConfirm = {
                showConfirm = false
                onDelete(selected.toList())
                selected.clear()
            },
            onCancel = { showConfirm = false },
            title = stringResource(id = R.string.delete_confirmation_title),
            content = {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.delete_confirmation_message,
                        count = selected.size,
                        selected.size
                    )
                )
            },
            confirmButtonText = stringResource(id = R.string.delete),
            dismissButtonText = stringResource(id = android.R.string.cancel)
        )
    }
}

@Composable
fun DetailsScreenContent(
    selected: MutableList<File>,
    isGrid: Boolean,
    files: List<File>
) {
    val context : Context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()) {
        if (files.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = context.getString(R.string.no_files_found))
            }
        } else {
            if (isGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(96.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(files) { file ->
                        val checked = file in selected
                        Box(modifier = Modifier.padding(4.dp)) {
                            FilePreviewCard(
                                file = file, modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(file) {
                                        detectTapGestures(
                                            onLongPress = {
                                                if (checked) selected.remove(file) else selected.add(file)
                                            },
                                            onTap = { openFile(context, file) }
                                        )
                                    }
                            )
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (checked) selected.remove(file) else selected.add(file)
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(files) { file ->
                        val checked = file in selected
                        val fileExtension = remember(file.name) { getFileExtension(file.name) }
                        val imageExt = remember { context.resources.getStringArray(R.array.image_extensions).toList() }
                        val videoExt = remember { context.resources.getStringArray(R.array.video_extensions).toList() }
                        val isMedia = remember(fileExtension) {
                            imageExt.any { it.equals(fileExtension, ignoreCase = true) } ||
                                    videoExt.any { it.equals(fileExtension, ignoreCase = true) }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .pointerInput(file) {
                                    detectTapGestures(
                                        onLongPress = {
                                            if (checked) selected.remove(file) else selected.add(file)
                                        },
                                        onTap = { openFile(context, file) }
                                    )
                                }
                        ) {
                            if (isMedia) {
                                FilePreviewCard(file = file, modifier = Modifier.weight(1f))
                            } else {
                                FileListItem(file = file, modifier = Modifier.weight(1f))
                            }
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (checked) selected.remove(file) else selected.add(file)
                                },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartSuggestionsCard(
    selected: MutableList<File>,
    suggested: List<File>,
    onShowConfirmChange: (Boolean) -> Unit,
) {
    val context: Context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_auto_fix_high),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.smart_suggestions),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(suggested) { file ->
                    val checked = file in selected
                    Box(modifier = Modifier.padding(4.dp)) {
                        FilePreviewCard(
                            file = file,
                            modifier = Modifier
                                .size(64.dp)
                                .pointerInput(file) {
                                    detectTapGestures(
                                        onLongPress = {
                                            if (checked) selected.remove(file) else selected.add(file)
                                        },
                                        onTap = { openFile(context, file) }
                                    )
                                }
                        )
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                if (checked) selected.remove(file) else selected.add(file)
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
            }
            Button(
                onClick = {
                    selected.clear()
                    selected.addAll(suggested)
                    onShowConfirmChange(true)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(id = R.string.delete_all_suggested))
            }
        }
    }
}
