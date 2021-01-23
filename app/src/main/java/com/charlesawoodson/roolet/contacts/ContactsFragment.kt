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

    var phones: Map<Long, ArrayList<String>> = HashMap()
    var contacts: ArrayList<Contact> = ArrayList()


    private val PROJECTION_NUMBERS: Array<out String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    private val PROJECTION_DETAILS: Array<out String> = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
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
        when (loader.id) {
            0 -> {
                phones = HashMap()
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        val contactId = data.getLong(0)
                        val phone = data.getString(1)
                        var list: ArrayList<String>
                        if (phones.containsKey(contactId)) {
                            list = phones.getValue(contactId)
                        } else {
                            list = ArrayList()
                            (phones as HashMap<Long, ArrayList<String>>)[contactId] = list
                        }
                        list.add(phone)
                    }
                    data.close()
                }
                LoaderManager.getInstance(this).initLoader(1, null, this)
            }
            1 -> {
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        val contactId = data.getLong(0)
                        val name = data.getString(1)
                        val photo = data.getString(2)
                        val contactPhones: List<String>? = phones[contactId]
                        contactPhones?.forEach { phone ->
                            contacts.add(Contact(contactId, name, phone, photo))
                        }
                    }
                    data.close()
                    // todo: load adapter
                }
            }
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