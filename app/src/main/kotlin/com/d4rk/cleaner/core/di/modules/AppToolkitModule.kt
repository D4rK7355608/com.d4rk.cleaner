package com.d4rk.cleaner.core.di.modules

import com.d4rk.android.libs.apptoolkit.app.help.domain.model.ui.HelpScreenConfig
import com.d4rk.android.libs.apptoolkit.app.help.domain.usecases.GetFAQsUseCase
import com.d4rk.android.libs.apptoolkit.app.help.domain.usecases.LaunchReviewFlowUseCase
import com.d4rk.android.libs.apptoolkit.app.help.domain.usecases.RequestReviewFlowUseCase
import com.d4rk.android.libs.apptoolkit.app.help.ui.HelpViewModel
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupViewModel
import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import com.d4rk.android.libs.apptoolkit.app.support.domain.usecases.QueryProductDetailsUseCase
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportViewModel
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.app.startup.utils.interfaces.providers.AppStartupProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appToolkitModule : Module = module {
    single<StartupProvider> { AppStartupProvider() }

    viewModel {
        StartupViewModel(loadConsentInfoUseCase = get() , dispatcherProvider = get())
    }

    single<QueryProductDetailsUseCase> { QueryProductDetailsUseCase() }
    viewModel {
        SupportViewModel(queryProductDetailsUseCase = get() , dispatcherProvider = get())
    }

    single<HelpScreenConfig> { HelpScreenConfig(versionName = BuildConfig.VERSION_NAME , versionCode = BuildConfig.VERSION_CODE) }
    single<GetFAQsUseCase> { GetFAQsUseCase(application = get()) }
    single<RequestReviewFlowUseCase> { RequestReviewFlowUseCase(application = get()) }
    single<LaunchReviewFlowUseCase> { LaunchReviewFlowUseCase() }

    viewModel {
        HelpViewModel(getFAQsUseCase = get() , requestReviewFlowUseCase = get() , launchReviewFlowUseCase = get() , dispatcherProvider = get())
    }
}