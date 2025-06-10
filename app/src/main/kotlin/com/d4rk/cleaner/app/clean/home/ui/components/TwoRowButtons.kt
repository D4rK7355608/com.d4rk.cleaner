package com.d4rk.cleaner.app.clean.home.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@Composable
fun TwoRowButtons(
    modifier : Modifier , enabled : Boolean , onStartButtonClick : () -> Unit , onStartButtonIcon : ImageVector , onStartButtonText : Int , onEndButtonClick : () -> Unit , onEndButtonIcon : ImageVector , onEndButtonText : Int , view : View
) {

    Row(
        modifier = modifier.fillMaxWidth() , horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedButton(
            enabled = enabled ,
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onStartButtonClick()
            } ,
            modifier = Modifier
                    .weight(weight = 1f)
                    .bounceClick() ,
        ) {
            Icon(
                imageVector = onStartButtonIcon , contentDescription = "Move to trash" , modifier = Modifier.size(size = SizeConstants.ButtonIconSize)
            )
            ButtonIconSpacer()
            Text(text = stringResource(id = onStartButtonText) , modifier = Modifier.basicMarquee())
        }

        Spacer(Modifier.width(width = SizeConstants.SmallSize))

        Button(
            enabled = enabled ,
            onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onEndButtonClick()
            } ,
            modifier = Modifier
                    .weight(1f)
                    .bounceClick() ,
        ) {
            Icon(
                imageVector = onEndButtonIcon , contentDescription = "Delete forever" , modifier = Modifier.size(size = SizeConstants.ButtonIconSize)
            )
            ButtonIconSpacer()
            Text(text = stringResource(id = onEndButtonText) , modifier = Modifier.basicMarquee())
        }
    }
}