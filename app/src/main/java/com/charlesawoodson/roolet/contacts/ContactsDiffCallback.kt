package com.charlesawoodson.roolet.contacts

import androidx.recyclerview.widget.DiffUtil

class ContactsDiffCallback(private val oldList: List<Contact>, private val newList: List<Contact>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (_, name, number, photo) = oldList[oldItemPosition]
        val (_, name1, number1, photo1) = newList[newItemPosition]

        return name == name1 && number == number1 && photo == photo1
    }

}