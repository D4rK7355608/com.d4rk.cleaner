package com.d4rk.cleaner.ui.components.navigation

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import com.d4rk.cleaner.ui.components.animations.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffoldWithBackButton(
    title: String, onBackClicked: () -> Unit, content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehaviorState: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val view: View = LocalView.current

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviorState.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = { Text(title) }, navigationIcon = {
                IconButton(modifier = Modifier.bounceClick(), onClick = {
                    onBackClicked()
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }, scrollBehavior = scrollBehaviorState
            )
        }) { paddingValues ->
        content(paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffold(
    title: String, content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehaviorState: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(modifier = Modifier.nestedScroll(scrollBehaviorState.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = { Text(title) }, scrollBehavior = scrollBehaviorState
            )
        }) { paddingValues ->
        content(paddingValues)
    }
}