package com.d4rk.cleaner.app.apps.manager.ui.components.tabs

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.domain.actions.AppManagerEvent
import com.d4rk.cleaner.app.apps.manager.domain.data.model.ApkInfo
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.apps.manager.ui.components.ApkItem
import com.d4rk.cleaner.app.apps.manager.ui.components.ShimmerLoadingScreen

@Composable
fun ApksTab(
    apkFiles: List<ApkInfo>,
    isLoading: Boolean,
    viewModel: AppManagerViewModel,
    paddingValues: PaddingValues = PaddingValues()
) {
    Crossfade(targetState = isLoading, label = "ApksTab") { targetIsLoading ->
        when {
            targetIsLoading -> {
                ShimmerLoadingScreen(paddingValues)
            }

            apkFiles.isEmpty() -> {
                NoDataScreen(
                    textMessage = R.string.no_app_installed, showRetry = true, onRetry = {
                        viewModel.onEvent(event = AppManagerEvent.LoadAppData)
                    }, icon = Icons.Outlined.Android
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = SizeConstants.ExtraTinySize),
                    verticalArrangement = Arrangement.spacedBy(space = SizeConstants.ExtraTinySize),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = apkFiles,
                        key = { _: Int, apkInfo: ApkInfo -> apkInfo.id }) { _: Int, apkInfo: ApkInfo ->

                        ApkItem(
                            apkPath = apkInfo.path, viewModel = viewModel, modifier = Modifier
                                .animateItem()
                                .padding(
                                    start = SizeConstants.SmallSize,
                                    end = SizeConstants.SmallSize,
                                    top = SizeConstants.SmallSize
                                )
                        )
                    }
                }
            }
        }
    }
}