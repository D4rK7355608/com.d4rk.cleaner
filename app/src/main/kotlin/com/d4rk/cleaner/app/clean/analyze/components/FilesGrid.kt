package com.d4rk.cleaner.app.clean.analyze.components

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NonLazyGrid
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ScreenHelper
import java.io.File

@Composable
fun FilesGrid(
    files : List<File> ,
    fileSelectionStates : Map<File , Boolean> ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    originals: Set<File> = emptySet(),
    view : View ,
) {
    val columns : Int = if (ScreenHelper.isTablet(context = LocalContext.current)) 6 else 3

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NonLazyGrid(
            columns = columns , itemCount = files.size , modifier = Modifier.padding(horizontal = SizeConstants.SmallSize)
        ) { index ->
            val file : File = files[index]
            FileCard(
                file = file,
                isChecked = fileSelectionStates[file] == true,
                onCheckedChange = { isChecked -> onFileSelectionChange(file, isChecked) },
                isOriginal = file in originals,
                view = view,
                modifier = Modifier
            )
        }
    }
}