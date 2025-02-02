package com.d4rk.cleaner.ui.screens.settings.cleaning

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.SwitchPreferenceItem
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CleaningSettingsList(paddingValues : PaddingValues) {
    val dataStore : DataStore = AppCoreManager.dataStore
    val genericFilter : Boolean by dataStore.genericFilter.collectAsState(initial = true)
    val deleteEmptyFolders : Boolean by dataStore.deleteEmptyFolders.collectAsState(initial = true)
    val deleteArchives : Boolean by dataStore.deleteArchives.collectAsState(initial = false)
    val deleteInvalidMedia : Boolean by dataStore.deleteInvalidMedia.collectAsState(initial = false)
    val deleteCorpseFiles : Boolean by dataStore.deleteCorpseFiles.collectAsState(initial = false)
    val deleteApkFiles : Boolean by dataStore.deleteApkFiles.collectAsState(initial = true)
    val deleteAudioFiles : Boolean by dataStore.deleteAudioFiles.collectAsState(initial = false)
    val deleteVideoFiles : Boolean by dataStore.deleteVideoFiles.collectAsState(initial = false)
    val windowsExtensions : Boolean by dataStore.deleteWindowsFiles.collectAsState(initial = false)
    val officeExtensions : Boolean by dataStore.deleteOfficeFiles.collectAsState(initial = false)
    val fontExtensions : Boolean by dataStore.deleteFontFiles.collectAsState(initial = false)
    val otherExtensions : Boolean by dataStore.deleteOtherFiles.collectAsState(initial = false)
    val deleteImageFiles : Boolean by dataStore.deleteImageFiles.collectAsState(initial = false)
    val clipboardClean : Boolean by dataStore.clipboardClean.collectAsState(initial = false)

    LazyColumn(
        modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues) ,
    ) {
        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.filters))
            SwitchPreferenceItem(
                title = stringResource(id = R.string.generic_filter) ,
                summary = stringResource(id = R.string.summary_preference_settings_generic_filter) ,
                checked = genericFilter ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveGenericFilter(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_empty_folders) ,
                checked = deleteEmptyFolders ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteEmptyFolders(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_archives) ,
                summary = stringResource(id = R.string.summary_preference_settings_archive_filter) ,
                checked = deleteArchives ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteArchives(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_corpse_files) ,
                summary = stringResource(id = R.string.summary_preference_settings_delete_corpse_files) ,
                checked = deleteCorpseFiles ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteCorpseFiles(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_apk_files) ,
                summary = stringResource(id = R.string.summary_preference_settings_delete_apk_files) ,
                checked = deleteApkFiles ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteApkFiles(isChecked)
                }
            }

            SwitchPreferenceItem(
                title = "Windows Extensions" ,
                summary = "Detects and delete windows extensions (exe,dll,sys)" ,
                checked = windowsExtensions ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteWindowsFiles(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = "Office Extensions" ,
                summary = "Detects and delete office extensions (doc,xls,ppt,docx,xlsx,pptx)" ,
                checked = officeExtensions ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteOfficeFiles(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = "Font Extensions" ,
                summary = "Detects and delete font extensions (ttf,otf)" ,
                checked = fontExtensions ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteFontFiles(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = "Other Extensions" ,
                summary = "Detects and delete other extensions (tmp,ini,log,bak,old)" ,
                checked = otherExtensions ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteOtherFiles(isChecked)
                }
            }
        }

        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.media))
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_audio) ,
                summary = stringResource(id = R.string.summary_preference_settings_delete_audio) ,
                checked = deleteAudioFiles ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteAudioFiles(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_video) ,
                summary = stringResource(id = R.string.summary_preference_settings_delete_video) ,
                checked = deleteVideoFiles ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteVideoFiles(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_images) ,
                summary = stringResource(id = R.string.summary_preference_settings_delete_images) ,
                checked = deleteImageFiles ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteImageFiles(isChecked)
                }
            }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.delete_invalid_media) ,
                summary = stringResource(id = R.string.summary_preference_settings_delete_invalid_media) ,
                checked = deleteInvalidMedia ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveDeleteInvalidMedia(isChecked)
                }
            }
        }

        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.scanner))
            SwitchPreferenceItem(
                title = stringResource(id = R.string.clipboard_clean) ,
                checked = clipboardClean ,
            ) { isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveClipboardClean(isChecked)
                }
            }
        }
    }
}