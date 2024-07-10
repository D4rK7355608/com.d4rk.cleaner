package com.d4rk.cleaner.ui.settings.cleaning

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.utils.compose.components.PreferenceCategoryItem
import com.d4rk.cleaner.utils.compose.components.SwitchPreferenceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleaningSettingsComposable(activity: CleaningSettingsActivity) {
    val context = LocalContext.current
    val dataStore = DataStore.getInstance(context)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val genericFilter by dataStore.genericFilter.collectAsState(initial = true)
    val deleteEmptyFolders by dataStore.deleteEmptyFolders.collectAsState(initial = true)
    val deleteArchives by dataStore.deleteArchives.collectAsState(initial = false)
    val deleteInvalidMedia by dataStore.deleteInvalidMedia.collectAsState(initial = false)
    val deleteCorpseFiles by dataStore.deleteCorpseFiles.collectAsState(initial = false)
    val deleteApkFiles by dataStore.deleteApkFiles.collectAsState(initial = true)
    val deleteAudioFiles by dataStore.deleteAudioFiles.collectAsState(initial = false)
    val deleteVideoFiles by dataStore.deleteVideoFiles.collectAsState(initial = false)
    val deleteImageFiles by dataStore.deleteImageFiles.collectAsState(initial = false)
    val doubleChecker by dataStore.doubleChecker.collectAsState(initial = false)
    val clipboardClean by dataStore.clipboardClean.collectAsState(initial = false)
    val oneClickClean by dataStore.oneClickClean.collectAsState(initial = false)
    val dailyCleaner by dataStore.dailyCleaner.collectAsState(initial = false)
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.cleaning)) }, navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues),
        ) {
            item {
                PreferenceCategoryItem(title = stringResource(R.string.filters))
                SwitchPreferenceItem(
                    title = stringResource(R.string.generic_filter),
                    summary = stringResource(R.string.summary_preference_settings_generic_filter),
                    checked = genericFilter,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveGenericFilter(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.delete_empty_folders),
                    checked = deleteEmptyFolders,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteEmptyFolders(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.delete_archives),
                    summary = stringResource(R.string.summary_preference_settings_archive_filter),
                    checked = deleteArchives,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteArchives(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.delete_corpse_files),
                    summary = stringResource(R.string.summary_preference_settings_delete_corpse_files),
                    checked = deleteCorpseFiles,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteCorpseFiles(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.delete_apk_files),
                    summary = stringResource(R.string.summary_preference_settings_delete_apk_files),
                    checked = deleteApkFiles,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteApkFiles(isChecked)
                    }
                }
            }

            item {
                PreferenceCategoryItem(title = "Media")
                SwitchPreferenceItem(
                    title = "Delete audio",
                    summary = "Delete the audio files from the device",
                    checked = deleteAudioFiles,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteAudioFiles(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = "Delete video",
                    summary = "Delete the video files from the device",
                    checked = deleteVideoFiles,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteVideoFiles(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = "Delete images",
                    summary = "Delete the image files from the device",
                    checked = deleteImageFiles,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteImageFiles(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.delete_invalid_media),
                    summary = stringResource(R.string.summary_preference_settings_delete_invalid_media),
                    checked = deleteInvalidMedia,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteInvalidMedia(isChecked)
                    }
                }
            }

            item {
                PreferenceCategoryItem(title = stringResource(R.string.scanner))
                SwitchPreferenceItem(
                    title = stringResource(R.string.double_checker),
                    summary = stringResource(R.string.summary_preference_settings_double_checker),
                    checked = doubleChecker,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDoubleChecker(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.clipboard_clean),
                    checked = clipboardClean,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveClipboardClean(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.one_click_clean),
                    summary = stringResource(R.string.summary_preference_settings_one_click_clean),
                    checked = oneClickClean,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveOneClickClean(isChecked)
                    }
                }
                SwitchPreferenceItem(
                    title = stringResource(R.string.daily_clean),
                    summary = stringResource(R.string.summary_preference_settings_daily_clean),
                    checked = dailyCleaner,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDailyCleaner(isChecked)
                        if (isChecked) {
                            //    CleanReceiver.scheduleAlarm(context.applicationContext)
                        } else {
                            //  CleanReceiver.cancelAlarm(context.applicationContext)
                        }
                    }
                }
            }
        }
    }
}