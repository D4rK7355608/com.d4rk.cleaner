package com.d4rk.cleaner.ui.screens.analyze.components

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

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
        val sortedDates : List<String> = filesByDate.keys.sortedByDescending { dateString ->
            SimpleDateFormat("yyyy-MM-dd" , Locale.getDefault()).parse(dateString)
        }

        sortedDates.forEach { date ->
            val files : List<File> = filesByDate[date] ?: emptyList()
            item(key = date) {
                DateHeader(
                    files = files , fileSelectionStates = fileSelectionStates , onFileSelectionChange = onFileSelectionChange , view = view
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