package com.d4rk.cleaner.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportActivity
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedIconButtonDirection
import com.d4rk.android.libs.apptoolkit.core.ui.components.dropdown.CommonDropdownMenuItem
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
    val animatedTitleVisible = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        if (!showSearch) animatedTitleVisible.value = true
    }

    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                AnimatedVisibility(
                    visible = showSearch,
                    enter = TopAppBarTransitions.titleEnter,
                    exit = TopAppBarTransitions.titleExit,
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
                    visible = !showSearch && animatedTitleVisible.value,
                    enter = TopAppBarTransitions.searchEnter,
                    exit = TopAppBarTransitions.searchExit
                ) {
                    Text(text = stringResource(id = R.string.app_name))
                }
            }
        },
        navigationIcon = {
            AnimatedIconButtonDirection(
                icon = navigationIcon,
                contentDescription = stringResource(id = ToolkitR.string.go_back),
                onClick = { onNavigationIconClick() },
                vibrate = false
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
                CommonDropdownMenuItem(
                    textResId = ToolkitR.string.support_us,
                    icon = Icons.Outlined.VolunteerActivism,
                    onClick = {
                        expandedMenu = false
                        IntentsHelper.openActivity(context, SupportActivity::class.java)
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}


object TopAppBarTransitions {
    private val slideFadeScaleSpec: TweenSpec<Float> =
        tween(durationMillis = 500)

    val searchEnter: EnterTransition by lazy {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween()
        ) + fadeIn(animationSpec = slideFadeScaleSpec) + scaleIn(
            animationSpec = slideFadeScaleSpec,
        )
    }

    val searchExit: ExitTransition by lazy {
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween()
        ) + fadeOut(animationSpec = slideFadeScaleSpec) + scaleOut(animationSpec = slideFadeScaleSpec)
    }

    val titleEnter: EnterTransition by lazy {
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween()
        ) + fadeIn(animationSpec = slideFadeScaleSpec) + scaleIn(
            animationSpec = slideFadeScaleSpec,
        )
    }

    val titleExit: ExitTransition by lazy {
        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween()
        ) + fadeOut(animationSpec = slideFadeScaleSpec) + scaleOut(
            animationSpec = slideFadeScaleSpec,
        )
    }
}