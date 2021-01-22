package com.charlesawoodson.roolet.contacts

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.charlesawoodson.roolet.R

class ContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    private val FROM_COLUMNS: Array<String> = arrayOf(
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY // todo: put more columns later
    )

    private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)

    // Define global mutable variables
    // Define a ListView object
    lateinit var contactsList: ListView

    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0

    // The contact's LOOKUP_KEY
    var contactKey: String? = null

    // A content URI for the selected contact
    var contactUri: Uri? = null

    // An adapter that binds the result Cursor to the ListView
    private var cursorAdapter: SimpleCursorAdapter? = null

    private val PROJECTION: Array<out String> = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    )

    companion object {
        // The column index for the _ID column
        private const val CONTACT_ID_INDEX: Int = 0

        // The column index for the CONTACT_KEY column
        private const val CONTACT_KEY_INDEX: Int = 1
    }

    private val SELECTION: String = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"

    // Defines a variable for the search string
    private val searchString: String = ""

    // Defines the array to hold values that replace the ?
    private val selectionArgs = arrayOf(searchString)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Gets the ListView from the View list of the parent activity
        activity?.also {
            contactsList = it.findViewById<ListView>(R.id.contact_list_view)
            // Gets a CursorAdapter
            cursorAdapter = SimpleCursorAdapter(
                it,
                R.layout.contact_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0
            )
            // Sets the adapter for the ListView
            contactsList.adapter = cursorAdapter
        }

        contactsList.onItemClickListener = this
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     *
     * This will always be called from the process's main thread.
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // Called immediately after initLoader()
        /*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        selectionArgs[0] = "%$searchString%"
        // Starts the query
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
            )
        } ?: throw IllegalStateException()
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is *not* allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See [ FragmentManager.openTransaction()][androidx.fragment.app.FragmentManager.beginTransaction] for further discussion on this.
     *
     *
     * This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     *
     *
     *  *
     *
     *The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a [android.database.Cursor]
     * and you place it in a [android.widget.CursorAdapter], use
     * the [android.widget.CursorAdapter.CursorAdapter] constructor *without* passing
     * in either [android.widget.CursorAdapter.FLAG_AUTO_REQUERY]
     * or [android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER]
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     *  *  The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a [android.database.Cursor] from a [android.content.CursorLoader],
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * [android.widget.CursorAdapter], you should use the
     * [android.widget.CursorAdapter.swapCursor]
     * method so that the old Cursor is not closed.
     *
     *
     *
     * This will always be called from the process's main thread.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // The loader framework calls onLoadFinished() when the Contacts Provider returns the results of the query
        data?.also { cursor ->
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

//                    if (phoneNumber > 0) {
//                        val cursorPhone = contentResolver.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)
                }
            }
            cursor.close()
        }

        cursorAdapter?.swapCursor(data)
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     *
     * This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        cursorAdapter?.swapCursor(null)
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     *
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this
     * will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Get the Cursor
        val cursor: Cursor? = (parent?.adapter as? CursorAdapter)?.cursor?.apply {
            // Move to the selected contact
            moveToPosition(position)
            // Get the _ID value
            contactId = getLong(CONTACT_ID_INDEX)
            // Get the selected LOOKUP KEY
            contactKey = getString(CONTACT_KEY_INDEX)
            // Create the contact's content Uri
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
            /*
             * You can use contactUri as the content URI for retrieving
             * the details for a contact.
             */
        }
    }
}