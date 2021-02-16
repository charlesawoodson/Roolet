package com.charlesawoodson.roolet.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.text.toSpannable
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.adapters.ContactsAdapter
import com.charlesawoodson.roolet.contacts.adapters.GroupMembersAdapter
import com.charlesawoodson.roolet.contacts.dialogs.*
import com.charlesawoodson.roolet.contacts.model.Contact
import com.charlesawoodson.roolet.db.Group
import com.charlesawoodson.roolet.groups.GroupsActivity
import com.charlesawoodson.roolet.groups.dialogs.GroupsTutorialDialogFragment
import com.charlesawoodson.roolet.lists.SelectableListItem
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(), ContactsAdapter.OnContactsItemClickListener {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
    }

    private val arguments: EditGroupArgs by args() // todo: create NullableArgs<T> wrapper

    private val viewModel: ContactsViewModel by fragmentViewModel()

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        ContactsAdapter(this)
    }

    private val groupMembersAdapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        GroupMembersAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectSubscribe(ContactsState::allContacts) { allContacts ->
            progressSpinner.isVisible = allContacts.isEmpty()
        }

        viewModel.selectSubscribe(ContactsState::filteredContacts) { filteredContacts ->
            adapter.updateData(filteredContacts)
        }

        viewModel.selectSubscribe(ContactsState::groupMembers) { groupMembers ->
            groupMembersAdapter.updateData(groupMembers)
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

        setupRecyclerViews()
        setOnClickListeners()

        filterEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.setFilter(text.toString())
        }

        filterEditText.setOnFocusChangeListener { _, hasFocus ->
            cancelTextView.isVisible = hasFocus
        }

        backImageView.isVisible = arguments.group == null
        deleteGroupTextView.isGone = arguments.group == null

        arguments.group?.title?.also { title ->
            groupTitleEditText.setText(title.toSpannable())
        }

        if (!sharedPreferences.getBoolean(getString(R.string.contacts_tutorial_seen_pref), false)) {
            ContactsTutorialDialogFragment().show(childFragmentManager, null)
        }
    }


    private fun setOnClickListeners() {
        saveGroupTextView.setOnClickListener {
            saveGroup()
        }

        backImageView.setOnClickListener {
            requireActivity().finish()
        }

        deleteGroupTextView.setOnClickListener {
            arguments.group?.groupId?.also {
                viewModel.deleteGroup(it)
            }
            Intent(requireContext(), GroupsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(this)
            }
        }

        cancelTextView.setOnClickListener {
            filterEditText.text.clear()
            filterEditText.clearFocus()
            closeKeyboard()
        }
    }

    private fun setupRecyclerViews() {
        contactsRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        contactsRecyclerView.adapter = adapter

        groupMembersRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        groupMembersRecyclerView.adapter = groupMembersAdapter
    }

    override fun toggleSelection(contact: SelectableListItem<Contact>) {
        if (contact.data.phones.size == 1 || contact.selected) {
            viewModel.toggleSelection(contact.data.id)
        } else {
            showPhoneSelectionDialog(contact)
        }
    }

    private fun showPhoneSelectionDialog(contact: SelectableListItem<Contact>) {
        SelectPhoneDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(
                    MvRx.KEY_ARG,
                    SelectPhoneArgs(contact.data.id, contact.data.phones)
                )
            }
        }.show(childFragmentManager, null)
    }

    private fun saveGroup() {
        withState(viewModel) { state ->
            when {
                state.groupMembers.isEmpty() -> {
                    showErrorDialog(R.string.no_members, R.string.add_people_to_party)
                }
                groupTitleEditText.text.isBlank() -> {
                    showErrorDialog(R.string.no_party_name, R.string.add_name_for_party)
                }
                else -> {
                    // todo: improve this
                    val group = if (arguments.group?.groupId != null) {
                        Group(
                            groupId = arguments.group?.groupId!!,
                            title = groupTitleEditText.text.toString(),
                            members = state.groupMembers
                        )
                    } else {
                        Group(
                            title = groupTitleEditText.text.toString(),
                            members = state.groupMembers
                        )
                    }
                    viewModel.saveGroup(group)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun showErrorDialog(titleRes: Int, descriptionRes: Int) {
        ErrorDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(MvRx.KEY_ARG, ErrorDialogArgs(titleRes, descriptionRes))
            }
        }.show(childFragmentManager, null)
    }

    private fun closeKeyboard() {
        val imm =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

}