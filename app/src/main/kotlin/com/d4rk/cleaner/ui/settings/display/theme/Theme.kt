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
    val dataStore = DataStore(context)
    val themeMode = dataStore.themeMode.collectAsState(initial = "follow_system").value
    val isDynamicColors = dataStore.dynamicColors.collectAsState(initial = true).value
    val isAmoledMode = dataStore.amoledMode.collectAsState(initial = false).value // Add this line


    val isDarkTheme = when (themeMode) {
        stringResource(R.string.dark_mode) -> true
        stringResource(R.string.light_mode) -> false

        // TODO: FIX or Remove the auto battery saver theme
        //stringResource(R.string.auto_battery_mode) -> BatteryManagerCompat.from(context).isPowerSaveMode
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        isDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme && isAmoledMode -> darkColorScheme(onBackground = Color.Black)
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}