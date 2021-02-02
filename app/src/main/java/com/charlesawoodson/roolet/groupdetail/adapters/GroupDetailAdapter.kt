package com.charlesawoodson.roolet.groupdetail.adapters

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
import com.charlesawoodson.roolet.contacts.model.GroupMember
import com.charlesawoodson.roolet.db.Group
import kotlinx.android.synthetic.main.list_item_group_detail.view.*

class GroupDetailAdapter :
    RecyclerView.Adapter<GroupDetailAdapter.ViewHolder>() {

    private val data = ArrayList<GroupMember>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_group_detail, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        if (!item.photoUri.isNullOrBlank()) {
            Glide.with(holder.context).load(item.photoUri).circleCrop()
                .into(holder.groupMemberImageView)
        } else {
            holder.groupMemberImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.context,
                    R.mipmap.ic_launcher_round
                )
            )
        }

        holder.nameTextView.text = item.name
        holder.numberTextView.text = item.number
    }

    override fun getItemCount(): Int = data.size

    fun updateData(groupMembers: List<GroupMember>) {
        data.clear()
        data.addAll(groupMembers)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = itemView.context

        val nameTextView: TextView = itemView.nameTextView
        val numberTextView: TextView = itemView.numberTextView
        val groupMemberImageView: ImageView = itemView.groupMemberImageView
    }
}