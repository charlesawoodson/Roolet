package com.charlesawoodson.roolet.contacts.diffutils

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.charlesawoodson.roolet.contacts.model.Contact
import com.charlesawoodson.roolet.contacts.model.GroupMember
import com.charlesawoodson.roolet.contacts.adapters.ContactsAdapter.Companion.SELECTED_PAYLOAD
import com.charlesawoodson.roolet.lists.SelectableListItem

class SelectedContactsDiffCallback(
    private val oldList: List<SelectableListItem<Contact>>,
    private val newList: List<SelectableListItem<Contact>>
) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].data.id == newList[newItemPosition].data.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].selected == newList[newItemPosition].selected
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        val diff = Bundle()
        diff.putBoolean(SELECTED_PAYLOAD, newList[newItemPosition].selected)

        return diff
    }

}

class GroupMemberDiffCallback(
    private val oldList: List<GroupMember>,
    private val newList: List<GroupMember>
) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}