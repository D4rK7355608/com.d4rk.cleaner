package com.d4rk.cleaner.core.di.modules

import com.d4rk.android.libs.apptoolkit.app.about.ui.AboutViewModel
import com.d4rk.android.libs.apptoolkit.app.ads.data.ConsentRepository
import com.d4rk.android.libs.apptoolkit.app.ads.data.ConsentRepositoryImpl
import com.d4rk.android.libs.apptoolkit.app.ads.domain.usecases.LoadConsentInfoUseCase
import com.d4rk.android.libs.apptoolkit.app.ads.ui.AdsSettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.permissions.ui.PermissionsViewModel
import com.d4rk.android.libs.apptoolkit.app.permissions.utils.interfaces.PermissionsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.settings.ui.SettingsViewModel
import com.d4rk.android.libs.apptoolkit.app.settings.utils.interfaces.SettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AboutSettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AdvancedSettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.DisplaySettingsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.GeneralSettingsContentProvider
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.PrivacySettingsProvider
import com.d4rk.cleaner.app.settings.settings.utils.providers.AppAboutSettingsProvider
import com.d4rk.cleaner.app.settings.settings.utils.providers.AppAdvancedSettingsProvider
import com.d4rk.cleaner.app.settings.settings.utils.providers.AppBuildInfoProvider
import com.d4rk.cleaner.app.settings.settings.utils.providers.AppDisplaySettingsProvider
import com.d4rk.cleaner.app.settings.settings.utils.providers.AppPrivacySettingsProvider
import com.d4rk.cleaner.app.settings.settings.utils.providers.AppSettingsProvider
import com.d4rk.cleaner.app.settings.settings.utils.providers.AppSettingsScreens
import com.d4rk.cleaner.app.settings.settings.utils.providers.PermissionsSettingsProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsProvider> { AppSettingsProvider() }

    viewModel {
        SettingsViewModel(settingsProvider = get() , dispatcherProvider = get())
    }

    single<AboutSettingsProvider> { AppAboutSettingsProvider(context = get()) }
    single<AdvancedSettingsProvider> { AppAdvancedSettingsProvider(context = get()) }
    single<DisplaySettingsProvider> { AppDisplaySettingsProvider(context = get()) }
    single<PrivacySettingsProvider> { AppPrivacySettingsProvider(context = get()) }
    single<BuildInfoProvider> { AppBuildInfoProvider(context = get()) }
    single { AppSettingsScreens() }

    single<GeneralSettingsContentProvider> { GeneralSettingsContentProvider(deviceProvider = get() , advancedProvider = get() , displayProvider = get() , privacyProvider = get() , configProvider = get() , customScreens = get<AppSettingsScreens>().customScreens) }
    viewModel {
        GeneralSettingsViewModel()
    }

    single<PermissionsProvider> { PermissionsSettingsProvider() }
    viewModel {
        PermissionsViewModel(settingsProvider = get() , dispatcherProvider = get())
    }

    single<ConsentRepository> { ConsentRepositoryImpl(context = androidContext()) }
    single<LoadConsentInfoUseCase> { LoadConsentInfoUseCase(repository = get()) }
    viewModel {
        AdsSettingsViewModel(loadConsentInfoUseCase = get() , dispatcherProvider = get())
    }

    viewModel {
        AboutViewModel()
    }
}