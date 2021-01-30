package com.charlesawoodson.roolet.groups.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.db.Group
import kotlinx.android.synthetic.main.item_roolet_group.view.*

class GroupsAdapter(private val listener: OnGroupItemClickListener) :
    RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    private val data = ArrayList<Group>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_roolet_group, parent, false)

        return ViewHolder(view, data, listener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.groupNameTextView.text = item.title
        setGroupMembersIcon(item, holder)


    }

    private fun setGroupMembersIcon(item: Group, holder: ViewHolder) {
        for (i in 0..2) {
            when (i) {
                0 -> {
                    setGroupMemberIcon(i, item, holder.context, holder.memberOneImageView)
                }
                1 -> {
                    setGroupMemberIcon(i, item, holder.context, holder.memberTwoImageView)
                }
                2 -> {
                    setGroupMemberIcon(i, item, holder.context, holder.memberThreeImageView)
                }
            }
        }
    }

    private fun setGroupMemberIcon(i: Int, item: Group, context: Context, memberImageView: ImageView) {
        if (i < item.members.size) {
            val groupMember = item.members[i]

            if (!groupMember.photoUri.isNullOrBlank()) {
                Glide.with(context).load(groupMember.photoUri).circleCrop()
                    .into(memberImageView)
            } else {
                memberImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.mipmap.ic_launcher_round
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newGroups: List<Group>) {
        data.clear()
        data.addAll(newGroups)
        notifyDataSetChanged()
    }

    class ViewHolder(
        view: View,
        data: ArrayList<Group>,
        listener: OnGroupItemClickListener
    ) : RecyclerView.ViewHolder(view) {
        val context: Context = itemView.context

        val groupNameTextView: TextView = itemView.groupNameTextView
        val groupMembersTextView: TextView = itemView.groupMembersTextView
        val memberOneImageView: ImageView = itemView.memberOneImageView
        val memberTwoImageView: ImageView = itemView.memberTwoImageView
        val memberThreeImageView: ImageView = itemView.memberThreeImageView

        init {
            itemView.setOnClickListener {
                listener.onGroupItemClick(data[adapterPosition])
            }
        }
    }

    interface OnGroupItemClickListener {
        fun onGroupItemClick(group: Group)
    }

}