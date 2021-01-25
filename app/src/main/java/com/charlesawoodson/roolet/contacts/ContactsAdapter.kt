package com.charlesawoodson.roolet.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.charlesawoodson.roolet.R
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactsAdapter(private val listener: OnContactsItemClickListener) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private val data = ArrayList<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)

        return ViewHolder(view, listener, data)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = data[position].name
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newContacts: List<Contact>) {
        val diffCallback = ContactsDiffCallback(data, newContacts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(newContacts)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View, listener: OnContactsItemClickListener, data: ArrayList<Contact>) :
        RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = itemView.nameTextView

        init {
            itemView.setOnClickListener {
                listener.toggleSelection(data[adapterPosition])
            }
        }
    }


    interface OnContactsItemClickListener {
        fun toggleSelection(contact: Contact)
    }
}