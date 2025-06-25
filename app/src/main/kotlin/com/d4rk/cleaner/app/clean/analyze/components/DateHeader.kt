package com.d4rk.cleaner.app.clean.analyze.components

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.app.clean.analyze.utils.helpers.TimeHelper
import java.io.File
import java.util.Date

@Composable
fun DateHeader(
    files : List<File> ,
    fileSelectionStates : Map<File , Boolean> ,
    onFileSelectionChange : (File , Boolean) -> Unit ,
    onDateSelectionChange : (List<File>, Boolean) -> Unit ,
    view : View ,
) {
    val context : Context = LocalContext.current
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SizeConstants.SmallSize , vertical = SizeConstants.ExtraSmallSize) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (files.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(start = SizeConstants.SmallSize) , text = TimeHelper.formatDate(date = Date(files[0].lastModified()) , context = context)
            )
        }
        val allFilesForDateSelected : Boolean = files.all { fileSelectionStates[it] == true }
        Checkbox(modifier = Modifier.bounceClick() , checked = allFilesForDateSelected , onCheckedChange = { isChecked ->
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onDateSelectionChange(files, isChecked)
        })
    }
}
