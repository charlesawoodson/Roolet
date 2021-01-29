package com.charlesawoodson.roolet.contacts

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.charlesawoodson.roolet.DatabaseHelperImpl
import com.charlesawoodson.roolet.User
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.extensions.updateItems
import com.charlesawoodson.roolet.lists.SelectableListItem
import com.charlesawoodson.roolet.repository.ContactsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class ContactsState(
    val contacts: List<SelectableListItem<Contact>> = emptyList(),
    val filteredContacts: List<SelectableListItem<Contact>> = emptyList(),
    val selectedContacts: List<Contact> = emptyList(),
    val selectedContact: Contact? = null,
    val filter: String = ""
) : MvRxState

class ContactsViewModel(
    initialState: ContactsState,
    private val contactsRepository: ContactsRepository,
    private val dbHelper: DatabaseHelperImpl
) : BaseMvRxViewModel<ContactsState>(initialState, true) {

    init {
        getAllContactsFromRoom()
        // fetchContacts()

        selectSubscribe(ContactsState::contacts, ContactsState::filter) { contacts, filter ->
            val filteredList = contacts.filter { it.data.name.contains(filter) }

            val selectedList = contacts
                .filter { it.selected }
                .map { it.data }

            setState {
                copy(filteredContacts = filteredList, selectedContacts = selectedList)
            }
        }
    }

    private fun getAllContactsFromRoom() {
        viewModelScope.launch {
            dbHelper.updateSelectedNumberByContactId(3, "999-133-4878")
            val testAsync = async { dbHelper.getUsers() }
            val test = testAsync.await()

            test.forEach {
                Log.d("getAllContactsFrom()", it.toString())
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

            dbHelper.insertAll(contacts.filter { it.phones.isNotEmpty() }.map {
                User(
                    it.id,
                    it.name,
                    it.photoUri,
                    "it.selectedNumber"
                )
            })

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
                    copy(selected = !selected)
                })
            )
        }
    }

    fun addSelectedContact(contact: Contact) {
        setState {
            copy(
                contacts = contacts.updateItems(
                    { it.data.id == contact.id },
                    { copy(selected = true) }
                )
            )
        }
    }

    fun setSelectedContact(contact: Contact?) {
        setState {
            copy(selectedContact = contact)
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