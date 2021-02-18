package com.charlesawoodson.roolet.contacts.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.airbnb.mvrx.args
import com.airbnb.mvrx.parentFragmentViewModel
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.ContactsViewModel
import com.charlesawoodson.roolet.contacts.model.Phone
import com.charlesawoodson.roolet.mvrx.BaseDialogFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_select_phone_dialog.*

class SelectPhoneDialogFragment : BaseDialogFragment(gravity = Gravity.BOTTOM) {

    // todo: store args in view model | remove this
    private val arguments: SelectPhoneArgs by args()

    private val viewModel: ContactsViewModel by parentFragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_phone_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setWindowAnimations(R.style.DialogAnimation)

        buttonsLinearLayout.removeAllViews()
        arguments.phones.forEach { phone ->
            addPhoneNumberButton(phone)
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addPhoneNumberButton(phone: Phone) {
        val type =
            ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, phone.type, "NONE")

        val button = Button(context)

        button.text = "$type: ${phone.number}"
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        button.backgroundTintList =
            AppCompatResources.getColorStateList(requireContext(), R.color.phone_button_color_state)

        button.setOnClickListener {
            viewModel.setSelectedPhone(arguments.contactId, phone)
            dismiss()
        }

        buttonsLinearLayout.addView(button)
    }
}

@Parcelize
data class SelectPhoneArgs(
    val contactId: Long,
    val phones: List<Phone>
) : Parcelable


