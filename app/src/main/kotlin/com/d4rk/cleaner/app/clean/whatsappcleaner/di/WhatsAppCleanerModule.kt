package com.d4rk.cleaner.app.clean.whatsappcleaner.di

import com.d4rk.cleaner.app.clean.whatsappcleaner.data.WhatsAppCleanerRepositoryImpl
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.interface.WhatsAppCleanerRepository
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.usecases.DeleteWhatsAppMediaUseCase
import com.d4rk.cleaner.app.clean.whatsappcleaner.domain.usecases.GetWhatsAppMediaSummaryUseCase
import com.d4rk.cleaner.app.clean.whatsappcleaner.ui.WhatsAppCleanerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val whatsAppCleanerModule: Module = module {
    single<WhatsAppCleanerRepository> { WhatsAppCleanerRepositoryImpl(application = androidApplication()) }
    single { GetWhatsAppMediaSummaryUseCase(repository = get()) }
    single { DeleteWhatsAppMediaUseCase(repository = get()) }
    viewModel { WhatsAppCleanerViewModel(getSummaryUseCase = get(), deleteUseCase = get(), dispatchers = get()) }
}
