package com.d4rk.cleaner.app.apps.manager.ui.components.tabs

import android.content.pm.ApplicationInfo
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerEvent
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.apps.manager.ui.components.AppItemComposable
import com.d4rk.cleaner.app.apps.manager.ui.components.ShimmerLoadingScreen

@Composable
fun AppsTab(
    apps: List<ApplicationInfo>,
    isLoading: Boolean,
    usageStats: Map<String, Long>,
    viewModel: AppManagerViewModel,
    paddingValues: PaddingValues = PaddingValues(),
) {
    Crossfade(targetState = isLoading , label = "AppsTabCrossfade") { isLoadingState ->
        when {
            isLoadingState -> {
                ShimmerLoadingScreen(paddingValues)
            }

            apps.isEmpty() -> {
                NoDataScreen(
                    textMessage = R.string.no_app_installed , showRetry = true , onRetry = {
                        viewModel.onEvent(event = AppManagerEvent.LoadAppData)
                    })
            }

            else -> {
                val context = LocalContext.current
                val packageManager = context.packageManager
                val sorted = apps.sortedWith(
                    compareByDescending<ApplicationInfo> { usageStats[it.packageName] ?: 0L }
                        .thenBy { packageManager.getApplicationLabel(it).toString().lowercase() }
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = SizeConstants.ExtraTinySize),
                    verticalArrangement = Arrangement.spacedBy(space = SizeConstants.ExtraTinySize),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(items = sorted, key = { _: Int, app: ApplicationInfo -> app.packageName }) { _: Int, app: ApplicationInfo ->
                        AppItemComposable(
                            app = app,
                            lastUsed = usageStats[app.packageName],
                            viewModel = viewModel,
                            modifier = Modifier
                                .animateItem()
                                .padding(start = SizeConstants.SmallSize, end = SizeConstants.SmallSize, top = SizeConstants.SmallSize)
                        )
                    }
                }
            }
        }
    }
}