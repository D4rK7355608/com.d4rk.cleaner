package com.d4rk.cleaner.utils

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Creates a clickable card with a title and a switch within your app's preferences.
 *
 * This composable is useful for displaying settings or preferences that can be toggled on or off.
 * When clicked, the card toggles the switch and calls the provided callback function.
 *
 * @param title The text displayed on the card's title.
 * @param switchState A state variable holding the current on/off state of the switch (true for on).
 * @param onSwitchToggled A callback function called whenever the switch is toggled.
 *  This function receives the new state of the switch (boolean) as a parameter.
 */
@Composable
fun SwitchCardComposable(
    title : String , switchState : State<Boolean> , onSwitchToggled : (Boolean) -> Unit
) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable {
                onSwitchToggled(! switchState.value)
            }) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) ,
            horizontalArrangement = Arrangement.SpaceBetween ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            Switch(checked = switchState.value ,
                   onCheckedChange = onSwitchToggled ,
                   thumbContent = if (switchState.value) {
                       {
                           Icon(
                               imageVector = Icons.Filled.Check ,
                               contentDescription = null ,
                               modifier = Modifier.size(SwitchDefaults.IconSize) ,
                           )
                       }
                   }
                   else {
                       null
                   })
        }
    }
}

/**
 * Displays a category header within your app's preference screens.
 *
 * Use this composable within a scrollable container to separate different sections of
 * your app's preferences with clear category titles.
 *
 * @param title The text to be displayed as the category header.
 */
@Composable
fun PreferenceCategoryItem(
    title : String
) {
    Text(
        text = title ,
        color = MaterialTheme.colorScheme.primary ,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold) ,
        modifier = Modifier.padding(start = 16.dp , top = 16.dp)
    )
}

/**
 * Creates a clickable preference item for app preference screens.
 *
 * This composable displays a preference item with an optional icon, title, and summary.
 * Clicking the entire row triggers the provided `onClick` callback function.
 *
 * @param icon An optional icon to be displayed at the beginning of the preference item. (Provide a `Painter` object)
 * @param title The main title text displayed for the preference item.
 * @param summary An optional secondary text displayed below the title for additional information.
 * @param onClick A callback function that is called when the entire preference item is clicked.
 *  Leave this empty if no action is needed on click.
 */
@Composable
fun PreferenceItem(
    icon : Painter? = null ,
    title : String? = null ,
    summary : String? = null ,
    onClick : () -> Unit = {}
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick) , verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(painter = it , contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            title?.let {
                Text(text = it , style = MaterialTheme.typography.titleLarge)
            }
            summary?.let {
                Text(text = it , style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Creates a clickable preference item with a switch for app preference screens.
 *
 * This composable combines an icon (optional), title, summary (optional), and a switch. Clicking
 * the entire row toggles the switch and calls the provided `onCheckedChange` callback function.
 *
 * @param icon An optional icon to be displayed at the beginning of the preference item. (Provide a `Painter` object)
 * @param title The main title text displayed for the preference item.
 * @param summary An optional secondary text displayed below the title for additional information.
 * @param checked The initial state of the switch (true for on, false for off).
 * @param onCheckedChange A callback function called whenever the switch is toggled.
 *  This function receives the new state of the switch (boolean) as a parameter.
 */
@Composable
fun SwitchPreferenceItem(
    icon : Painter? = null ,
    title : String ,
    summary : String? = null ,
    checked : Boolean ,
    onCheckedChange : (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = { onCheckedChange(! checked) }) ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(painter = it , contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
        ) {
            Text(text = title , style = MaterialTheme.typography.titleLarge)
            summary?.let {
                Text(text = it , style = MaterialTheme.typography.bodyMedium)
            }
        }
        Switch(
            checked = checked ,
            onCheckedChange = onCheckedChange ,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Creates a clickable preference item with a switch and a divider for app preference screens.
 *
 * This composable combines an optional icon, title, summary, switch, and a divider. Clicking
 * the entire row triggers the provided `onClick` callback function. Toggling the switch calls
 * the `onCheckedChange` callback with the new state.
 *
 * @param icon An optional icon to be displayed at the beginning of the preference item. (Provide a `Painter` object)
 * @param title The main title text displayed for the preference item.
 * @param summary A secondary text displayed below the title for additional information.
 * @param checked The initial state of the switch (true for on, false for off).
 * @param onCheckedChange A callback function called whenever the switch is toggled.
 *  This function receives the new state of the switch (boolean) as a parameter.
 * @param onClick A callback function that is called when the entire preference item is clicked.
 *  Leave this empty if no action is needed on click.
 */
@Composable
fun SwitchPreferenceItemWithDivider(
    icon : Painter? = null ,
    title : String ,
    summary : String ,
    checked : Boolean ,
    onCheckedChange : (Boolean) -> Unit ,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick) , verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(painter = it , contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
        ) {
            Text(text = title , style = MaterialTheme.typography.titleLarge)
            Text(text = summary , style = MaterialTheme.typography.bodyMedium)
        }

        VerticalDivider(
            modifier = Modifier
                    .height(32.dp)
                    .align(Alignment.CenterVertically) ,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) ,
            thickness = 1.dp
        )

        Switch(
            checked = checked ,
            onCheckedChange = onCheckedChange ,
            modifier = Modifier.padding(16.dp)
        )
    }
}

enum class ButtonState { Pressed , Idle }

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 0.95f else 1f , label = ""
    )
    this
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = remember { MutableInteractionSource() } ,
                       indication = null ,
                       onClick = { })
            .pointerInput(buttonState) {
                awaitPointerEventScope {
                    buttonState = if (buttonState == ButtonState.Pressed) {
                        waitForUpOrCancellation()
                        ButtonState.Idle
                    }
                    else {
                        awaitFirstDown(false)
                        ButtonState.Pressed
                    }
                }
            }
}