package com.d4rk.cleaner.app.clean.analyze.components.tabs

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticPagerSwipe
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.app.clean.analyze.components.DuplicateGroupsSection
import com.d4rk.cleaner.app.clean.analyze.components.FilesByDateSection
import com.d4rk.cleaner.app.clean.scanner.domain.actions.ScannerEvent
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.ui.ScannerViewModel
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TabsContent(
    groupedFiles : Map<String , List<File>> , viewModel : ScannerViewModel , view : View , coroutineScope : CoroutineScope , data : UiScannerModel
) {
    val tabs : List<String> = groupedFiles.keys.toList()
    val pagerState : PagerState = rememberPagerState(pageCount = { tabs.size })
    val duplicatesTabTitle =
        data.analyzeState.fileTypesData.fileTypesTitles.getOrElse(10) { "Duplicates" }
    val hasDuplicatesTab = groupedFiles.containsKey(duplicatesTabTitle)

    Row(
        modifier = Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically
    ) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage ,
            modifier = Modifier.weight(weight = 1f) ,
            edgePadding = 0.dp ,
            indicator = { tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(currentTabPosition = tabPositions[pagerState.currentPage]) , shape = RoundedCornerShape(
                        topStart = 3.dp ,
                        topEnd = 3.dp ,
                    )
                )
            } ,
        ) {
            tabs.forEachIndexed { index , title ->
                val allFilesInCategory : List<File> = groupedFiles[title] ?: emptyList()
                val duplicateOriginals = data.analyzeState.duplicateOriginals
                val isCategoryChecked : Boolean = allFilesInCategory.filterNot { it in duplicateOriginals }.all { file ->
                    data.analyzeState.fileSelectionMap[file] == true
                }

                Tab(modifier = Modifier
                        .bounceClick()
                        .clip(RoundedCornerShape(50)) , selected = pagerState.currentPage == index , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page = index)
                    }
                } , text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Start
                    ) {
                        Checkbox(checked = isCategoryChecked , onCheckedChange = {
                            viewModel.toggleSelectFilesForCategory(category = title)
                        })
                        Text(text = title)
                    }
                })
            }
        }

        IconButton(modifier = Modifier.bounceClick() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            viewModel.onCloseAnalyzeComposable()
        }) {
            Icon(
                modifier = Modifier.size(SizeConstants.ButtonIconSize),
                imageVector = Icons.Outlined.Close ,
                contentDescription = null
            )
        }
    }

    HorizontalPager(
        modifier = Modifier.hapticPagerSwipe(pagerState) ,
        state = pagerState ,
    ) { page ->
        val filesForCurrentPage = groupedFiles[tabs[page]] ?: emptyList()

        val filesByDateRaw = remember(filesForCurrentPage) {
            filesForCurrentPage.groupBy { file ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(file.lastModified()))
            }
        }

        if (hasDuplicatesTab && tabs[page] == duplicatesTabTitle) {
            val duplicateGroups = data.analyzeState.duplicateGroups
            val filesByDate = remember(duplicateGroups) {
                duplicateGroups.groupBy { group ->
                    val firstFile = group.first()
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(firstFile.lastModified()))
                }
            }

            DuplicateGroupsSection(
                modifier = Modifier ,
                filesByDate = filesByDate ,
                fileSelectionStates = data.analyzeState.fileSelectionMap ,
                onFileSelectionChange = viewModel::onFileSelectionChange ,
                onDateSelectionChange = { files, checked -> viewModel.onEvent(ScannerEvent.ToggleSelectFilesForDate(files , checked)) } ,
                originals = data.analyzeState.duplicateOriginals ,
                view = view
            )
        } else {
            FilesByDateSection(
                modifier = Modifier ,
                filesByDate = filesByDateRaw ,
                fileSelectionStates = data.analyzeState.fileSelectionMap ,
                onFileSelectionChange = viewModel::onFileSelectionChange ,
                onDateSelectionChange = { files, checked -> viewModel.onEvent(ScannerEvent.ToggleSelectFilesForDate(files , checked)) } ,
                originals = data.analyzeState.duplicateOriginals ,
                view = view
            )
        }
    }}