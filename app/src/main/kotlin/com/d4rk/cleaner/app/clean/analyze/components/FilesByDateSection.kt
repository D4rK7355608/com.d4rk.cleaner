package com.d4rk.cleaner.app.clean.analyze.components

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil3.imageLoader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FilesByDateSection(
    modifier : Modifier ,
    filesByDate : Map<String , List<File>> ,
    fileSelectionStates : Map<File , Boolean> ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    onDateSelectionChange: (List<File>, Boolean) -> Unit ,
    originals: Set<File> = emptySet(),
    view : View ,
) {
    val imageLoader = LocalContext.current.imageLoader
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        val sortedDates : List<String> = filesByDate.keys.sortedByDescending { dateString ->
            SimpleDateFormat("yyyy-MM-dd" , Locale.getDefault()).parse(dateString)
        }

        sortedDates.forEach { date ->
            val files : List<File> = filesByDate[date] ?: emptyList()
            if (files.isNotEmpty()) {
                item(key = date) {
                    DateHeader(
                        files = files , fileSelectionStates = fileSelectionStates , onFileSelectionChange = onFileSelectionChange , onDateSelectionChange = onDateSelectionChange , view = view
                    )
                }

                item(key = "$date-grid") {
                    FilesGrid(
                        files = files ,
                        fileSelectionStates = fileSelectionStates ,
                        onFileSelectionChange = onFileSelectionChange ,
                        originals = originals ,
                        view = view ,
                    )
                }
            }
        }
    }
}