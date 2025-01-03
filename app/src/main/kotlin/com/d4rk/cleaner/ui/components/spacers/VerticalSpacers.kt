package com.d4rk.cleaner.ui.components.spacers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LargeVerticalSpacer() {
    Spacer(modifier = Modifier.height(height = 16.dp))
}

@Composable
fun MediumVerticalSpacer() {
    Spacer(modifier = Modifier.height(height = 12.dp))
}

@Composable
fun SmallVerticalSpacer() {
    Spacer(modifier = Modifier.height(height = 8.dp))
}