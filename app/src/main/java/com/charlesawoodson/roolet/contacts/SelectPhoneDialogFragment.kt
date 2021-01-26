package com.charlesawoodson.roolet.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
            Log.d("ContactCity", contact.toString())
            contact?.data?.phones?.forEach {
                when (it.type) {
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> {
                        mobileButton.text = "Mobile: ${it.number}"
                        mobileButton.isVisible = true
                    }
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> {
                        homeButton.text = "Home: ${it.number}"
                        homeButton.isVisible = true
                    }
                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> {
                        workButton.text = "Work: ${it.number}"
                        workButton.isVisible = true
                    }
                }
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

    }

}