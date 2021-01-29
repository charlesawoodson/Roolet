package com.charlesawoodson.roolet.contacts

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import com.airbnb.mvrx.parentFragmentViewModel
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.mvrx.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_select_phone_dialog.*

class SelectPhoneDialogFragment() : BaseDialogFragment(gravity = Gravity.BOTTOM) {

    private val viewModel: ContactsViewModel by parentFragmentViewModel()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectSubscribe(ContactsState::selectedContact) { contact ->
            buttonsLinearLayout.removeAllViews()
            contact?.phones?.forEach { phone ->
                addPhoneNumberButton(phone, contact)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_phone_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addPhoneNumberButton(phone: Phone, contact: Contact?) {
        val type =
            ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, phone.type, "NONE")

        val button = Button(context)
        button.backgroundTintList =
            AppCompatResources.getColorStateList(requireContext(), R.color.phone_button_color_state)
        button.text = "$type: ${phone.number}"

        button.setOnClickListener {
            if (contact != null) {
                viewModel.addSelectedContact(contact.copy(selectedPhone = phone))
            }
            dismiss()
        }

        buttonsLinearLayout.addView(button)
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.setSelectedContact(null)
        super.onDismiss(dialog)
    }

}