package com.charlesawoodson.roolet.contacts

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.charlesawoodson.roolet.contacts.model.Contact
import com.charlesawoodson.roolet.contacts.model.GroupMember
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
data class GroupArgs(
    val group: Group? = null
) : Parcelable

data class ContactsState(
    val allContacts: List<SelectableListItem<Contact>> = emptyList(),
    val filteredContacts: List<SelectableListItem<Contact>> = emptyList(),
    val groupMembers: List<GroupMember> = emptyList(),
    val dialogContact: Contact? = null,
    val filter: String = ""
) : MvRxState

class ContactsViewModel(
    initialState: ContactsState,
    private val contactsRepository: ContactsRepository,
    private val dbHelper: DatabaseHelperImpl,
    groupArgs: GroupArgs?
) : BaseMvRxViewModel<ContactsState>(initialState, true) {

    private val selectedIds = groupArgs?.group?.members?.map { it.id }?.toSet()

    init {
        fetchContacts()

        selectSubscribe(ContactsState::allContacts, ContactsState::filter) { contacts, filter ->
            val filteredContacts = contacts.filter { it.data.name.contains(filter, true) }

            val groupMembers = contacts
                .filter { it.selected && it.data.selectedPhone != null }
                .map { it.data }
                .map {
                    GroupMember(
                        it.id,
                        it.name,
                        it.photoUri,
                        it.selectedPhone?.number!!,
                        it.selectedPhone?.type!!
                    )
                }

            setState {
                copy(filteredContacts = filteredContacts, groupMembers = groupMembers)
            }
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
                            selected = selectedIds?.contains(it.id) == true
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

    fun toggleSelection(item: SelectableListItem<Contact>) {
        setState {
            copy(
                allContacts = allContacts.updateItems({ it.data.id == item.data.id }, {
                    copy(
                        selected = !selected,
                        data = data.copy(selectedPhone = item.data.phones[0])
                    )
                })
            )
        }
    }

    fun addSelectedContact(contact: Contact) {
        setState {
            copy(
                allContacts = allContacts.updateItems({ it.data.id == contact.id }, {
                    copy(
                        selected = true,
                        data = data.copy(selectedPhone = contact.selectedPhone)
                    )
                })
            )
        }
    }

    fun setDialogContact(contact: Contact?) {
        setState {
            copy(dialogContact = contact)
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

            val group = viewModelContext.args<GroupArgs>()

            return ContactsViewModel(
                state,
                ContactsRepository(viewModelContext.activity.applicationContext),
                dbHelper,
                group
            )
        }
    }
}