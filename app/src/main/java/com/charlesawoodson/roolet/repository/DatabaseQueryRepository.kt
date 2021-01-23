package com.charlesawoodson.roolet.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract

import androidx.lifecycle.MutableLiveData


class DatabaseQueryRepository {

//    fun newInstance(): DatabaseQueryRepository {
//        return DatabaseQueryRepository()
//    }

    private var queryHandler: QueryHandler? = null


    companion object {
        const val CONTACTS_QUERY_TOKEN = 0
    }

    private val PROJECTION: Array<out String> = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.HAS_PHONE_NUMBER
    )

    private val SELECTION: String = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"

    // Defines a variable for the search string
    private val searchString: String = ""

    // Defines the array to hold values that replace the ?
    private val selectionArgs = arrayOf(searchString)

    // Use id in some way, but we're not in this example
    fun fetchData(contentResolver: ContentResolver?, id: Long): MutableLiveData<String> {
        queryHandler = QueryHandler(contentResolver)

        return query(
            CONTACTS_QUERY_TOKEN,
            ContactsContract.Contacts.CONTENT_URI,
            PROJECTION,
            SELECTION,
            selectionArgs,
            null
        )
    }

    private fun query(
        token: Int,
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortBy: String?
    ): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        // Pass MutableLiveData in as a cookie, so we can set the result
        // in OnQueryComplete
        queryHandler?.startQuery(token, result, uri, projection, selection, selectionArgs, sortBy)
        return result
    }

}