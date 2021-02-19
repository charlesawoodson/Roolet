package com.charlesawoodson.roolet.groups.adapters

import androidx.recyclerview.widget.DiffUtil
import com.charlesawoodson.roolet.db.Group

class GroupsDiffCallback(
    private val oldList: List<Group>,
    private val newList: List<Group>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].groupId == newList[newItemPosition].groupId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
