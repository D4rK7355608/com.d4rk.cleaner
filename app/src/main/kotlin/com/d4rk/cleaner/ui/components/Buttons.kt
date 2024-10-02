package com.d4rk.cleaner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.ui.components.animations.bounceClick

@Composable
fun TwoRowButtons(enabled : Boolean ,
                  onStartButtonClick: () -> Unit ,
                  onStartButtonIcon : ImageVector ,
                  onEndButtonClick: () -> Unit ,
                  onEndButtonIcon : ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedButton(enabled = enabled ,
                       onClick = {
                           onStartButtonClick()
                       } ,
                       modifier = Modifier
                               .weight(1f)
                               .bounceClick() ,
                       colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
            Icon(
                imageVector = onStartButtonIcon ,
                contentDescription = "Move to trash" ,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Move to trash")
        }

        Spacer(Modifier.width(8.dp))

        Button(enabled = enabled ,
               onClick = {
                  onEndButtonClick()
               } ,
               modifier = Modifier
                       .weight(1f)
                       .bounceClick() ,
               colors = ButtonDefaults.buttonColors(contentColor = Color.White)) {
            Icon(
                imageVector = onEndButtonIcon ,
                contentDescription = "Delete forever" ,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Delete forever")
        }
    }
}