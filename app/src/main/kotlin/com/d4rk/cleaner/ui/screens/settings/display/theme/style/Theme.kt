package com.d4rk.cleaner.ui.screens.settings.display.theme.style

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
import com.d4rk.cleaner.data.datastore.DataStore

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

/**
 * Determines and returns the appropriate color scheme based on user preferences and system capabilities.
 *
 * This function considers the following factors:
 * - **Dark Theme:** If enabled by the user (isDarkTheme).
 * - **AMOLED Mode:** If enabled by the user (isAmoledMode).
 * - **Dynamic Colors:** If supported by the device (Build Version) and enabled by the user (isDynamicColors).
 * - **System Context:** Used to access dynamic color resources if available (context).
 *
 * @param isDarkTheme Whether the user prefers a dark theme.
 * @param isAmoledMode Whether the user prefers AMOLED-optimized colors.
 * @param isDynamicColors Whether the user has enabled dynamic colors (if supported).
 * @param context The current application context, used for accessing system resources.
 *
 * @return The most suitable color scheme based on the provided parameters.
 */
private fun getColorScheme(
    isDarkTheme: Boolean, isAmoledMode: Boolean, isDynamicColors: Boolean, context: Context
): ColorScheme {
    val dynamicDark: ColorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(context) else darkScheme
    val dynamicLight: ColorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicLightColorScheme(context) else lightScheme

    return when {
        isAmoledMode && isDarkTheme && isDynamicColors -> dynamicDark.copy(
            surface = Color.Black,
            background = Color.Black,
        )

        isAmoledMode && isDarkTheme -> darkScheme.copy(
            surface = Color.Black,
            background = Color.Black,
        )

        isDynamicColors -> if (isDarkTheme) dynamicDark else dynamicLight
        else -> if (isDarkTheme) darkScheme else lightScheme
    }
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val context: Context = LocalContext.current
    val dataStore: DataStore = DataStore.getInstance(context)
    val themeMode: String = dataStore.themeMode.collectAsState(initial = "follow_system").value
    val isDynamicColors: Boolean = dataStore.dynamicColors.collectAsState(initial = true).value
    val isAmoledMode: Boolean = dataStore.amoledMode.collectAsState(initial = false).value

    val isSystemDarkTheme: Boolean = isSystemInDarkTheme()
    val isDarkTheme: Boolean = when (themeMode) {
        stringResource(id = R.string.dark_mode) -> true
        stringResource(id = R.string.light_mode) -> false
        else -> isSystemDarkTheme
    }

    val colorScheme: ColorScheme =
        getColorScheme(isDarkTheme, isAmoledMode, isDynamicColors, context)

    val view: View = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window: Window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, content = content
    )
}