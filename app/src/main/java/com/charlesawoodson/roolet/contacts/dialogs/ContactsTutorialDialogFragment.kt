package com.charlesawoodson.roolet.contacts.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.charlesawoodson.roolet.R
import kotlinx.android.synthetic.main.contact_list_item.view.*
import kotlinx.android.synthetic.main.view_contacts_tutorial.*
import kotlinx.android.synthetic.main.view_groups_tutorial.dialogContainer

class ContactsTutorialDialogFragment : DialogFragment() {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_contacts_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(ContextCompat.getDrawable(requireContext(), R.drawable.roolet_icon_grey))
            .circleCrop()
            .into(contactListItem.contactImageView)

        dialogContainer.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val editor = sharedPreferences.edit()
        editor.putBoolean(getString(R.string.contacts_tutorial_seen_pref), true)
        editor.apply()
    }

}