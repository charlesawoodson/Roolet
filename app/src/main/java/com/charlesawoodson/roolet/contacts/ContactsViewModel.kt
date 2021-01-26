package com.charlesawoodson.roolet.contacts

import com.airbnb.mvrx.*
import com.charlesawoodson.roolet.extensions.updateItems
import com.charlesawoodson.roolet.lists.SelectableListItem

data class ContactsState(
    val contacts: List<SelectableListItem<Contact>> = emptyList(),
    val filteredContacts: List<SelectableListItem<Contact>> = emptyList(),
    val selectedContacts: List<Contact> = emptyList(),
    val selectedContact: SelectableListItem<Contact>? = null,
    val filter: String = ""
) : MvRxState

class ContactsViewModel(initialState: ContactsState) :
    BaseMvRxViewModel<ContactsState>(initialState, true) {

    var phones: Map<Long, ArrayList<Phone>> = HashMap()
    var typeToPhoneMap = mutableMapOf<Int, String>()

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

        selectSubscribe(ContactsState::selectedContact) { selectedContact ->
            selectedContact?.data?.phones?.forEach {
                typeToPhoneMap[it.type] = it.number
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

    fun setSelectedContact(contact: SelectableListItem<Contact>?) {
        setState {
            copy(selectedContact = contact)
        }
    }

}