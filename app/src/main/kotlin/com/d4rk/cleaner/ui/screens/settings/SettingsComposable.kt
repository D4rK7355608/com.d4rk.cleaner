package com.d4rk.cleaner.ui.screens.settings

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ContactSupport
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SafetyCheck
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.SettingsPreferenceItem
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.about.AboutSettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.advanced.AdvancedSettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.display.DisplaySettingsList
import com.d4rk.android.libs.apptoolkit.ui.screens.settings.privacy.PrivacySettingsList
import com.d4rk.android.libs.apptoolkit.utils.helpers.ScreenHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.ui.components.navigation.TopAppBarScaffoldWithBackButton
import com.d4rk.cleaner.ui.screens.help.HelpActivity
import com.d4rk.cleaner.ui.screens.settings.cleaning.CleaningSettingsList
import com.d4rk.cleaner.ui.screens.settings.general.GeneralSettingsActivity
import com.d4rk.cleaner.ui.screens.settings.general.SettingsContent
import com.d4rk.cleaner.utils.providers.AppAboutSettingsProvider
import com.d4rk.cleaner.utils.providers.AppAdvancedSettingsProvider
import com.d4rk.cleaner.utils.providers.AppDisplaySettingsProvider
import com.d4rk.cleaner.utils.providers.AppPrivacySettingsProvider

@Composable
fun SettingsComposable(activity : SettingsActivity) {
    val context : Context = LocalContext.current

    TopAppBarScaffoldWithBackButton(title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.settings) , onBackClicked = { activity.finish() }) { paddingValues ->
        val isTabletOrLandscape : Boolean = ScreenHelper.isLandscapeOrTablet(context = context)
        if (isTabletOrLandscape) {
            TabletSettingsScreen(paddingValues = paddingValues , context = context)
        }
        else {
            PhoneSettingsScreen(paddingValues = paddingValues , context = context)
        }
    }
}

@Composable
fun PhoneSettingsScreen(paddingValues : PaddingValues , context : Context) {
    SettingsList(paddingValues = paddingValues , onPreferenceClick = { preference ->
        when (preference) {
            "notifications" -> com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openAppNotificationSettings(context)

            "display" -> GeneralSettingsActivity.start(
                context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.display) , content = SettingsContent.DISPLAY
            )

            "cleaning" -> GeneralSettingsActivity.start(
                context , title = context.getString(R.string.cleaning) , content = SettingsContent.CLEANING
            )

            "privacy" -> GeneralSettingsActivity.start(
                context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.security_and_privacy) , content = SettingsContent.PRIVACY
            )

            "advanced" -> GeneralSettingsActivity.start(
                context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.advanced) , content = SettingsContent.ADVANCED
            )

            "about" -> GeneralSettingsActivity.start(
                context , title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.about) , content = SettingsContent.ABOUT
            )
        }
    })
}

@Composable
fun TabletSettingsScreen(paddingValues : PaddingValues , context : Context) {
    var selectedPreference : String? by remember { mutableStateOf(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight()
        ) {
            SettingsList(paddingValues = paddingValues , onPreferenceClick = { preference ->
                selectedPreference = preference
            })
        }

        Box(
            modifier = Modifier
                    .weight(weight = 2f)
                    .fillMaxHeight()
        ) {
            AnimatedContent(targetState = selectedPreference , transitionSpec = {
                if (targetState != initialState) {
                    val animationSpec : TweenSpec<IntOffset> = tween(durationMillis = 300)
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left , animationSpec = animationSpec
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left , animationSpec = animationSpec
                    )
                }
                else {
                    val fadeSpec : TweenSpec<Float> = tween(durationMillis = 300)
                    fadeIn(animationSpec = fadeSpec) togetherWith fadeOut(animationSpec = fadeSpec)
                }
            }) { preference ->
                preference?.let {
                    SettingsDetail(
                        preference = it , context = context , paddingValues = paddingValues
                    )
                } ?: SettingsDetailPlaceholder(paddingValues = paddingValues)
            }
        }
    }
}

@Composable
fun SettingsDetailPlaceholder(paddingValues : PaddingValues) {
    val context : Context = LocalContext.current

    Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            item {
                Card(
                    modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp)
                            .fillMaxSize()
                            .wrapContentHeight() ,
                    shape = RoundedCornerShape(size = 28.dp) ,
                ) {
                    Column(
                        modifier = Modifier.padding(all = 24.dp) , horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = R.drawable.il_settings , contentDescription = null , modifier = Modifier
                                    .size(size = 258.dp)
                                    .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(height = 16.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth() , text = stringResource(id = R.string.app_name) , style = MaterialTheme.typography.titleMedium , color = MaterialTheme.colorScheme.onSurface , textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(height = 8.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth() , text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.settings_placeholder_description) , style = MaterialTheme.typography.bodyMedium , color = MaterialTheme.colorScheme.onSurfaceVariant , textAlign = TextAlign.Center
                        )
                    }

                    OutlinedButton(modifier = Modifier
                            .padding(all = 24.dp)
                            .align(Alignment.Start)
                            .bounceClick() , onClick = {
                        com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openActivity(
                            context = context , activityClass = HelpActivity::class.java
                        )
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ContactSupport , contentDescription = null
                        )
                        ButtonIconSpacer()
                        Text(text = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.get_help))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDetail(preference : String , context : Context , paddingValues : PaddingValues) {
    when (preference) {
        "notifications" -> com.d4rk.android.libs.apptoolkit.utils.helpers.IntentsHelper.openAppNotificationSettings(context)

        "display" -> DisplaySettingsList(
            paddingValues = paddingValues , provider = AppDisplaySettingsProvider()
        )

        "cleaning" -> CleaningSettingsList(
            paddingValues = paddingValues
        )

        "privacy" -> PrivacySettingsList(
            paddingValues = paddingValues , provider = AppPrivacySettingsProvider()
        )

        "advanced" -> AdvancedSettingsList(
            paddingValues = paddingValues , provider = AppAdvancedSettingsProvider()
        )

        "about" -> AboutSettingsList(
            paddingValues = paddingValues , provider = AppAboutSettingsProvider()
        )

        else -> Text(
            text = "Unknown preference" , style = MaterialTheme.typography.bodyLarge , color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun SettingsList(
    paddingValues : PaddingValues , onPreferenceClick : (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues) ,
    ) {
        item {
            Spacer(modifier = Modifier.height(height = 24.dp))
            Column(modifier = Modifier.run {
                padding(start = 16.dp , end = 16.dp).clip(RoundedCornerShape(24.dp))
            }) {
                SettingsPreferenceItem(Icons.Outlined.Notifications , title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.notifications) , summary = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_notifications) , onClick = { onPreferenceClick("notifications") })
                Spacer(modifier = Modifier.height(2.dp))
                SettingsPreferenceItem(Icons.Outlined.Palette , title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.display) , summary = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_display) , onClick = { onPreferenceClick("display") })
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Column(
                modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
            ) {
                SettingsPreferenceItem(Icons.Outlined.CleaningServices,
                                       title = stringResource(id = R.string.cleaning),
                                       summary = stringResource(id = R.string.summary_preference_settings_cleaning),
                                       onClick = {
                                           onPreferenceClick("cleaning")
                                       })
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Column(
                modifier = Modifier
                        .padding(start = 16.dp , end = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
            ) {
                SettingsPreferenceItem(Icons.Outlined.SafetyCheck , title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.security_and_privacy) , summary = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_privacy_and_security) , onClick = { onPreferenceClick("privacy") })
                Spacer(modifier = Modifier.height(2.dp))
                SettingsPreferenceItem(Icons.Outlined.Build , title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.advanced) , summary = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.summary_preference_settings_advanced) , onClick = { onPreferenceClick("advanced") })
                Spacer(modifier = Modifier.height(2.dp))
                SettingsPreferenceItem(Icons.Outlined.Info , title = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.about) , summary = stringResource(id = R.string.summary_preference_settings_about) , onClick = { onPreferenceClick("about") })
            }
        }
    }
}