package com.d4rk.cleaner.app.settings.cleaning.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.SwitchPreferenceItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraTinyVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.d4rk.cleaner.app.auto.AutoCleanScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun CleaningSettingsList(paddingValues : PaddingValues) {
    val context = LocalContext.current
    val dataStore : DataStore = koinInject()
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
    val deleteDuplicateFiles: Boolean by dataStore.deleteDuplicateFiles.collectAsState(initial = false)
    val clipboardClean : Boolean by dataStore.clipboardClean.collectAsState(initial = false)
    val streakReminderEnabled: Boolean by dataStore.streakReminderEnabled.collectAsState(initial = false)
    val showStreakCardPref: Boolean by dataStore.showStreakCard.collectAsState(initial = true)
    val autoCleanEnabled: Boolean by dataStore.autoCleanEnabled.collectAsState(initial = false)
    val widgetActionsEnabled: Boolean by dataStore.widgetActionsEnabled.collectAsState(initial = true)

    LazyColumn(
        modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues) ,
    ) {
        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.filters))
            SmallVerticalSpacer()

            Column(
                modifier = Modifier
                        .padding(horizontal = SizeConstants.LargeSize)
                        .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.generic_filter) ,
                    summary = stringResource(id = R.string.summary_preference_settings_generic_filter) ,
                    checked = genericFilter ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveGenericFilter(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_empty_folders) ,
                    checked = deleteEmptyFolders ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteEmptyFolders(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_archives) ,
                    summary = stringResource(id = R.string.summary_preference_settings_archive_filter) ,
                    checked = deleteArchives ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteArchives(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_corpse_files) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_corpse_files) ,
                    checked = deleteCorpseFiles ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteCorpseFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_apk_files) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_apk_files) ,
                    checked = deleteApkFiles ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteApkFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_windows_files) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_windows_files) ,
                    checked = windowsExtensions ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteWindowsFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_office_files) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_office_files) ,
                    checked = officeExtensions ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteOfficeFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_font_files) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_font_files) ,
                    checked = fontExtensions ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteFontFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_other_files) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_other_files) ,
                    checked = otherExtensions ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteOtherFiles(isChecked)
                    }
                }
            }
        }

        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.media))
            SmallVerticalSpacer()

            Column(
                modifier = Modifier
                        .padding(horizontal = SizeConstants.LargeSize)
                        .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_audio) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_audio) ,
                    checked = deleteAudioFiles ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteAudioFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_video) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_video) ,
                    checked = deleteVideoFiles ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteVideoFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.delete_images) ,
                    summary = stringResource(id = R.string.summary_preference_settings_delete_images) ,
                    checked = deleteImageFiles ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteImageFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

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
        }

        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.scanner))
            SmallVerticalSpacer()

            Column(
                modifier = Modifier
                        .padding(horizontal = SizeConstants.LargeSize)
                        .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.duplicates),
                    summary = stringResource(id = R.string.summary_preference_settings_delete_duplicates),
                    checked = deleteDuplicateFiles,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDeleteDuplicateFiles(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.clipboard_clean) ,
                    checked = clipboardClean ,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveClipboardClean(isChecked)
                    }
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.preference_streak_reminder),
                    checked = streakReminderEnabled,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveStreakReminderEnabled(isChecked)
                    }
                }

                if (!showStreakCardPref) {
                    ExtraTinyVerticalSpacer()
                    SwitchPreferenceItem(
                        title = stringResource(id = R.string.preference_show_streak_card),
                        checked = false,
                    ) { isChecked ->
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveShowStreakCard(isChecked)
                            if (isChecked) dataStore.saveStreakHideUntil(0L)
                        }
                        if (isChecked) {
                            Toast.makeText(context, context.getString(R.string.streak_return_toast), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.automatic_cleanup))
            SmallVerticalSpacer()

            Column(
                modifier = Modifier
                    .padding(horizontal = SizeConstants.LargeSize)
                    .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.enable_automatic_cleaning),
                    checked = autoCleanEnabled,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveAutoCleanEnabled(isChecked)
                        if (isChecked) AutoCleanScheduler.schedule(context, dataStore) else AutoCleanScheduler.cancel(context)
                    }
                }
            }
        }

        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.widget_settings_title))
            SmallVerticalSpacer()

            Column(
                modifier = Modifier
                    .padding(horizontal = SizeConstants.LargeSize)
                    .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.enable_widget_actions),
                    checked = widgetActionsEnabled,
                ) { isChecked ->
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveWidgetActionsEnabled(isChecked)
                    }
                }
            }
        }
    }
}
