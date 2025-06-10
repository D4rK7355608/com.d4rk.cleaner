package com.d4rk.cleaner.app.clean.home.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen

@Composable
fun InfoColumn(
    title : String , value : String , modifier : Modifier = Modifier , isLoading : Boolean
) {
    if (isLoading) {
        LoadingScreen()
    }
    else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally , modifier = modifier
        ) {
            Text(text = title , style = MaterialTheme.typography.bodySmall , modifier = Modifier.basicMarquee())
            Text(
                text = value , style = MaterialTheme.typography.bodyMedium , maxLines = 2 , overflow = TextOverflow.Ellipsis
            )
        }
    }
}