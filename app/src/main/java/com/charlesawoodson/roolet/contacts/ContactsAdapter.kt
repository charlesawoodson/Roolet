package com.charlesawoodson.roolet.contacts

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
import com.charlesawoodson.roolet.lists.SelectableListItem
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactsAdapter(private val listener: OnContactsItemClickListener) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private val data = ArrayList<SelectableListItem<Contact>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)

        return ViewHolder(view, listener, data)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position].data
        holder.nameTextView.text = item.name
        holder.checkImageView.isVisible = data[position].selected

        if (!item.photoUri.isNullOrBlank()) {
            Glide.with(holder.context).load(item.photoUri).circleCrop().into(holder.contactImageView)
        } else {
            holder.contactImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.context,
                    R.mipmap.ic_launcher_round
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        } else {
            val test = payloads[0] as Bundle
            holder.checkImageView.isVisible = test.getBoolean(SELECTED_PAYLOAD)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long {
        return data[position].data.id
    }

    fun updateData(newContacts: List<SelectableListItem<Contact>>) {
        val diffCallback = SelectedContactsDiffCallback(data, newContacts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(newContacts)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(
        view: View,
        listener: OnContactsItemClickListener,
        data: ArrayList<SelectableListItem<Contact>>
    ) :
        RecyclerView.ViewHolder(view) {

        val context: Context = itemView.context

        val nameTextView: TextView = itemView.nameTextView
        val checkImageView: ImageView = itemView.selectedImageView
        val contactImageView: ImageView = itemView.contactImageView

        init {
            itemView.setOnClickListener {
                listener.toggleSelection(data[adapterPosition])
            }
        }
    }


    interface OnContactsItemClickListener {
        fun toggleSelection(contact: SelectableListItem<Contact>)
    }

    companion object {
        const val SELECTED_PAYLOAD = "selected"
    }
}