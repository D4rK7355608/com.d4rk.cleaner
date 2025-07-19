package com.d4rk.cleaner.app.clean.contacts.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import com.d4rk.cleaner.app.clean.contacts.domain.data.model.RawContactInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepositoryImpl(context: Context) : ContactsRepository {
    private val resolver = context.contentResolver

    private fun normalizePhone(number: String): String =
        number.filterIndexed { index, c -> c.isDigit() || (index == 0 && c == '+') }

    private fun normalizeName(name: String): String =
        name.filter { it.isLetterOrDigit() }.lowercase()

    private fun samePhoneNumber(a: String, b: String): Boolean {
        val na = normalizePhone(a)
        val nb = normalizePhone(b)
        if (na == nb) return true
        val da = na.trimStart('+')
        val db = nb.trimStart('+')
        return da == db || da.endsWith(db) || db.endsWith(da)
    }

    override suspend fun findDuplicates(): List<List<RawContactInfo>> {
        val contacts = withContext(Dispatchers.IO) {
            val list = mutableListOf<RawContactInfo>()
            val projection = arrayOf(
                ContactsContract.RawContacts._ID,
                ContactsContract.RawContacts.CONTACT_ID,
                ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
                ContactsContract.RawContacts.ACCOUNT_TYPE,
                ContactsContract.RawContacts.ACCOUNT_NAME
            )

            resolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, null, null, null)?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts._ID)
                val contactIdIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.CONTACT_ID)
                val nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)
                val updatedIdx = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)
                val typeIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_TYPE)
                val accIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_NAME)

                while (cursor.moveToNext()) {
                    val rawId = cursor.getLong(idIdx)
                    val contactId = cursor.getLong(contactIdIdx)
                    val name = cursor.getString(nameIdx) ?: ""
                    val updated = cursor.getLong(updatedIdx)
                    val accountType = cursor.getString(typeIdx)
                    val accountName = cursor.getString(accIdx)

                    val phones = mutableListOf<String>()
                    resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        "${ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID}=?",
                        arrayOf(rawId.toString()),
                        null
                    )?.use { pCur ->
                        val numIdx = pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        while (pCur.moveToNext()) {
                            val num = pCur.getString(numIdx)
                            val normalized = normalizePhone(num)
                            if (normalized.isNotEmpty()) phones.add(normalized)
                        }
                    }

                    val emails = mutableListOf<String>()
                    resolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                        "${ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID}=?",
                        arrayOf(rawId.toString()),
                        null
                    )?.use { eCur ->
                        val emIdx = eCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)
                        while (eCur.moveToNext()) {
                            emails.add(eCur.getString(emIdx).lowercase())
                        }
                    }

                    list.add(
                        RawContactInfo(contactId, rawId, name, accountType, accountName, updated, phones, emails)
                    )
                }
            }
            list
        }

        if (contacts.isEmpty()) return emptyList()

        return withContext(Dispatchers.Default) {
            val parent = IntArray(contacts.size) { it }

            fun find(x: Int): Int {
                var r = x
                while (parent[r] != r) r = parent[r]
                var i = x
                while (parent[i] != r) {
                    val p = parent[i]
                    parent[i] = r
                    i = p
                }
                return r
            }

            fun union(a: Int, b: Int) {
                val ra = find(a)
                val rb = find(b)
                if (ra != rb) parent[rb] = ra
            }

            fun areDuplicates(a: RawContactInfo, b: RawContactInfo): Boolean {
                a.phones.forEach { pa ->
                    b.phones.forEach { pb ->
                        if (samePhoneNumber(pa, pb)) return true
                    }
                }

                if (a.emails.isNotEmpty() && b.emails.isNotEmpty()) {
                    if (a.emails.any { it in b.emails }) return true
                }

                val nameA = normalizeName(a.displayName)
                val nameB = normalizeName(b.displayName)
                if (nameA.isNotEmpty() && nameA == nameB) {
                    if (a.phones.isEmpty() && b.phones.isEmpty() && a.emails.isEmpty() && b.emails.isEmpty()) {
                        return true
                    }
                    a.phones.forEach { pa ->
                        b.phones.forEach { pb ->
                            val digitsA = normalizePhone(pa).trimStart('+')
                            val digitsB = normalizePhone(pb).trimStart('+')
                            if (digitsA.endsWith(digitsB) || digitsB.endsWith(digitsA)) return true
                        }
                    }
                }
                return false
            }

            for (i in contacts.indices) {
                for (j in i + 1 until contacts.size) {
                    if (areDuplicates(contacts[i], contacts[j])) union(i, j)
                }
            }

            val groupsMap = mutableMapOf<Int, MutableList<RawContactInfo>>()
            contacts.forEachIndexed { index, info ->
                val root = find(index)
                groupsMap.getOrPut(root) { mutableListOf() }.add(info)
            }
            groupsMap.values.filter { it.size > 1 }
        }
    }

    override suspend fun deleteOlder(group: List<RawContactInfo>) = withContext(Dispatchers.IO) {
        val keep = group.maxByOrNull { it.lastUpdated } ?: return@withContext
        group.filter { it != keep }.forEach { info ->
            val uri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, info.rawContactId)
            resolver.delete(uri, null, null)
        }
    }

    override suspend fun mergeContacts(group: List<RawContactInfo>) = withContext(Dispatchers.IO) {
        val keep = group.maxByOrNull { it.lastUpdated } ?: return@withContext
        group.filter { it != keep }.forEach { source ->
            source.phones.forEach { phone ->
                val values = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, keep.rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                }
                resolver.insert(ContactsContract.Data.CONTENT_URI, values)
            }
            source.emails.forEach { email ->
                val values = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, keep.rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                    put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                }
                resolver.insert(ContactsContract.Data.CONTENT_URI, values)
            }
            val uri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, source.rawContactId)
            resolver.delete(uri, null, null)
        }
    }
}
