package com.d4rk.cleaner.utils.compose.components

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
import com.d4rk.cleaner.utils.compose.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScaffold(
    title: String ,
    onBackClicked: () -> Unit ,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehaviorState: TopAppBarScrollBehavior =
            TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviorState.nestedScrollConnection) ,
        topBar = {
            LargeTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(modifier = Modifier.bounceClick(), onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehaviorState
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}