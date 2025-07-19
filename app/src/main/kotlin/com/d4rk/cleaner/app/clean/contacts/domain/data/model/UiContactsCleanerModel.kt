package com.d4rk.cleaner.app.clean.contacts.domain.data.model

data class UiContactsCleanerModel(
    val duplicates: List<List<RawContactInfo>> = emptyList()
)
