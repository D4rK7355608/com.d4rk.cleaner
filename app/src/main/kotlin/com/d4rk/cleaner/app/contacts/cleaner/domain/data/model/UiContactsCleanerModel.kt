package com.d4rk.cleaner.app.contacts.cleaner.domain.data.model

data class UiContactsCleanerModel(
    val duplicates: List<List<RawContactInfo>> = emptyList()
)
