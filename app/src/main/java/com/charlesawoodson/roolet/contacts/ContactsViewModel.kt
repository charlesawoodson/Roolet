package com.charlesawoodson.roolet.contacts

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.charlesawoodson.roolet.extensions.appendAt
import com.charlesawoodson.roolet.extensions.removeItem

data class ContactsState(
    val contacts: List<Contact> = emptyList(),
    val filteredContacts: List<Contact> = emptyList(),
    val selectedContacts: List<Contact> = emptyList(),
    val filter: String = ""
) : MvRxState

class ContactsViewModel(initialState: ContactsState) :
    BaseMvRxViewModel<ContactsState>(initialState, true) {

    var phones: Map<Long, ArrayList<String>> = HashMap()

    init {

        selectSubscribe(ContactsState::contacts, ContactsState::filter) { contacts, filter ->
            val list = contacts.filter {
                it.name.contains(filter)
            }

            setState {
                copy(filteredContacts = list)
            }
        }

    }

    fun setContacts(contacts: List<Contact>) {
        setState {
            copy(contacts = contacts)
        }
    }

    fun setFilter(filter: String) {
        setState {
            copy(filter = filter)
        }
    }

    fun addSelectedContact(contact: Contact) {
        setState {
            copy(selectedContacts = selectedContacts + contact)
        }
    }

}