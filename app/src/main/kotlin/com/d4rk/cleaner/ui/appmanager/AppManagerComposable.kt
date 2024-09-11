package com.d4rk.cleaner.ui.appmanager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.appmanager.ui.ApkInfo
import com.d4rk.cleaner.utils.PermissionsUtils
import com.d4rk.cleaner.utils.haptic.weakHapticFeedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * Composable function for managing and displaying different app categories.
 */
@Composable
fun AppManagerComposable() {
    val viewModel: AppManagerViewModel = viewModel(
        factory = AppManagerViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val context: Context = LocalContext.current
    val tabs: List<String> = listOf(
        stringResource(id = R.string.installed_apps),
        stringResource(id = R.string.system_apps),
        stringResource(id = R.string.app_install_files),
    )
    val pagerState: PagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val transition: Transition<Boolean> =
        updateTransition(targetState = !isLoading, label = "LoadingTransition")

    val contentAlpha: Float by transition.animateFloat(label = "Content Alpha") {
        if (it) 1f else 0f
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(context) {
        if (!PermissionsUtils.hasUsageAccessPermissions(context)) {
            PermissionsUtils.requestUsageAccess(context as Activity)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.alpha(contentAlpha),
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        shape = RoundedCornerShape(
                            topStart = 3.dp,
                            topEnd = 3.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp,
                        ),
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, selected = pagerState.currentPage == index, onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    })
                }
            }

            HorizontalPager(
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> AppsComposable(
                        apps = uiState.installedApps.filter { app: ApplicationInfo ->
                            app.flags and ApplicationInfo.FLAG_SYSTEM == 0
                        }
                    )

                    1 -> AppsComposable(
                        apps = uiState.installedApps.filter { app: ApplicationInfo ->
                            app.flags and ApplicationInfo.FLAG_SYSTEM != 0
                        }
                    )

                    2 -> ApksComposable(apkFiles = uiState.apkFiles)
                }
            }
        }
    }
}

/**
 * Composable function for displaying a list of apps.
 *
 * @param apps List of ApplicationInfo objects representing the apps to display.
 */
@Composable
fun AppsComposable(apps: List<ApplicationInfo>) {
    LazyColumn {
        items(
            items = apps,
            key = { app -> app.packageName }
        ) { app ->
            AppItemComposable(app)
        }
    }
}

/**
 * Composable function for displaying detailed information about a single app.
 *
 * @param app ApplicationInfo object representing the app to display.
 */
@Composable
fun AppItemComposable(
    app: ApplicationInfo
) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val packageManager: PackageManager = context.packageManager
    val appName: String = app.loadLabel(packageManager).toString()
    val apkPath: String = app.publicSourceDir
    val apkFile = File(apkPath)
    val sizeInBytes: Long = apkFile.length()
    val sizeInKB: Long = sizeInBytes / 1024
    val sizeInMB: Long = sizeInKB / 1024
    val appSize: String = "%.2f MB".format(sizeInMB.toFloat())
    val appIcon: ImageBitmap = remember(app.packageName) {
        val drawable: Drawable = app.loadIcon(packageManager)
        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
        bitmap.asImageBitmap()
    }
    var showMenu: Boolean by remember { mutableStateOf(false) }
    OutlinedCard(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = appIcon, contentDescription = null, modifier = Modifier.size(48.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = appSize, style = MaterialTheme.typography.bodyMedium
                )
            }


            Box {
                IconButton(onClick = {
                    view.weakHapticFeedback()
                    showMenu = true
                }) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = {
                    view.weakHapticFeedback()
                    showMenu = false
                }) {
                    DropdownMenuItem(text = {
                        Text(stringResource(R.string.uninstall))
                    }, onClick = {
                        view.weakHapticFeedback()
                        val uri: Uri = Uri.fromParts("package", app.packageName, null)
                        val intent = Intent(Intent.ACTION_DELETE, uri)
                        context.startActivity(intent)
                    })
                    DropdownMenuItem(text = { Text(stringResource(R.string.share)) }, onClick = {
                        view.weakHapticFeedback()
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app")
                        @Suppress("DEPRECATION") val isFromPlayStore: Boolean =
                            context.packageManager.getInstallerPackageName(app.packageName) == "com.android.vending"
                        if (isFromPlayStore) {
                            val playStoreLink =
                                "https://play.google.com/store/apps/details?id=${app.packageName}"
                            val shareMessage = "Check out this app: $appName\n$playStoreLink"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        } else {
                            val shareMessage = "Check out this app: $appName\n$app.packageName"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share App"))
                    })
                    DropdownMenuItem(text = { Text(stringResource(R.string.app_info)) },
                        onClick = {
                            view.weakHapticFeedback()
                            val appInfoIntent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val packageUri: Uri =
                                Uri.fromParts("package", app.packageName, null)
                            appInfoIntent.data = packageUri
                            context.startActivity(appInfoIntent)
                        })
                }
            }
        }
    }
}

/**
 * Composable function for displaying a list of APK files on the device.
 */
@Composable
fun ApksComposable(apkFiles: List<ApkInfo>) {
    LazyColumn {
        items(
            items = apkFiles,
            key = { apkInfo -> apkInfo.id }
        ) { apkInfo ->
            ApkItemComposable(apkPath = apkInfo.path)
        }
    }
}


/**
 * Composable function for displaying detailed information about an APK file.
 *
 * @param apkPath Path to the APK file.
 */
@Composable
fun ApkItemComposable(apkPath: String) {
    val context: Context = LocalContext.current
    val view: View = LocalView.current
    val apkFile = File(apkPath)
    val sizeInBytes: Long = apkFile.length()
    val sizeInKB: Long = sizeInBytes / 1024
    val sizeInMB: Long = sizeInKB / 1024
    val apkSize: String = "%.2f MB".format(sizeInMB.toFloat())
    val apkName: String = apkFile.name

    val packageInfo: PackageInfo? = context.packageManager.getPackageArchiveInfo(apkPath, 0)
    val appIcon: ImageBitmap =
        packageInfo?.applicationInfo?.loadIcon(context.packageManager)?.let {
            val bitmap: Bitmap = if (it is BitmapDrawable) {
                it.bitmap
            } else {
                val bitmap: Bitmap = Bitmap.createBitmap(
                    it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                it.setBounds(0, 0, canvas.width, canvas.height)
                it.draw(canvas)
                bitmap
            }
            bitmap.asImageBitmap()
        } ?: ImageBitmap.imageResource(id = R.mipmap.ic_launcher)

    var showMenu: Boolean by remember { mutableStateOf(value = false) }

    OutlinedCard(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = appIcon, contentDescription = null, modifier = Modifier.size(48.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = apkName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = apkSize, style = MaterialTheme.typography.bodyMedium
                )
            }

            Box {
                IconButton(onClick = {
                    view.weakHapticFeedback()
                    showMenu = true
                }) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = {
                    view.weakHapticFeedback()
                    showMenu = false
                }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.share)) }, onClick = {
                        view.weakHapticFeedback()
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/vnd.android.package-archive"
                        val contentUri: Uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            apkFile
                        )
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                context.getString(R.string.share_apk)
                            )
                        )
                    })

                    DropdownMenuItem(text = { Text(stringResource(id = R.string.install)) },
                        onClick = {
                            view.weakHapticFeedback()
                            val installIntent = Intent(Intent.ACTION_VIEW)
                            val contentUri: Uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                apkFile
                            )
                            installIntent.setDataAndType(
                                contentUri,
                                "application/vnd.android.package-archive"
                            )
                            installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.startActivity(installIntent)
                        })
                }
            }
        }
    }
}