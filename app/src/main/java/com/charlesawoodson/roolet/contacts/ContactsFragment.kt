package com.charlesawoodson.roolet.contacts

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.charlesawoodson.roolet.R

class ContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    var phones: Map<Long, List<String>> = HashMap()
    var contacts: List<Contact> = ArrayList()


    private val PROJECTION_NUMBERS: Array<out String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    )

    private val PROJECTION_DETAILS: Array<out String> = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME
    )

    // private val SELECTION: String = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?" SQL QUERY TABLE RETURNED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // Called immediately after initLoader()

        // todo: Start Loading

        when (id) {
            0 -> {
                return CursorLoader(
                    requireContext(),
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION_NUMBERS,
                    null,
                    null,
                    null
                )
            }
            else -> {
                return CursorLoader(
                    requireContext(),
                    ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION_DETAILS,
                    null,
                    null,
                    null
                )
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // The loader framework calls onLoadFinished() when the Contacts Provider returns the results of the query
        data?.also { cursor ->
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                    )).toInt()


                    if (phoneNumber > 0) {
                        val cursorPhone = activity?.contentResolver?.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            arrayOf(id),
                            null
                        )

                        cursorPhone?.also {
                            if (cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.getString(
                                        cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    )
                                }
                            }
                            cursorPhone.close()
                        }
                    }

                }
                cursor.close()
            }

            // cursorAdapter?.swapCursor(cursor)
        }
    }

    /**
     * This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        // cursorAdapter?.swapCursor(null)
    }


}