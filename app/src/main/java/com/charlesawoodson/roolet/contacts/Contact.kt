package com.charlesawoodson.roolet.contacts

data class Contact(val id: Long, val name: String, val phones: List<Phone>, val photo: String?, val selectedNumber: String = "")

data class Phone(val number: String, val type: Int)
