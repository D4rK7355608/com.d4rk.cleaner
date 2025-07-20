package com.d4rk.cleaner.app.clean.contacts.domain.data.model

data class DuplicateContactGroup(
    val contacts: List<RawContactInfo>,
    val isSelected: Boolean = false
)

data class UiContactsCleanerModel(
    val duplicates: List<DuplicateContactGroup> = emptyList()
)
