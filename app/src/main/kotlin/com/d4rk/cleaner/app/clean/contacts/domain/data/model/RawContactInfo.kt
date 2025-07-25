package com.d4rk.cleaner.app.clean.contacts.domain.data.model

data class RawContactInfo(
    val contactId: Long,
    val rawContactId: Long,
    val displayName: String,
    val accountType: String?,
    val accountName: String?,
    val lastUpdated: Long,
    val phones: List<String>,
    val emails: List<String>,
    val isSelected: Boolean = false
)
