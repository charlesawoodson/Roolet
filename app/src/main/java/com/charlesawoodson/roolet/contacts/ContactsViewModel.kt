package com.charlesawoodson.roolet.contacts

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.charlesawoodson.roolet.DatabaseHelperImpl
import com.charlesawoodson.roolet.Group
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.extensions.updateItems
import com.charlesawoodson.roolet.lists.SelectableListItem
import com.charlesawoodson.roolet.repository.ContactsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class ContactsState(
    val contacts: List<SelectableListItem<Contact>> = emptyList(),
    val filteredContacts: List<SelectableListItem<Contact>> = emptyList(),
    val groupMembers: List<Contact> = emptyList(),
    val selectedContact: Contact? = null,
    val filter: String = ""
) : MvRxState

class ContactsViewModel(
    initialState: ContactsState,
    private val contactsRepository: ContactsRepository,
    private val dbHelper: DatabaseHelperImpl
) : BaseMvRxViewModel<ContactsState>(initialState, true) {

    init {
        fetchContacts()

        selectSubscribe(ContactsState::contacts, ContactsState::filter) { contacts, filter ->

            contacts.forEach {
                Log.d("ContactsViewModel", it.toString())
            }

            val filteredList = contacts.filter { it.data.name.contains(filter) }

            val selectedList = contacts
                .filter { it.selected && it.data.selectedPhone != null }
                .map { it.data }

            setState {
                copy(filteredContacts = filteredList, groupMembers = selectedList)
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
                }
            }

            setState {
                copy(contacts = contacts
                    .filter { it.phones.isNotEmpty() }
                    .map { SelectableListItem(it) })
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
                contacts = contacts.updateItems({ it.data.id == item.data.id }, {
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
                contacts = contacts.updateItems({ it.data.id == contact.id }, {
                    copy(
                        selected = true,
                        data = data.copy(selectedPhone = contact.selectedPhone)
                    )
                })
            )
        }
    }

    fun setSelectedContact(contact: Contact?) {
        setState {
            copy(selectedContact = contact)
        }
    }

    fun saveGroup(group: Group) {
        viewModelScope.launch {
            dbHelper.insertGroup(group)

            val getGroupsAsync = async { dbHelper.getGroups() }
            getGroupsAsync.await().forEach {
                Log.d("ContactsViewModel", it.toString())
            }

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

            return ContactsViewModel(
                state,
                ContactsRepository(viewModelContext.activity.applicationContext),
                dbHelper
            )
        }
    }
}