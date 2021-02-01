package com.charlesawoodson.roolet.contacts.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Contact(
    val id: Long,
    val name: String,
    val photoUri: String?,
    var phones: List<Phone> = emptyList(),
    var selectedPhone: Phone? = null
)

data class Phone(val number: String, val type: Int)

@Parcelize
data class GroupMember(
    val id: Long,
    val name: String,
    val photoUri: String?,
    val number: String,
    val type: Int
) : Parcelable