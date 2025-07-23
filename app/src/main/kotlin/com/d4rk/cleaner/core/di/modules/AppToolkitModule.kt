package com.d4rk.cleaner.core.di.modules

import com.d4rk.android.libs.apptoolkit.app.help.domain.data.model.HelpScreenConfig
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.github.GithubTarget
import com.d4rk.android.libs.apptoolkit.app.issuereporter.ui.IssueReporterViewModel
import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import com.d4rk.android.libs.apptoolkit.app.support.billing.BillingRepository
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.github.GithubConstants
import com.d4rk.cleaner.BuildConfig
import com.d4rk.cleaner.app.startup.utils.interfaces.providers.AppStartupProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appToolkitModule : Module = module {
    single<StartupProvider> { AppStartupProvider() }

    single(createdAtStart = true) { BillingRepository.getInstance(context = get()) }
    viewModel {
        SupportViewModel(billingRepository = get(), dispatcherProvider = get())
    }

    viewModel {
        IssueReporterViewModel(
            dispatcherProvider = get(),
            httpClient = get(),
            githubTarget = get(),
            githubToken = get(named("github_token"))
        )
    }

    single(qualifier = named(name = "github_repository")) { "Smart-Cleaner-for-Android" }
    single<GithubTarget> {
        GithubTarget(
            username = GithubConstants.GITHUB_USER,
            repository = get(qualifier = named(name = "github_repository")),
        )
    }

    single(qualifier = named("github_changelog")) {
        GithubConstants.githubChangelog(get<String>(qualifier = named(name = "github_repository")))
    }

    single(qualifier = named("github_token")) { BuildConfig.GITHUB_TOKEN }

    single<HelpScreenConfig> { HelpScreenConfig(versionName = BuildConfig.VERSION_NAME , versionCode = BuildConfig.VERSION_CODE) }
}