package com.d4rk.cleaner.ui.components.spacers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LargeHorizontalSpacer() {
    Spacer(modifier = Modifier.width(width = 16.dp))
}

@Composable
fun MediumHorizontalSpacer() {
    Spacer(modifier = Modifier.width(width = 12.dp))
}

@Composable
fun SmallHorizontalSpacer() {
    Spacer(modifier = Modifier.width(width = 8.dp))
}

@Composable
fun ButtonHorizontalSpacer() {
    Spacer(modifier = Modifier.width(width = ButtonDefaults.IconSpacing))
}