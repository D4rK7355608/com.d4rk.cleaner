package com.d4rk.cleaner.ui.settings.display.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.store.DataStore

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val themeMode = dataStore.themeMode.collectAsState(initial = "follow_system").value
    val isDynamicColors = dataStore.dynamicColors.collectAsState(initial = true).value
    val isAmoledMode = dataStore.amoledMode.collectAsState(initial = false).value

    val isSystemDarkTheme = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        stringResource(R.string.dark_mode) -> true
        stringResource(R.string.light_mode) -> false
        else -> isSystemDarkTheme
    }

    val colorScheme = when {
        isDarkTheme && isAmoledMode && isDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicDarkColorScheme(
            context
        ).copy(
            surface = Color.Black,
            background = Color.Black,
        )

        isDarkTheme && isAmoledMode -> darkColorScheme(
            surface = Color.Black,
            background = Color.Black,
        )

        isDarkTheme -> if (isDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(context)
        } else {
            DarkColorScheme
        }

        else -> if (isDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(context)
        } else {
            LightColorScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}