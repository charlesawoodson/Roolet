package com.charlesawoodson.roolet.contacts

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.charlesawoodson.roolet.contacts.model.Contact
import com.charlesawoodson.roolet.contacts.model.GroupMember
import com.charlesawoodson.roolet.contacts.model.Phone
import com.charlesawoodson.roolet.contacts.repository.ContactsRepository
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.db.DatabaseHelperImpl
import com.charlesawoodson.roolet.db.Group
import com.charlesawoodson.roolet.extensions.updateItems
import com.charlesawoodson.roolet.lists.SelectableListItem
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Parcelize
data class EditGroupArgs(
    val group: Group? = null
) : Parcelable

data class ContactsState(
    val allContacts: List<SelectableListItem<Contact>> = emptyList(),
    val filteredContacts: List<SelectableListItem<Contact>> = emptyList(),
    val groupMembers: List<GroupMember> = emptyList(),
    val filter: String = "",
    val hasContactsPermission: Boolean = false
) : MvRxState

class ContactsViewModel(
    initialState: ContactsState,
    private val contactsRepository: ContactsRepository,
    private val dbHelper: DatabaseHelperImpl,
    editGroupArgs: EditGroupArgs
) : BaseMvRxViewModel<ContactsState>(initialState, true) {

    private val selectedIds = editGroupArgs.group?.members?.map { it.id }?.toSet() ?: emptySet()
    val selectedGroupId = editGroupArgs.group?.groupId

    init {
        editGroupArgs.group?.members?.let {
            setState {
                copy(groupMembers = it)
            }
        }

        selectSubscribe(ContactsState::hasContactsPermission) { hasPermission ->
            if (hasPermission) {
                fetchContacts()
            }
        }

        selectSubscribe(ContactsState::allContacts, ContactsState::filter) { contacts, filter ->
            if (contacts.isNotEmpty()) {
                val filteredContacts = contacts.filter { it.data.name.contains(filter, true) }

                val groupMembers = contacts
                    .filter { it.selected && it.data.selectedPhone != null }
                    .map {
                        val contact = it.data
                        GroupMember(
                            contact.id,
                            contact.name,
                            contact.photoUri,
                            contact.selectedPhone?.number!!,
                            contact.selectedPhone?.type!!
                        )
                    }

                setState {
                    copy(filteredContacts = filteredContacts, groupMembers = groupMembers)
                }
            }
        }
    }

    fun setHasContactsPermissions(hasPermissions: Boolean) {
        setState {
            copy(hasContactsPermission = hasPermissions)
        }
    }

    private fun fetchContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { contactsRepository.getPhoneContacts() }
            val contactNumbersAsync = async { contactsRepository.getContactNumbers() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()

            contacts.forEach {
                contactNumbers[it.id]?.let { phones ->
                    it.phones = phones
                    it.selectedPhone = phones[0]
                }
            }

            setState {
                copy(allContacts = contacts
                    .filter { it.phones.isNotEmpty() && it.selectedPhone != null }
                    .map {
                        SelectableListItem(
                            it,
                            selected = selectedIds.contains(it.id)
                        )
                    })
            }
        }
    }

    fun setFilter(filter: String) {
        setState {
            copy(filter = filter)
        }
    }

    fun toggleSelection(contactId: Long) {
        setState {
            copy(
                allContacts = allContacts.updateItems({ it.data.id == contactId }, {
                    copy(selected = !selected)
                })
            )
        }
    }

    fun setSelectedPhone(contactId: Long, selectedPhone: Phone) {
        setState {
            copy(
                allContacts = allContacts.updateItems({ it.data.id == contactId }, {
                    copy(
                        selected = true,
                        data = data.copy(selectedPhone = selectedPhone)
                    )
                })
            )
        }
    }

    fun saveGroup(group: Group) {
        viewModelScope.launch {
            dbHelper.insertGroup(group)
        }
    }

    fun deleteGroup(groupId: Long) {
        viewModelScope.launch {
            dbHelper.deleteGroupById(groupId)
        }
    }

    companion object : MvRxViewModelFactory<ContactsViewModel, ContactsState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: ContactsState
        ): ContactsViewModel {
            val context = viewModelContext.activity.applicationContext
            val dbHelper =
                DatabaseHelperImpl(DatabaseBuilder.getInstance(context))

            val group = viewModelContext.args<EditGroupArgs>()

            return ContactsViewModel(
                state,
                ContactsRepository(context),
                dbHelper,
                group
            )
        }
    }
}