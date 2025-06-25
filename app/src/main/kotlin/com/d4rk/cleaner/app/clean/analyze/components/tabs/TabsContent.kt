package com.d4rk.cleaner.app.clean.analyze.components.tabs

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import coil3.ImageLoader
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticPagerSwipe
import com.d4rk.cleaner.app.clean.analyze.components.FilesByDateSection
import com.d4rk.cleaner.app.clean.analyze.components.DuplicateGroupsSection
import com.d4rk.cleaner.app.clean.home.domain.data.model.ui.UiHomeModel
import com.d4rk.cleaner.app.clean.home.ui.HomeViewModel
import com.d4rk.cleaner.app.clean.home.utils.helpers.groupDuplicatesByOriginal
import com.d4rk.cleaner.app.clean.home.domain.actions.HomeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TabsContent(
    groupedFiles : Map<String , List<File>> , imageLoader : ImageLoader , viewModel : HomeViewModel , view : View , coroutineScope : CoroutineScope , data : UiHomeModel
) {
    val tabs : List<String> = groupedFiles.keys.toList()
    val pagerState : PagerState = rememberPagerState(pageCount = { tabs.size })

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
                        Spacer(modifier = Modifier.width(4.dp))
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
                imageVector = Icons.Outlined.Close , contentDescription = null
            )
        }
    }

    HorizontalPager(
        modifier = Modifier.hapticPagerSwipe(pagerState) ,
        state = pagerState ,
    ) { page ->
        val filesForCurrentPage = groupedFiles[tabs[page]] ?: emptyList()

        val filesByDateRaw = filesForCurrentPage.groupBy { file ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(file.lastModified()))
        }

        if (tabs[page] == tabs.last()) {
            val filesByDate = filesByDateRaw.mapValues { (_, list) ->
                groupDuplicatesByOriginal(list)
            }

            DuplicateGroupsSection(
                modifier = Modifier,
                filesByDate = filesByDate,
                fileSelectionStates = data.analyzeState.fileSelectionMap,
                imageLoader = imageLoader,
                onFileSelectionChange = viewModel::onFileSelectionChange,
                onDateSelectionChange = { files, checked -> viewModel.onEvent(HomeEvent.ToggleSelectFilesForDate(files, checked)) },
                originals = data.analyzeState.duplicateOriginals,
                view = view
            )
        } else {
            FilesByDateSection(
                modifier = Modifier,
                filesByDate = filesByDateRaw,
                fileSelectionStates = data.analyzeState.fileSelectionMap,
                imageLoader = imageLoader,
                onFileSelectionChange = viewModel::onFileSelectionChange,
                onDateSelectionChange = { files, checked -> viewModel.onEvent(HomeEvent.ToggleSelectFilesForDate(files, checked)) },
                originals = data.analyzeState.duplicateOriginals,
                view = view
            )
        }
    }
}