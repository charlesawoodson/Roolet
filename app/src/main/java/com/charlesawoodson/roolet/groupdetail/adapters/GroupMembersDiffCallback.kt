package com.charlesawoodson.roolet.groupdetail.adapters

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.charlesawoodson.roolet.contacts.model.GroupMember
import com.charlesawoodson.roolet.groupdetail.adapters.GroupDetailAdapter.Companion.LAST_CALLED_PAYLOAD

class GroupMembersDiffCallback(
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
        return oldList[oldItemPosition].beenCalled == newList[newItemPosition].beenCalled
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        return Bundle().apply {
            putBoolean(LAST_CALLED_PAYLOAD, newList[newItemPosition].beenCalled)
        }
    }
}
