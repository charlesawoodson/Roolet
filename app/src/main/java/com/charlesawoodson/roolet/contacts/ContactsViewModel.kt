package com.charlesawoodson.roolet.contacts

import com.airbnb.mvrx.*

data class ContactsState(val contacts: Async<List<Contact>> = Uninitialized) : MvRxState

class ContactsViewModel(initialState: ContactsState) :
    BaseMvRxViewModel<ContactsState>(initialState, true) {

    var phones: Map<Long, ArrayList<String>> = HashMap()

    init {

    }

    fun setContacts(contacts: List<Contact>) {
        setState {
            copy(contacts = Success(contacts))
        }
    }

}