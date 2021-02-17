package com.charlesawoodson.roolet.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
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

    init {
        selectSubscribe(ContactsState::hasContactsPermission) { hasPermission ->
            if (hasPermission) {
                fetchContacts()
            }
        }

        selectSubscribe(ContactsState::allContacts, ContactsState::filter) { contacts, filter ->
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
                    ) // todo: add phone to group member?
                }

            setState {
                copy(filteredContacts = filteredContacts, groupMembers = groupMembers)
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
                    .filter { it.phones.isNotEmpty() }
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
            val dbHelper =
                DatabaseHelperImpl(DatabaseBuilder.getInstance(viewModelContext.activity.applicationContext))

            val group = viewModelContext.args<EditGroupArgs>()

            return ContactsViewModel(
                state,
                ContactsRepository(viewModelContext.activity.applicationContext),
                dbHelper,
                group
            )
        }
    }
}