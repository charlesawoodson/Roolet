package com.charlesawoodson.roolet.contacts.repository

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.charlesawoodson.roolet.contacts.model.Contact
import com.charlesawoodson.roolet.contacts.model.Phone


class ContactsRepository(private val applicationContext: Context) {

    suspend fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()

        val contactsCursor = applicationContext.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val photoUriIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getLong(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val photoUri = contactsCursor.getString(photoUriIndex)
                if (name != null) {
                    contactsList.add(Contact(id, name, photoUri))
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    suspend fun getContactNumbers(): HashMap<Long, ArrayList<Phone>> {
        val contactsNumberMap = HashMap<Long, ArrayList<Phone>>()
        val phoneCursor: Cursor? = applicationContext.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val typeIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)

            val numberSet = mutableSetOf<String>()

            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getLong(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                val type: Int = phoneCursor.getInt(typeIndex)

                val phone = Phone(number, type)

                // todo: does not currently support multiple contacts w same phone numbers (support?)
                if (!numberSet.contains(number)) {
                    if (contactsNumberMap.containsKey(contactId)) {
                        contactsNumberMap[contactId]?.add(phone)
                    } else {
                        contactsNumberMap[contactId] = arrayListOf(phone)
                    }
                    numberSet.add(number)
                }
                // contact contains all the numbers of a particular contact
            }
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    suspend fun getContactEmails(): HashMap<String, ArrayList<String>> {
        val contactsEmailMap = HashMap<String, ArrayList<String>>()
        val emailCursor = applicationContext.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex =
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex =
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                //check if the map contains key or not, if not then create a new array list with email
                if (contactsEmailMap.containsKey(contactId)) {
                    contactsEmailMap[contactId]?.add(email)
                } else {
                    contactsEmailMap[contactId] = arrayListOf(email)
                }
            }
            //contact contains all the emails of a particular contact
            emailCursor.close()
        }
        return contactsEmailMap
    }
}