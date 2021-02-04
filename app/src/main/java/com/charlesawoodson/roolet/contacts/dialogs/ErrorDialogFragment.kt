package com.charlesawoodson.roolet.contacts.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.args
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.mvrx.BaseDialogFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_error_dialog.*

class ErrorDialogFragment : BaseDialogFragment(gravity = Gravity.TOP, width = .9f) {

    private val arguments: ErrorDialogArgs by args()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_error_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorTitle.setText(arguments.titleRes)
        errorDescription.setText(arguments.descriptionRes)
    }

}

@Parcelize
data class ErrorDialogArgs(
    val titleRes: Int,
    val descriptionRes: Int
) : Parcelable