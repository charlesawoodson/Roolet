package com.charlesawoodson.roolet.groupdetail.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.airbnb.mvrx.parentFragmentViewModel
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.model.Phone
import com.charlesawoodson.roolet.groupdetail.GroupsDetailViewModel
import com.charlesawoodson.roolet.mvrx.BaseDialogFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_call_phone_dialog.*
import kotlinx.android.synthetic.main.fragment_select_phone_dialog.cancelButton

class CallPhoneDialogFragment : BaseDialogFragment(gravity = Gravity.BOTTOM, roundEdges = false) {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
    }

    private val viewModel: GroupsDetailViewModel by parentFragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_call_phone_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setWindowAnimations(R.style.DialogAnimation)

        val randomNumber = viewModel.getRandomNumber()
        callButton.text = "Call +1 $randomNumber"

        callButton.setOnClickListener {
            if (!sharedPreferences.getBoolean(getString(R.string.repeat_calls_pref), false)) {
                viewModel.removeNumber(randomNumber)
            }
            // todo: update groupItem with call time

            Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$randomNumber") // todo: fix probably
                ContextCompat.startActivity(requireContext(), this, null)
            }
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}

@Parcelize
data class SelectPhoneArgs(
    val contactId: Long,
    val phones: List<Phone>
) : Parcelable


