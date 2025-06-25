package com.d4rk.cleaner.app.clean.analyze.components

import android.view.View
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ScreenHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DuplicateGroupsSection(
    modifier : Modifier ,
    filesByDate : Map<String , List<List<File>>> ,
    fileSelectionStates : Map<File , Boolean> ,
    imageLoader : ImageLoader ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    onDateSelectionChange: (List<File>, Boolean) -> Unit ,
    originals: Set<File> = emptySet(),
    view : View ,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val columns = if (ScreenHelper.isTablet(context = context)) 6 else 3
    val cardSize = (configuration.screenWidthDp.dp - SizeConstants.SmallSize * 2) / columns // FIXME: Using Configuration.screenWidthDp instead of LocalWindowInfo.current.containerSize

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        val sortedDates : List<String> = filesByDate.keys.sortedByDescending { dateString ->
            SimpleDateFormat("yyyy-MM-dd" , Locale.getDefault()).parse(dateString)
        }

        sortedDates.forEach { date ->
            val groups : List<List<File>> = filesByDate[date] ?: emptyList()
            val allFiles : List<File> = groups.flatten()
            if (allFiles.isNotEmpty()) {
                item(key = date) {
                    DateHeader(
                        files = allFiles , fileSelectionStates = fileSelectionStates , onFileSelectionChange = onFileSelectionChange , onDateSelectionChange = onDateSelectionChange , view = view
                    )
                }
            }

            items(groups) { group ->
                Row(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SizeConstants.SmallSize)
                            .horizontalScroll(rememberScrollState())
                ) {
                    group.forEach { file ->
                        FileCard(
                            file = file,
                            imageLoader = imageLoader,
                            isChecked = fileSelectionStates[file] == true,
                            onCheckedChange = { checked -> onFileSelectionChange(file, checked) },
                            isOriginal = file in originals,
                            view = view,
                            modifier = Modifier
                                .padding(end = SizeConstants.SmallSize, top = SizeConstants.MediumSize)
                                .width(cardSize)
                        )
                    }
                }
            }
        }
    }
}
