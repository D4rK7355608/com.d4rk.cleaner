package com.d4rk.cleaner.app.main.ui.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportActivity
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedIconButtonDirection
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cleaner.R
import com.d4rk.android.libs.apptoolkit.R as ToolkitR

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
            AnimatedVisibility(
                visible = showSearch,
                enter = slideInHorizontally { it } + fadeIn() + scaleIn(),
                exit = slideOutHorizontally { it } + fadeOut() + scaleOut()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(CircleShape)
                        .fillMaxWidth(),
                    shape = CircleShape,
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.size(SizeConstants.ButtonIconSize)
                        )

                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    textStyle = MaterialTheme.typography.labelLarge,
                    singleLine = true,
                )
            }
            AnimatedVisibility(
                visible = !showSearch,
                enter = slideInHorizontally { -it } + fadeIn() + scaleIn(),
                exit = slideOutHorizontally { -it } + fadeOut() + scaleOut()
            ) {
                Text(text = stringResource(id = R.string.app_name))
            }
        },
        navigationIcon = {
            AnimatedIconButtonDirection(
                icon = navigationIcon,
                contentDescription = stringResource(id = ToolkitR.string.go_back),
                onClick = { onNavigationIconClick() },
            )
        },
        actions = {
            var expandedMenu by remember { mutableStateOf(false) }

            AnimatedIconButtonDirection(
                fromRight = true,
                icon = Icons.Outlined.MoreVert,
                contentDescription = stringResource(id = ToolkitR.string.content_description_more_options),
                onClick = { expandedMenu = true },
            )

            DropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu = false }) {
                DropdownMenuItem(
                    modifier = Modifier.bounceClick(),
                    text = { Text(stringResource(id = ToolkitR.string.support_us)) },
                    onClick = {
                        expandedMenu = false
                        IntentsHelper.openActivity(context, SupportActivity::class.java)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.VolunteerActivism,
                            contentDescription = stringResource(id = ToolkitR.string.support_us)
                        )
                    })
            }
        },
        scrollBehavior = scrollBehavior
    )
}
