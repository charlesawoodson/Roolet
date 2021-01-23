package com.charlesawoodson.roolet.repository

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.charlesawoodson.roolet.repository.DatabaseQueryRepository.Companion.CONTACTS_QUERY_TOKEN


class QueryHandler(cr: ContentResolver?) : AsyncQueryHandler(cr) {

    // todo: dataHolder? initialize in constructor?

    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
        val mutableData: MutableLiveData<*>? = cookie as? MutableLiveData<String>
        try {
            when (token) {
                CONTACTS_QUERY_TOKEN -> {
                    cursor?.also {

                        if (cursor.count > 0) {
                            while (cursor.moveToNext()) {
                                val id =
                                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                                val name =
                                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                                val phoneNumber = (cursor.getString(
                                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                                )).toInt()
                            }
                        }
                    }
                }
            }
        } finally {
            cursor?.close()
        }
    }

}

