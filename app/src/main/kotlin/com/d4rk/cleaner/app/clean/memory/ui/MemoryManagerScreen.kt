package com.d4rk.cleaner.app.clean.memory.ui

import android.app.Activity
import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.os.Environment
import android.provider.Settings
import android.content.Intent
import java.io.File
import com.d4rk.cleaner.core.utils.helpers.FileManagerHelper
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.carousel.CustomCarousel
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.clean.memory.domain.actions.MemoryEvent
import com.d4rk.cleaner.app.clean.memory.domain.data.model.RamInfo
import com.d4rk.cleaner.app.clean.memory.domain.data.model.StorageInfo
import com.d4rk.cleaner.app.clean.memory.domain.data.model.ui.UiMemoryManagerScreen
import com.d4rk.cleaner.app.clean.memory.ui.components.MemoryManagerShimmer
import com.d4rk.cleaner.app.clean.memory.ui.components.RamInfoCard
import com.d4rk.cleaner.app.clean.memory.ui.components.StorageBreakdownGrid
import com.d4rk.cleaner.app.clean.memory.ui.components.StorageInfoCard
import com.d4rk.cleaner.core.utils.helpers.PermissionsHelper
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryManagerComposable(paddingValues : PaddingValues) {
    val viewModel : MemoryManagerViewModel = koinViewModel()
    val uiState : UiStateScreen<UiMemoryManagerScreen> by viewModel.uiState.collectAsState()
    val context : Context = LocalContext.current

    LaunchedEffect(key1 = true) {
        if (! PermissionsHelper.hasStoragePermissions(context)) {
            PermissionsHelper.requestStoragePermissions(context as Activity)
        }
        if (! PermissionsHelper.hasUsageAccessPermissions(context)) {
            PermissionsHelper.requestUsageAccess(context as Activity)
        }
    }

    ScreenStateHandler(screenState = uiState , onLoading = {
        MemoryManagerShimmer(paddingValues = paddingValues)
    } , onEmpty = {
        NoDataScreen(icon = Icons.Outlined.Memory , showRetry = true , onRetry = { viewModel.onEvent(MemoryEvent.LoadMemoryData) })
    } , onSuccess = { screenData ->
        MemoryManagerScreenContent(viewModel = viewModel , screenData = screenData , paddingValues = paddingValues)
    })
}

@Composable
fun MemoryManagerScreenContent(viewModel : MemoryManagerViewModel , screenData : UiMemoryManagerScreen , paddingValues : PaddingValues , adsConfig : AdsConfig = koinInject(qualifier = named(name = "large_banner"))) {
    val carouselItems = listOf(screenData.storageInfo , screenData.ramInfo)
    val pagerState : PagerState = rememberPagerState { carouselItems.size }
    val view : View = LocalView.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues = paddingValues)
    ) {
        CustomCarousel(items = carouselItems , sidePadding = 24.dp , pagerState = pagerState) { item ->
            when (item) {
                is StorageInfo -> StorageInfoCard(item)
                is RamInfo -> RamInfoCard(item)
                else -> Unit
            }
        }

        LargeVerticalSpacer()

        AdBanner(modifier = Modifier.padding(bottom = SizeConstants.MediumSize) , adsConfig = adsConfig)

        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(horizontal = SizeConstants.LargeSize) , verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.categories) ,
                modifier = Modifier.weight(1f) ,
                style = MaterialTheme.typography.headlineSmall ,
            )

            SmallHorizontalSpacer()
            IconButton(
                modifier = Modifier.bounceClick() , onClick = {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    viewModel.onEvent(MemoryEvent.ToggleListExpanded)
                }) {
                Icon(
                    modifier = Modifier.size(SizeConstants.ButtonIconSize),
                    imageVector = if (screenData.listExpanded) Icons.Outlined.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowLeft ,
                    contentDescription = if (screenData.listExpanded) "Collapse" else "Expand"
                )
            }
        }

        SmallVerticalSpacer()

        screenData.storageInfo?.storageBreakdown?.let { storageBreakdown ->
            if (screenData.listExpanded && storageBreakdown.isNotEmpty()) {
                StorageBreakdownGrid(
                    storageBreakdown = screenData.storageInfo.storageBreakdown,
                    onItemClick = { category -> handleStorageItemClick(context, category) }
                )
            }
        }
    }
}

private fun handleStorageItemClick(context: Context, category: String) {
    val pm = context.packageManager
    when (category) {
        context.getString(R.string.installed_apps) -> {
            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            if (intent.resolveActivity(pm) != null) {
                context.startActivity(intent)
            }
        }
        context.getString(R.string.system) -> {
            val intent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
            if (intent.resolveActivity(pm) != null) {
                context.startActivity(intent)
            }
        }
        context.getString(R.string.music) -> FileManagerHelper.openFolderOrSettings(
            context,
            File(Environment.getExternalStorageDirectory(), "Music")
        )
        context.getString(R.string.images) -> FileManagerHelper.openFolderOrSettings(
            context,
            File(Environment.getExternalStorageDirectory(), "DCIM")
        )
        context.getString(R.string.documents) -> FileManagerHelper.openFolderOrSettings(
            context,
            File(Environment.getExternalStorageDirectory(), "Documents")
        )
        context.getString(R.string.downloads) -> FileManagerHelper.openFolderOrSettings(
            context,
            File(Environment.getExternalStorageDirectory(), "Download")
        )
        context.getString(R.string.other_files) -> FileManagerHelper.openFolderOrSettings(
            context,
            Environment.getExternalStorageDirectory()
        )
    }
}