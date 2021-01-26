package com.charlesawoodson.roolet.mvrx

import androidx.fragment.app.DialogFragment
import com.airbnb.mvrx.MvRxView

open class BaseDialogFragment(override val mvrxViewId: String = "") : DialogFragment(), MvRxView {

    /**
     * Override this to handle any state changes from MvRxViewModels created through MvRx Fragment delegates.
     */
    override fun invalidate() {

    }
}