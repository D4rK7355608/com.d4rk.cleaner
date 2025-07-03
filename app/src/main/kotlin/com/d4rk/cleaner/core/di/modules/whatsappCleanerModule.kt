package com.d4rk.cleaner.core.di.modules

import com.d4rk.cleaner.app.clean.whatsapp.summary.data.WhatsAppCleanerRepositoryImpl
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.repository.WhatsAppCleanerRepository
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.usecases.DeleteWhatsAppMediaUseCase
import com.d4rk.cleaner.app.clean.whatsapp.summary.domain.usecases.GetWhatsAppMediaSummaryUseCase
import com.d4rk.cleaner.app.clean.whatsapp.summary.ui.WhatsAppCleanerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val whatsappCleanerModule: Module = module {
    single<WhatsAppCleanerRepository> { WhatsAppCleanerRepositoryImpl(androidApplication()) }
    single { GetWhatsAppMediaSummaryUseCase(repository = get()) }
    single { DeleteWhatsAppMediaUseCase(repository = get()) }
    viewModel { WhatsAppCleanerViewModel(getSummaryUseCase = get(), deleteUseCase = get(), dispatchers = get()) }
}
