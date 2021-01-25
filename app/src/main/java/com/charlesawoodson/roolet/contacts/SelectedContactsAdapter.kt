package com.charlesawoodson.roolet.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        val item = data[position]
        holder.nameTextView.text = data[position].name
        if (!item.photo.isNullOrBlank()) {
            Glide.with(holder.context).load(item.photo).circleCrop().into(holder.contactImageView)
        } else {
            holder.contactImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.context,
                    R.mipmap.ic_launcher_round
                )
            )
        }
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
        val context: Context = itemView.context
        val nameTextView: TextView = itemView.selectedContactNameTextView
        val contactImageView: ImageView = itemView.selectedContactImageView
    }

}