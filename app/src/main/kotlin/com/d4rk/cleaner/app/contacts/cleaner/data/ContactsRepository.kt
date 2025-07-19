package com.d4rk.cleaner.app.contacts.cleaner.data

import com.d4rk.cleaner.app.contacts.cleaner.domain.data.model.RawContactInfo

interface ContactsRepository {
    suspend fun findDuplicates(): List<List<RawContactInfo>>
    suspend fun deleteOlder(group: List<RawContactInfo>)
    suspend fun mergeContacts(group: List<RawContactInfo>)
}
