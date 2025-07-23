package com.d4rk.cleaner.app.main.ui.components.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R

@Composable
fun NavigationDrawerItemContent(
    item: NavigationDrawerItem,
    handleNavigationItemClick: () -> Unit = {}
) {
    val title: String = stringResource(id = item.title)

    NavigationDrawerItem(label = { Text(text = title) }, selected = false, onClick = {
        handleNavigationItemClick()
    }, icon = {
        Icon(imageVector = item.selectedIcon, contentDescription = title)
    }, badge = {
        if (item.badgeText.isNotBlank()) {
            Text(text = item.badgeText)
        }
    }, modifier = Modifier
        .padding(paddingValues = NavigationDrawerItemDefaults.ItemPadding)
        .bounceClick())

    if (item.title == R.string.trash) {
        HorizontalDivider(modifier = Modifier.padding(all = SizeConstants.SmallSize))
    }
}