package com.charlesawoodson.roolet.contacts

import com.airbnb.mvrx.*
import com.charlesawoodson.roolet.extensions.updateItems
import com.charlesawoodson.roolet.lists.SelectableListItem

data class ContactsState(
    val contacts: List<SelectableListItem<Contact>> = emptyList(),
    val filteredContacts: List<SelectableListItem<Contact>> = emptyList(),
    val selectedContacts: List<Contact> = emptyList(),
    val selectedContact: Contact? = null,
    val filter: String = ""
) : MvRxState

class ContactsViewModel(initialState: ContactsState) :
    BaseMvRxViewModel<ContactsState>(initialState, true) {

    var phones: Map<Long, ArrayList<Phone>> = HashMap()

    init {

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

    fun setContacts(contacts: List<SelectableListItem<Contact>>) {
        setState {
            copy(contacts = contacts)
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

}