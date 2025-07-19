package com.d4rk.cleaner.core.di.modules

import com.d4rk.cleaner.app.clean.contacts.data.ContactsRepository
import com.d4rk.cleaner.app.clean.contacts.data.ContactsRepositoryImpl
import com.d4rk.cleaner.app.clean.contacts.ui.ContactsCleanerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val contactsModule: Module = module {
    single<ContactsRepository> { ContactsRepositoryImpl(context = androidContext()) }
    viewModel { ContactsCleanerViewModel(repository = get(), dispatchers = get()) }
}
