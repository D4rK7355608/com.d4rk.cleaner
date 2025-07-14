package com.d4rk.cleaner.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportActivity
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedIconButtonDirection
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanerTopAppBar(
    navigationIcon: ImageVector,
    onNavigationIconClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    showSearch: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val context: Context = LocalContext.current
    TopAppBar(
        title = {
            if (showSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
                    placeholder = { Text(text = stringResource(id = R.string.search)) },
                    singleLine = true,
                    modifier = Modifier
                )
            } else {
                Text(text = stringResource(id = R.string.app_name))
            }
        },
        navigationIcon = {
            AnimatedIconButtonDirection(
                icon = navigationIcon,
                contentDescription = stringResource(id = R.string.go_back),
                onClick = { onNavigationIconClick() },
            )
        },
        actions = {
            var expandedMenu by remember { mutableStateOf(false) }

            AnimatedIconButtonDirection(
                fromRight = true,
                icon = Icons.Outlined.MoreVert,
                contentDescription = stringResource(id = R.string.content_description_more_options),
                onClick = { expandedMenu = true },
            )

            DropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu = false }) {
                DropdownMenuItem(
                    modifier = Modifier.bounceClick(),
                    text = { Text(stringResource(id = R.string.support_us)) },
                    onClick = {
                        expandedMenu = false
                        IntentsHelper.openActivity(context, SupportActivity::class.java)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.VolunteerActivism,
                            contentDescription = stringResource(id = R.string.support_us)
                        )
                    })
            }
        },
        scrollBehavior = scrollBehavior
    )
}

