package com.d4rk.cleaner.app.contacts.cleaner.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import android.os.Build
import androidx.annotation.RequiresApi
import android.telephony.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.d4rk.cleaner.app.contacts.cleaner.domain.data.model.RawContactInfo

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class ContactsRepositoryImpl(private val context: Context) : ContactsRepository {
    private val resolver = context.contentResolver

    override suspend fun findDuplicates(): List<List<RawContactInfo>> = withContext(Dispatchers.IO) {
        val map = mutableMapOf<String, MutableList<RawContactInfo>>()
        val projection = arrayOf(
            ContactsContract.RawContacts._ID,
            ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.ContactsColumns.CONTACT_LAST_UPDATED_TIMESTAMP,
            ContactsContract.RawContacts.ACCOUNT_TYPE,
            ContactsContract.RawContacts.ACCOUNT_NAME
        )
        resolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, null, null, null)?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts._ID)
            val contactIdIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.CONTACT_ID)
            val nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)
            val updatedIdx = cursor.getColumnIndexOrThrow(ContactsContract.ContactsColumns.CONTACT_LAST_UPDATED_TIMESTAMP)
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
                        val num = PhoneNumberUtils.normalizeNumber(pCur.getString(numIdx)) ?: continue
                        phones.add(num)
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
                val key = (phones.sorted().joinToString(",") + "|" + emails.sorted().joinToString(",")).ifEmpty { name.lowercase() }
                val info = RawContactInfo(contactId, rawId, name, accountType, accountName, updated, phones, emails)
                map.getOrPut(key) { mutableListOf() }.add(info)
            }
        }
        map.values.filter { it.size > 1 }
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
