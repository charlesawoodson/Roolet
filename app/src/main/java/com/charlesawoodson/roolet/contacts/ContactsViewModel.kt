package com.charlesawoodson.roolet.contacts

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.charlesawoodson.roolet.extensions.appendAt
import com.charlesawoodson.roolet.extensions.removeItem
import com.charlesawoodson.roolet.extensions.updateItems
import com.charlesawoodson.roolet.lists.SelectableListItem

data class ContactsState(
    val contacts: List<SelectableListItem<Contact>> = emptyList(),
    val filteredContacts: List<SelectableListItem<Contact>> = emptyList(),
    val selectedContacts: List<Contact> = emptyList(),
    val filter: String = ""
) : MvRxState

class ContactsViewModel(initialState: ContactsState) :
    BaseMvRxViewModel<ContactsState>(initialState, true) {

    var phones: Map<Long, ArrayList<String>> = HashMap()

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

}