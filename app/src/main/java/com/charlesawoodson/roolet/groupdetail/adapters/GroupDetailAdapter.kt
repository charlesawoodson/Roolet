package com.charlesawoodson.roolet.groupdetail.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.model.GroupMember
import kotlinx.android.synthetic.main.list_item_group_member.view.*

class GroupDetailAdapter :
    RecyclerView.Adapter<GroupDetailAdapter.ViewHolder>() {

    private val data = ArrayList<GroupMember>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_group_member, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        if (!item.photoUri.isNullOrBlank()) {
            Glide.with(holder.context).load(item.photoUri).circleCrop()
                .into(holder.groupMemberImageView)
        } else {
            Glide.with(holder.context).load(
                ContextCompat.getDrawable(
                    holder.context,
                    R.drawable.roolet_icon_grey
                )
            ).circleCrop().into(holder.groupMemberImageView)
        }

        holder.nameTextView.text = item.name
        holder.numberTextView.text = item.number

        holder.lastCalledTextView.isVisible = item.beenCalled
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        } else {
            val payload = payloads[0] as Bundle
            holder.lastCalledTextView.isVisible = payload.getBoolean(LAST_CALLED_PAYLOAD)
        }
    }

    override fun getItemCount(): Int = data.size

    fun updateData(groupMembers: List<GroupMember>) {
        val diffCallback = GroupMembersDiffCallback(data, groupMembers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(groupMembers)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = itemView.context

        val nameTextView: TextView = itemView.nameTextView
        val numberTextView: TextView = itemView.numberTextView
        val groupMemberImageView: ImageView = itemView.groupMemberImageView
        val lastCalledTextView: TextView = itemView.lastCalledTextView
    }

    companion object {
        const val LAST_CALLED_PAYLOAD = "last_called_payload"
    }
}