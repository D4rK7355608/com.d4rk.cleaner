package com.d4rk.cleaner.utils.compose.components

import android.view.View
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.utils.haptic.weakHapticFeedback

/**
 * Creates a clickable card with a title and a switch for app preference screens.
 *
 * This composable function displays a card with a title and a switch. The entire card is clickable and toggles the switch when clicked, calling the provided `onSwitchToggled` callback function with the new state.
 * The switch displays a check icon when it's in the 'on' state.
 *
 * @param title The text displayed on the card's title.
 * @param switchState A state variable holding the current on/off state of the switch. Set to true for on and false for off.
 * @param onSwitchToggled A callback function that is called whenever the switch is toggled. This function receives the new state of the switch (boolean) as a parameter.
 */
@Composable
fun SwitchCardComposable(
    title: String, switchState: State<Boolean>, onSwitchToggled: (Boolean) -> Unit
) {
    val view: View = LocalView.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
        .clip(RoundedCornerShape(28.dp))
        .clickable {
            view.weakHapticFeedback()
            onSwitchToggled(!switchState.value)
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            Switch(
                checked = switchState.value,
                onCheckedChange = onSwitchToggled,
                thumbContent = if (switchState.value) {
                    {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                })
        }
    }
}

/**
 * Displays a category header within your app's preference screens.
 *
 * This composable function is used to display a category header in your app's preference screens. It helps in separating different sections of your app's preferences with clear category titles. The title is displayed in a distinct style and color to differentiate it from other preference items.
 *
 * @param title The text to be displayed as the category header. This is typically the name of the category.
 */
@Composable
fun PreferenceCategoryItem(
    title: String
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
    )
}

/**
 * Creates a clickable preference item for app preference screens.
 *
 * This composable function displays a preference item with an optional icon, title, and summary. The entire row is clickable and triggers the provided `onClick` callback function when clicked.
 *
 * @param icon An optional icon to be displayed at the start of the preference item. If provided, it should be an `ImageVector` object.
 * @param title An optional main title text displayed for the preference item.
 * @param summary An optional secondary text displayed below the title for additional information about the preference.
 * @param onClick A callback function that is called when the entire preference item is clicked. If no action is needed on click, this can be left empty.
 */
@Composable
fun PreferenceItem(
    icon: ImageVector? = null,
    title: String? = null,
    summary: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val view: View = LocalView.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = {
                view.weakHapticFeedback()
                onClick()
            }), verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(it, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (!enabled) LocalContentColor.current.copy(alpha = 0.38f) else LocalContentColor.current
                )
            }
            summary?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!enabled) LocalContentColor.current.copy(alpha = 0.38f) else LocalContentColor.current
                )
            }
        }
    }
}

/**
 * Creates a clickable preference item with a switch for app preference screens.
 *
 * This composable function combines an optional icon, title, optional summary, and a switch into a single row.
 * The entire row is clickable and toggles the switch when clicked, calling the provided `onCheckedChange` callback function with the new state.
 *
 * @param icon An optional icon to be displayed at the start of the preference item. If provided, it should be an `ImageVector` object.
 * @param title The main title text displayed for the preference item.
 * @param summary An optional secondary text displayed below the title for additional information about the preference.
 * @param checked The initial state of the switch. Set to true for on and false for off.
 * @param onCheckedChange A callback function that is called whenever the switch is toggled. This function receives the new state of the switch (boolean) as a parameter.
 */
@Composable
fun SwitchPreferenceItem(
    icon: ImageVector? = null,
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val view: View = LocalView.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = {
                view.weakHapticFeedback()
                onCheckedChange(!checked)
            }), verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(it, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            summary?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Creates a clickable preference item with a switch and a divider for app preference screens.
 *
 * This composable function combines an optional icon, title, summary, switch, and a divider into a single row.
 * The entire row is clickable and triggers the provided `onClick` callback function when clicked.
 * The switch is toggled on or off based on the `checked` parameter, and any change in its state calls
 * the `onCheckedChange` callback with the new state.
 *
 * @param icon An optional icon to be displayed at the start of the preference item. If provided, it should be an `ImageVector` object.
 * @param title The main title text displayed for the preference item.
 * @param summary A secondary text displayed below the title for additional information about the preference.
 * @param checked The initial state of the switch. Set to true for on and false for off.
 * @param onCheckedChange A callback function that is called whenever the switch is toggled. This function receives the new state of the switch (boolean) as a parameter.
 * @param onClick A callback function that is called when the entire preference item is clicked. If no action is needed on click, this can be left empty.
 */
@Composable
fun SwitchPreferenceItemWithDivider(
    icon: ImageVector? = null,
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onSwitchClick: (Boolean) -> Unit
) {
    val view: View = LocalView.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = {
                view.weakHapticFeedback()
                onClick()
            }), verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(it, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(text = summary, style = MaterialTheme.typography.bodyMedium)
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Switch(checked = checked, onCheckedChange = { isChecked ->
            onCheckedChange(isChecked)
            onSwitchClick(isChecked)
        }, modifier = Modifier.padding(16.dp))

    }
}