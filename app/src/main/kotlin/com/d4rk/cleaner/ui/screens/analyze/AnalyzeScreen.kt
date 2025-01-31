package com.d4rk.cleaner.ui.screens.analyze

import android.content.Context
import android.content.Intent
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import coil3.video.videoFramePercent
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.components.layouts.NonLazyGrid
import com.d4rk.cleaner.ui.components.buttons.TwoRowButtons
import com.d4rk.cleaner.ui.components.modifiers.hapticPagerSwipe
import com.d4rk.cleaner.ui.components.dialogs.ConfirmationAlertDialog
import com.d4rk.cleaner.ui.screens.home.HomeViewModel
import com.d4rk.cleaner.ui.screens.nofilesfound.NoFilesFoundScreen
import com.d4rk.cleaner.utils.helpers.TimeHelper
import com.d4rk.cleaner.utils.cleaning.getFileIcon
import com.google.common.io.Files.getFileExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable function that displays the Analyze screen.
 * This screen shows the results of a file scan, grouped by file type.
 * It allows users to select files and perform actions like moving to trash or permanently deleting them.
 *
 * @param imageLoader The ImageLoader instance used to load images.
 * @param view The underlying Android View instance.
 * @param viewModel The ViewModel that handles the logic of this screen.
 * @param data The UI data model for the home screen.
 * @author Mihai-Cristian Condrea
 */
@Composable
fun AnalyzeScreen(
    imageLoader : ImageLoader ,
    view : View ,
    viewModel : HomeViewModel ,
    data : UiHomeModel ,
) {
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val enabled = data.analyzeState.selectedFilesCount > 0
    val isLoading : Boolean by viewModel.isLoading.collectAsState()
    val filesTypesTitles = data.analyzeState.fileTypesData.fileTypesTitles
    val apkExtensions = data.analyzeState.fileTypesData.apkExtensions
    val imageExtensions = data.analyzeState.fileTypesData.imageExtensions
    val videoExtensions = data.analyzeState.fileTypesData.videoExtensions
    val audioExtensions = data.analyzeState.fileTypesData.audioExtensions
    val archiveExtensions = data.analyzeState.fileTypesData.archiveExtensions

    val emptyFoldersString = stringResource(R.string.empty_folders)

    val groupedFiles = remember(data.analyzeState) {
        val filesMap = data.analyzeState.scannedFileList.groupBy { file ->
            when (file.extension.lowercase()) {
                in imageExtensions -> {
                    return@groupBy filesTypesTitles[0]
                }

                in audioExtensions -> {
                    return@groupBy filesTypesTitles[1]
                }

                in videoExtensions -> {
                    return@groupBy filesTypesTitles[2]
                }

                in apkExtensions -> {
                    return@groupBy filesTypesTitles[3]
                }

                in archiveExtensions -> {
                    return@groupBy filesTypesTitles[4]
                }

                else -> {
                    return@groupBy filesTypesTitles[6]
                }
            }
        }.filter { it.value.isNotEmpty() }

        val finalMap = filesMap.toMutableMap()

        if (data.analyzeState.emptyFolderList.isNotEmpty()) {
            finalMap[emptyFoldersString] = data.analyzeState.emptyFolderList
        }

        finalMap
    }

    println("Cleaner for Android -> Recomposing AnalyzeScreen")

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
            when {
                isLoading -> {
                    println("Cleaner for Android -> ScannedFileList is empty, showing CircularProgressIndicator")
                    Box(
                        modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                groupedFiles.isEmpty() -> {
                    println("Cleaner for Android -> GroupedFiles is empty or file scan is empty, showing NoFilesFoundScreen")
                    NoFilesFoundScreen(viewModel = viewModel)
                }

                else -> {
                    println("Cleaner for Android -> Files found, displaying them in tabs")
                    val tabs = groupedFiles.keys.toList()
                    val pagerState : PagerState = rememberPagerState(pageCount = { tabs.size })

                    Row(
                        modifier = Modifier.fillMaxWidth() ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScrollableTabRow(
                            selectedTabIndex = pagerState.currentPage ,
                            modifier = Modifier.weight(1f) ,
                            edgePadding = 0.dp ,
                            indicator = { tabPositions ->
                                TabRowDefaults.PrimaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]) ,
                                    shape = RoundedCornerShape(
                                        topStart = 3.dp ,
                                        topEnd = 3.dp ,
                                        bottomEnd = 0.dp ,
                                        bottomStart = 0.dp ,
                                    ) ,
                                )
                            } ,
                        ) {
                            tabs.forEachIndexed { index , title ->
                                Tab(modifier = Modifier.bounceClick() ,
                                    selected = pagerState.currentPage == index ,
                                    onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    } ,
                                    text = { Text(text = title) })
                            }
                        }

                        IconButton(modifier = Modifier.bounceClick() , onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            viewModel.onCloseAnalyzeComposable()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Close , contentDescription = "Close"
                            )
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier.hapticPagerSwipe(pagerState) ,
                        state = pagerState ,
                    ) { page ->
                        val filesForCurrentPage = groupedFiles[tabs[page]] ?: emptyList()

                        val filesByDate = filesForCurrentPage.groupBy { file ->
                            SimpleDateFormat(
                                "yyyy-MM-dd" , Locale.getDefault()
                            ).format(Date(file.lastModified()))
                        }

                        FilesByDateSection(
                            modifier = Modifier ,
                            filesByDate = filesByDate ,
                            fileSelectionStates = data.analyzeState.fileSelectionMap ,
                            imageLoader = imageLoader ,
                            onFileSelectionChange = viewModel::onFileSelectionChange ,
                            view = view
                        )
                    }
                }
            }
        }
        if (data.analyzeState.scannedFileList.isNotEmpty() || data.analyzeState.emptyFolderList.isNotEmpty()) {
            println("Cleaner for Android -> ScannedFileList is not empty, displaying selection controls")
            Row(
                modifier = Modifier.fillMaxWidth() ,
                verticalAlignment = Alignment.CenterVertically ,
                horizontalArrangement = Arrangement.SpaceBetween ,
            ) {
                val statusText : String = if (data.analyzeState.selectedFilesCount > 0) {
                    pluralStringResource(
                        id = R.plurals.status_selected_files ,
                        count = data.analyzeState.selectedFilesCount ,
                        data.analyzeState.selectedFilesCount
                    )
                }
                else {
                    stringResource(id = R.string.status_no_files_selected)
                }
                val statusColor : Color by animateColorAsState(
                    targetValue = if (data.analyzeState.selectedFilesCount > 0) {
                        MaterialTheme.colorScheme.primary
                    }
                    else {
                        MaterialTheme.colorScheme.secondary
                    } , animationSpec = tween() , label = "Selected Files Status Color Animation"
                )

                Text(
                    text = statusText ,
                    color = statusColor ,
                    modifier = Modifier.animateContentSize()
                )
                SelectAllComposable(viewModel = viewModel , view = view)
            }

            TwoRowButtons(modifier = Modifier ,
                          enabled = enabled ,
                          onStartButtonClick = {
                              viewModel.setMoveToTrashConfirmationDialogVisibility(true)
                          } ,
                          onStartButtonIcon = Icons.Outlined.Delete ,
                          onStartButtonText = R.string.move_to_trash ,

                          onEndButtonClick = {
                              viewModel.setDeleteForeverConfirmationDialogVisibility(true)
                          } ,
                          onEndButtonIcon = Icons.Outlined.DeleteForever ,
                          onEndButtonText = R.string.delete_forever ,
                          view = view)

            if (data.analyzeState.isDeleteForeverConfirmationDialogVisible) {
                ConfirmationAlertDialog(confirmationTitle = stringResource(R.string.delete_forever_title) ,
                                        confirmationMessage = stringResource(R.string.delete_forever_message) ,
                                        confirmationConfirmButtonText = stringResource(android.R.string.ok) ,
                                        confirmationDismissButtonText = stringResource(android.R.string.cancel) ,
                                        onConfirm = {
                                            viewModel.clean()
                                            viewModel.setDeleteForeverConfirmationDialogVisibility(
                                                false
                                            )
                                        } ,
                                        onDismiss = {
                                            viewModel.setDeleteForeverConfirmationDialogVisibility(
                                                false
                                            )
                                        })
            }

            if (data.analyzeState.isMoveToTrashConfirmationDialogVisible) {
                ConfirmationAlertDialog(confirmationTitle = stringResource(R.string.move_to_trash_title) ,
                                        confirmationMessage = stringResource(R.string.move_to_trash_message) ,
                                        confirmationConfirmButtonText = stringResource(android.R.string.ok) ,
                                        confirmationDismissButtonText = stringResource(android.R.string.cancel) ,
                                        onConfirm = {
                                            viewModel.moveToTrash()
                                            viewModel.setMoveToTrashConfirmationDialogVisibility(
                                                false
                                            )
                                        } ,
                                        onDismiss = {
                                            viewModel.setMoveToTrashConfirmationDialogVisibility(
                                                false
                                            )
                                        })
            }
        }
    }
}

@Composable
fun FilesByDateSection(
    modifier : Modifier ,
    filesByDate : Map<String , List<File>> ,
    fileSelectionStates : Map<File , Boolean> ,
    imageLoader : ImageLoader ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    view : View ,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        val sortedDates = filesByDate.keys.sortedByDescending { dateString ->
            SimpleDateFormat("yyyy-MM-dd" , Locale.getDefault()).parse(dateString)
        }

        sortedDates.forEach { date ->
            val files = filesByDate[date] ?: emptyList()
            item(key = date) {
                DateHeader(
                    files = files ,
                    fileSelectionStates = fileSelectionStates ,
                    onFileSelectionChange = onFileSelectionChange ,
                    view = view
                )
            }

            item(key = "$date-grid") {
                FilesGrid(
                    files = files ,
                    imageLoader = imageLoader ,
                    fileSelectionStates = fileSelectionStates ,
                    onFileSelectionChange = onFileSelectionChange ,
                    view = view ,
                )
            }
        }
    }
}

@Composable
fun DateHeader(
    files : List<File> ,
    fileSelectionStates : Map<File , Boolean> ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    view : View ,
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp , vertical = 4.dp) ,
        verticalAlignment = Alignment.CenterVertically ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = 8.dp) ,
            text = TimeHelper.formatDate(Date(files[0].lastModified()))
        )
        val allFilesForDateSelected = files.all { fileSelectionStates[it] == true }
        Checkbox(modifier = Modifier.bounceClick() ,
                 checked = allFilesForDateSelected ,
                 onCheckedChange = { isChecked ->
                     view.playSoundEffect(SoundEffectConstants.CLICK)
                     files.forEach { file ->
                         onFileSelectionChange(file , isChecked)
                     }
                 })
    }
}

@Composable
fun FilesGrid(
    files : List<File> ,
    imageLoader : ImageLoader ,
    fileSelectionStates : Map<File , Boolean> ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    view : View ,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NonLazyGrid(
            columns = 3 , itemCount = files.size , modifier = Modifier.padding(horizontal = 8.dp)
        ) { index ->
            val file = files[index]
            FileCard(file = file ,
                     imageLoader = imageLoader ,
                     isChecked = fileSelectionStates[file] == true ,
                     onCheckedChange = { isChecked -> onFileSelectionChange(file , isChecked) } ,
                     view = view ,
                     modifier = Modifier)
        }
    }
}

@Composable
fun FileCard(
    file : File , imageLoader : ImageLoader , onCheckedChange : (Boolean) -> Unit ,
    isChecked : Boolean ,
    view : View ,
    modifier : Modifier = Modifier ,
) {
    val isFolder = file.isDirectory
    val context : Context = LocalContext.current
    val fileExtension : String = remember(file.name) { getFileExtension(file.name) }

    val imageExtensions =
            remember { context.resources.getStringArray(R.array.image_extensions).toList() }
    val videoExtensions =
            remember { context.resources.getStringArray(R.array.video_extensions).toList() }

    Card(
        modifier = modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f)
                .bounceClick()
                .clickable {
                    if (! file.isDirectory) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val uri = FileProvider.getUriForFile(
                            context , "${context.packageName}.fileprovider" , file
                        )
                        intent.setDataAndType(uri , context.contentResolver.getType(uri))
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(intent)
                    }
                } ,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isFolder) {
                Icon(
                    imageVector = Icons.Outlined.Folder ,
                    contentDescription = "Folder icon" ,
                    modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                )
            }
            else {
                when (fileExtension) {
                    in imageExtensions -> {

                        AsyncImage(
                            model = remember(file) {
                                ImageRequest.Builder(context).data(file).size(64)
                                        .crossfade(enable = true).build()
                            } ,
                            contentScale = ContentScale.FillWidth ,
                            contentDescription =file.name ,
                            modifier = Modifier.fillMaxWidth() ,
                            imageLoader = imageLoader ,
                        )
                    }

                    in videoExtensions -> {
                        AsyncImage(model = remember(file) {
                            ImageRequest.Builder(context).data(file)
                                    .decoderFactory { result , options , _ ->
                                        VideoFrameDecoder(result.source , options)
                                    }.videoFramePercent(framePercent = 0.5).crossfade(enable = true)
                                    .build()
                        } ,
                                   imageLoader = imageLoader ,
                                   contentDescription = file.name ,
                                   contentScale = ContentScale.Crop ,
                                   modifier = Modifier.fillMaxSize())
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
            }

            Checkbox(checked = isChecked , onCheckedChange = { checked ->
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onCheckedChange(checked)
            } , modifier = Modifier.align(Alignment.TopEnd))

            Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Black.copy(alpha = 0.4f)
                        )
                        .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = file.name ,
                    maxLines = 1 ,
                    overflow = TextOverflow.Ellipsis ,
                    modifier = Modifier
                            .basicMarquee()
                            .padding(8.dp)
                )
            }
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
    view : View ,
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
            selected = uiState.analyzeState.areAllFilesSelected ,
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                viewModel.toggleSelectAllFiles()
            } ,
            label = { Text(text = stringResource(id = R.string.select_all)) } ,
            leadingIcon = {
                AnimatedContent(
                    targetState = uiState.analyzeState.areAllFilesSelected ,
                    label = "Checkmark Animation"
                ) { targetChecked ->
                    if (targetChecked) {
                        Icon(
                            imageVector = Icons.Filled.Check ,
                            contentDescription = null ,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } ,
            interactionSource = interactionSource ,
        )
    }
}