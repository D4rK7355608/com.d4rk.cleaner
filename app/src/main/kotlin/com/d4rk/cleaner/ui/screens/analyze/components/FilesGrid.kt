package com.d4rk.cleaner.ui.screens.analyze.components

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.d4rk.android.libs.apptoolkit.utils.helpers.ScreenHelper
import com.d4rk.cleaner.ui.components.layouts.NonLazyGrid
import java.io.File

@Composable
fun FilesGrid(
    files : List<File> ,
    imageLoader : ImageLoader ,
    fileSelectionStates : Map<File , Boolean> ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    view : View ,
) {
    val columns : Int = if (ScreenHelper.isTablet(context = LocalContext.current)) 6 else 3

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NonLazyGrid(
            columns = columns , itemCount = files.size , modifier = Modifier.padding(horizontal = 8.dp)
        ) { index ->
            val file : File = files[index]
            FileCard(file = file , imageLoader = imageLoader , isChecked = fileSelectionStates[file] == true , onCheckedChange = { isChecked -> onFileSelectionChange(file , isChecked) } , view = view , modifier = Modifier)
        }
    }
}