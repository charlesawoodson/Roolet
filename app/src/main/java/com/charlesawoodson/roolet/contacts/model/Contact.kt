package com.charlesawoodson.roolet.contacts.model

data class Contact(
    val id: Long,
    val name: String,
    val photoUri: String?,
    var phones: List<Phone> = emptyList(),
    var selectedPhone: Phone? = null
)

data class Phone(val number: String, val type: Int)

data class GroupMember(val id: Long, val name: String, val photoUri: String?, val number: String, val type: Int)