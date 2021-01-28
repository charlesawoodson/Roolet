package com.charlesawoodson.roolet.contacts

data class Contact(
    val id: Long,
    val name: String,
    val photoUri: String?,
    var phones: List<Phone> = emptyList(),
    var selectedNumber: String = ""
)

data class Phone(val number: String, val type: Int)