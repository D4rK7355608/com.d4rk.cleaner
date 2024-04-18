package com.d4rk.cleaner.utils

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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Switch cards
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

// Preferences
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