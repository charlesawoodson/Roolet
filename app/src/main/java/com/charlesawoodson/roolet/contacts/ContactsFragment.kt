package com.charlesawoodson.roolet.contacts

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.*
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.extensions.removeItem
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private val viewModel: ContactsViewModel by fragmentViewModel()

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        ContactsAdapter()
    }

    private val PROJECTION_NUMBERS: Array<out String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    private val PROJECTION_DETAILS: Array<out String> = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectSubscribe(ContactsState::filteredContacts) { contacts ->
            progressSpinner.isGone = true
            adapter.updateData(contacts)
        }

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

        contactsRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        contactsRecyclerView.itemAnimator = DefaultItemAnimator()
        contactsRecyclerView.adapter = adapter

//        withState(viewModel) { state ->
//            adapter.updateDataImmediate(state.filteredItems)
//        }


        addGroupImageView.setOnClickListener {
            viewModel.setFilter(" ")
        }


        settingsImageView.setOnClickListener {
            viewModel.setFilter("N")
        }

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

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
                viewModel.phones = HashMap()
                if (data != null) {
                    while (!data.isClosed && data.moveToNext()) {
                        val contactId = data.getLong(0)
                        val phone = data.getString(1)
                        var list: ArrayList<String>
                        if (viewModel.phones.containsKey(contactId)) {
                            list = viewModel.phones.getValue(contactId)
                        } else {
                            list = ArrayList()
                            (viewModel.phones as HashMap<Long, ArrayList<String>>)[contactId] = list
                        }
                        list.add(phone)
                    }
                    data.close()
                }
                LoaderManager.getInstance(this).initLoader(1, null, this)
            }
            1 -> {
                if (data != null) {
                    val contacts: ArrayList<Contact> = ArrayList()
                    while (!data.isClosed && data.moveToNext()) {
                        val contactId = data.getLong(0)
                        val name = data.getString(1)
                        val photo = data.getString(2)
                        val contactPhones: List<String>? = viewModel.phones[contactId]

                        contactPhones?.forEach { phone ->
                            contacts.add(Contact(contactId, name, phone, photo))
                        }
                    }
                    viewModel.setContacts(contacts.sortedBy { it.name })
                    data.close()
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

    }

}