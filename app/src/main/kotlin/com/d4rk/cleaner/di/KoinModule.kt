package com.d4rk.cleaner.di

import com.d4rk.cleaner.app.main.data.updates.UpdateRepository
import com.d4rk.cleaner.app.main.data.updates.UpdateRepositoryImpl
import com.d4rk.cleaner.app.main.domain.updates.CheckForUpdatesUseCase
import com.d4rk.cleaner.app.main.domain.updates.CheckForUpdatesUseCaseImpl
import com.d4rk.cleaner.app.main.ui.MainViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule : Module = module {

    // Main
    single<UpdateRepository> { UpdateRepositoryImpl(get()) }
    single<CheckForUpdatesUseCase> { CheckForUpdatesUseCaseImpl(get()) }
    viewModel { MainViewModel(get()) }
}