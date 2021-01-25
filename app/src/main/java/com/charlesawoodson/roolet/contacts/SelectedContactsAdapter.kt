package com.charlesawoodson.roolet.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.charlesawoodson.roolet.R
import kotlinx.android.synthetic.main.selected_contact_list_item.view.*

class SelectedContactsAdapter() :
    RecyclerView.Adapter<SelectedContactsAdapter.ViewHolder>() {

    private val data = ArrayList<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.selected_contact_list_item, parent, false)

        return ViewHolder(view)
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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = itemView.selectedContactNameTextView
        val contactImageView: ImageView = itemView.selectedContactImageView

    }

}