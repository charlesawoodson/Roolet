package com.charlesawoodson.roolet.contacts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.charlesawoodson.roolet.Group
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.lists.SelectableListItem
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(), ContactsAdapter.OnContactsItemClickListener {

    private val viewModel: ContactsViewModel by fragmentViewModel()

    private val dialog by lazy(mode = LazyThreadSafetyMode.NONE) {
        SelectPhoneDialogFragment()
    }

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        ContactsAdapter(this)
    }

    private val selectedContactsAdapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        SelectedContactsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectSubscribe(ContactsState::filteredContacts) { contacts ->
            progressSpinner.isGone = true
            adapter.updateData(contacts)
        }

        viewModel.selectSubscribe(ContactsState::groupMembers) { selectedContacts ->
            selectedContactsAdapter.updateData(selectedContacts)
        }

        viewModel.selectSubscribe(ContactsState::selectedContact) { selectedContact ->
            if (!dialog.isVisible && selectedContact != null) {
                dialog.show(childFragmentManager, "SelectPhoneFragment")
            }
        }

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
        contactsRecyclerView.adapter = adapter

        groupMembersRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        groupMembersRecyclerView.adapter = selectedContactsAdapter

        // todo: map group members in view model
        saveGroupTextView.setOnClickListener {
            withState(viewModel) { state ->
                viewModel.saveGroup(
                    Group(
                        title = groupNameEditText.text.toString(),
                        members = state.groupMembers
                            .map {
                                GroupMember(
                                    it.name,
                                    it.photoUri,
                                    it.selectedPhone?.number!!,
                                    it.selectedPhone?.type!!
                                )
                            }
                    )
                )
            }
        }

        backImageView.setOnClickListener {
            requireActivity().finish()
        }

        filterEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.setFilter(text.toString())
        }

        filterEditText.setOnFocusChangeListener { _, hasFocus ->
            cancelTextView.isVisible = hasFocus
            closeKeyboard(hasFocus)
        }

        groupNameEditText.setOnFocusChangeListener { v, hasFocus ->
            closeKeyboard(hasFocus)
        }

        cancelTextView.setOnClickListener {
            filterEditText.text.clear()
            filterEditText.clearFocus()
        }
    }

    override fun toggleSelection(contact: SelectableListItem<Contact>) {
        if (contact.data.phones.size == 1 || contact.selected) {
            viewModel.toggleSelection(contact)
        } else {
            viewModel.setSelectedContact(contact.data)
        }
    }

    private fun closeKeyboard(hasFocus: Boolean) {
        if (!hasFocus) {
            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

}