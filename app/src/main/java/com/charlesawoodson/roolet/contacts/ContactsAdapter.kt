package com.charlesawoodson.roolet.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.charlesawoodson.roolet.R
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactsAdapter() : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private val data = ArrayList<Contact>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = itemView.nameTextView

        init {
            // todo: Set click listeners for the ViewHolder's View
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = data[position].name
    }

    override fun getItemCount(): Int = data.size

    fun updateData(contacts: List<Contact>) {
        data.clear()
        data.addAll(contacts)
        notifyDataSetChanged()
    }
}