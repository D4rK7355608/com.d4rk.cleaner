package com.d4rk.cleaner.app.onboarding.utils.interfaces.providers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.core.content.ContextCompat
import com.d4rk.android.libs.apptoolkit.app.oboarding.domain.data.model.ui.OnboardingPage
import com.d4rk.android.libs.apptoolkit.app.oboarding.ui.components.pages.CrashlyticsOnboardingPageTab
import com.d4rk.android.libs.apptoolkit.app.oboarding.ui.components.pages.FinalOnboardingPageTab
import com.d4rk.android.libs.apptoolkit.app.oboarding.ui.components.pages.ThemeOnboardingPageTab
import com.d4rk.android.libs.apptoolkit.app.oboarding.utils.interfaces.providers.OnboardingProvider
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.main.ui.MainActivity
import com.d4rk.cleaner.app.onboarding.ui.tabs.StoragePermissionOnboardingTab
import com.d4rk.cleaner.app.onboarding.utils.constants.OnboardingKeys
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppOnboardingProvider : OnboardingProvider, KoinComponent {

    private val dataStore: DataStore by inject()

    override fun getOnboardingPages(context: Context): List<OnboardingPage> {
        return listOf(
            OnboardingPage.DefaultPage(
                key = OnboardingKeys.WELCOME,
                title = context.getString(R.string.onboarding_welcome_title),
                description = context.getString(R.string.onboarding_welcome_description),
                imageVector = Icons.Outlined.CleaningServices
            ),
            OnboardingPage.DefaultPage(
                key = OnboardingKeys.PERSONALIZATION_OPTIONS,
                title = context.getString(R.string.onboarding_personalization_title),
                description = context.getString(R.string.onboarding_personalization_description),
                imageVector = Icons.Outlined.AutoDelete
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.THEME_OPTIONS,
                content = {
                    ThemeOnboardingPageTab()
                }
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.CRASHLYTICS_OPTIONS,
                content = {
                    CrashlyticsOnboardingPageTab()
                }
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.PERMISSION_STORAGE,
                content = {
                    StoragePermissionOnboardingTab()
                }
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.ONBOARDING_COMPLETE,
                content = {
                    FinalOnboardingPageTab()
                }
            ),

            ).filter {
            when (it) {
                is OnboardingPage.DefaultPage -> it.isEnabled
                is OnboardingPage.CustomPage -> it.isEnabled
            }
        }
    }

    override fun onOnboardingFinished(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!dataStore.isStreakReminderInitialized()) {
                val hasPermission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }

                dataStore.saveStreakReminderEnabled(hasPermission)
            }
        }

        context.startActivity(Intent(context, MainActivity::class.java))
    }
}